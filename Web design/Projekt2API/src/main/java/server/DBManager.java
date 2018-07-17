package src.main.java.server;

import java.beans.PropertyVetoException;
import java.sql.*;
import java.util.ArrayList;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.dbutils.DbUtils;
import server.exceptions.RequestException;


public class DBManager {

    private static Logger logger = LogManager.getLogger(DBManager.class);
    private ComboPooledDataSource cpds = new ComboPooledDataSource();

    /**
     * Set up JDBC connection pool at startup
     */
    public DBManager() {
        try {
            cpds.setDriverClass("com.mysql.cj.jdbc.Driver"); //loads the jdbc driver
        } catch (PropertyVetoException e) {
            logger.error(e);
            e.printStackTrace();
        }
        cpds.setJdbcUrl(UserConfig.DB_URL);
        cpds.setUser(UserConfig.DB_USER);
        cpds.setPassword(UserConfig.DB_PASSWORD);
        cpds.setInitialPoolSize(10);
        cpds.setMinPoolSize(5);
        cpds.setAcquireIncrement(5);
        cpds.setMaxPoolSize(100);
        cpds.setMaxConnectionAge(7200);
    }


    /**
     * Insert new Theme into the database
     *
     * @param theme new theme
     */
    public void saveTheme(Theme theme) throws RequestException {
        PreparedStatement stmt = null;
        Connection connection = null;
        try {
            connection = cpds.getConnection();
            stmt = connection.prepareStatement("INSERT INTO theme (theme) VALUES (?)");
            stmt.setString(1, theme.getTheme());
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error(e);
            if (e instanceof SQLIntegrityConstraintViolationException) {
                if (e.getMessage().startsWith("Duplicate entry")) {
                    throw new RequestException(Constants.HTTP_BAD_REQUEST, "Duplicate entry for theme " + theme.getTheme());
                }
            }
        } finally {
            DbUtils.closeQuietly(stmt);
            DbUtils.closeQuietly(connection);
        }
    }

    /**
     * Insert new question into the database
     *
     * @param question new question
     * @return questionID, the id the question got in the database. -1 if something went wrong
     */
    public int saveNewQuestion(Question question) {
        Connection connection = null;
        PreparedStatement stmtQuestion = null;
        PreparedStatement stmtAnswers = null;
        PreparedStatement stmtMedia = null;
        ResultSet result = null;
        int questionID = -1;
        try {
            connection = cpds.getConnection();
            connection.setAutoCommit(false);
            stmtQuestion = connection.prepareStatement("INSERT INTO question (the_question, correct_answer, theme) VALUES (?, ?,(select theme_id from theme where theme = ?))", Statement.RETURN_GENERATED_KEYS);
            stmtQuestion.setString(1, question.getQuestion());
            stmtQuestion.setString(2, question.getCorrectAnswer());
            stmtQuestion.setString(3, question.getTheme());
            stmtQuestion.executeUpdate();
            result = stmtQuestion.getGeneratedKeys();
            while (result.next()) {
                questionID = result.getInt(1);
            }
            // for every wrong answer
            for (String incorrectAnswer : question.getIncorrectAnswers()) {
                stmtAnswers = connection.prepareStatement("INSERT INTO wrong_answer VALUES (LAST_INSERT_ID(), ?)");
                stmtAnswers.setString(1, incorrectAnswer);
                stmtAnswers.executeUpdate();
            }
            // Media
            if (question.getMediaFileName() != null) {
                stmtMedia = connection.prepareStatement("INSERT INTO media (media_type, file_name, question_id) VALUES (?,?,LAST_INSERT_ID())");
                stmtMedia.setString(1, question.getMediaType());
                stmtMedia.setString(2, question.getMediaFileName());
                stmtMedia.executeUpdate();
            }
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    logger.error("Transaction is being rolled back");
                    questionID = -1;
                    logger.error(e);
                    connection.rollback();
                } catch (SQLException e1) {
                    logger.error(e1);
                }
            }
        } finally {
            DbUtils.closeQuietly(stmtMedia);
            DbUtils.closeQuietly(stmtQuestion);
            DbUtils.closeQuietly(stmtAnswers);
            DbUtils.closeQuietly(result);
            DbUtils.closeQuietly(connection);
        }
        return questionID;
    }

    /**
     * Selects all from theme table
     *
     * @return ArrayList<Theme>
     */
    public ArrayList<Theme> getAllThemes() {
        Connection connection = null;
        ArrayList<Theme> themeList = new ArrayList<>();
        PreparedStatement stmt = null;
        ResultSet result = null;
        try {
            connection = cpds.getConnection();
            stmt = connection
                    .prepareStatement("SELECT * FROM theme LEFT JOIN (SELECT * from (SELECT theme AS theme_name, COUNT(theme) AS nbrOfQuestions FROM question GROUP BY theme) AS nbr ) AS Nbr ON theme_name = theme_id");
            result = stmt.executeQuery();
            while (result.next()) {
                Theme theme = new Theme(result.getString(2), result.getInt(1), result.getInt(4));
                themeList.add(theme);
            }
        } catch (SQLException e) {
            logger.error(e);
        } finally {
            DbUtils.closeQuietly(stmt);
            DbUtils.closeQuietly(result);
            DbUtils.closeQuietly(connection);
        }
        return themeList;
    }

    /**
     * Selects theme with minimum number of questions
     *
     * @return ArrayList<Theme>
     */
    public ArrayList<Theme> getThemes(int minNbrQuestions, int nbrOfThemes) {
        Connection connection = null;
        ArrayList<Theme> themeList = new ArrayList<>();
        PreparedStatement stmt = null;
        ResultSet result = null;
        String endOFQuery = "WHERE nbrOfQuestions IS NOT NULL";
        if (minNbrQuestions == 0) {
            endOFQuery = " ";
        }
        try {
            connection = cpds.getConnection();
            stmt = connection
                    .prepareStatement("SELECT * FROM theme LEFT JOIN (SELECT * from (SELECT theme AS theme_name, COUNT(theme) AS nbrOfQuestions FROM question GROUP BY theme) AS nbr WHERE nbrOfQuestions >= ?) AS Nbr ON theme_name = theme_id " + endOFQuery + " LIMIT ?");
            stmt.setInt(1, minNbrQuestions);
            stmt.setInt(2, nbrOfThemes);
            result = stmt.executeQuery();
            while (result.next()) {
                Theme theme = new Theme(result.getString(2), result.getInt(1), result.getInt(4));
                themeList.add(theme);
            }
        } catch (SQLException e) {
            logger.error(e);
        } finally {
            DbUtils.closeQuietly(stmt);
            DbUtils.closeQuietly(result);
            DbUtils.closeQuietly(connection);
        }
        return themeList;
    }

    /**
     * Selects all question with a certain theme
     *
     * @param theme - choosen theme
     * @return ArrayList<questions>
     */
    public ArrayList<Question> getThemeQuestions(String theme) {
        Connection connection = null;
        ArrayList<Question> questionList = new ArrayList<>();
        PreparedStatement stmt = null;
        ResultSet result = null;
        try {
            connection = cpds.getConnection();
            stmt = connection
                    .prepareStatement("SELECT question_id FROM question WHERE theme = (SELECT theme_id FROM theme WHERE theme = ?)");
            stmt.setString(1, theme);
            result = stmt.executeQuery();
            while (result.next()) {
                Question question = getQuestion(result.getString(1));
                if (!question.getId().equals("-1")) {
                    questionList.add(question);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
        } finally {
            DbUtils.closeQuietly(stmt);
            DbUtils.closeQuietly(result);
            DbUtils.closeQuietly(connection);
        }
        return questionList;
    }

    /**
     * Selects specific number of questions from a certain theme
     *
     * @param theme          - choosen theme
     * @param nbrOfQuestions - number of questions to return
     * @return ArrayList<Question>
     */
    public ArrayList<Question> getThemeQuestions(String theme, int nbrOfQuestions, int indexStart) {
        Connection connection = null;
        ArrayList<Question> questionList = new ArrayList<>();
        PreparedStatement stmt = null;
        ResultSet result = null;
        try {
            connection = cpds.getConnection();
            stmt = connection
                    .prepareStatement("SELECT question_id FROM question WHERE theme = (SELECT theme_id FROM theme WHERE theme = ?) LIMIT ? OFFSET ?");
            stmt.setString(1, theme);
            stmt.setInt(2, nbrOfQuestions);
            stmt.setInt(3, indexStart);
            result = stmt.executeQuery();
            while (result.next()) {
                Question question = getQuestion(result.getString(1));
                if (!question.getId().equals("-1")) {
                    questionList.add(question);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
        } finally {
            DbUtils.closeQuietly(stmt);
            DbUtils.closeQuietly(result);
            DbUtils.closeQuietly(connection);
        }
        return questionList;
    }

    /**
     * Select one question with certain ID, and all its attributes
     *
     * @param question_ID choosen question's ID
     * @return Question
     */
    public Question getQuestion(String question_ID) {
        Connection connection = null;
        Boolean firstResult = true;
        Question theQuestion = new Question();
        ArrayList<String> incorrectAnswers = new ArrayList<>();
        PreparedStatement stmt = null;
        ResultSet result = null;
        try {
            connection = cpds.getConnection();
            stmt = connection
                    .prepareStatement("SELECT question.question_id, the_question, correct_answer, question_theme.theme, wrong_answer, media_type, file_name FROM question LEFT JOIN (SELECT * FROM theme) AS question_theme ON question.theme = question_theme.theme_id LEFT JOIN (SELECT * FROM wrong_answer) AS wrong ON question.question_id = wrong.question_id LEFT JOIN (SELECT * FROM media) AS question_media ON question.question_id = question_media.question_id WHERE question.question_id = ?");
            stmt.setString(1, question_ID);
            result = stmt.executeQuery();
            while (result.next()) {
                if (firstResult) {
                    theQuestion.setId(result.getString(1));
                    theQuestion.setQuestion(result.getString(2));
                    theQuestion.setCorrectAnswer(result.getString(3));
                    theQuestion.setTheme(result.getString(4));
                    theQuestion.setMediaType(result.getString(6));
                    theQuestion.setMediaFileName(result.getString(7));
                }
                incorrectAnswers.add(result.getString(5));
                firstResult = false;
            }
            theQuestion.setIncorrectAnswers(incorrectAnswers);
        } catch (SQLException e) {
            logger.error(e);
            theQuestion.setId("-1");
        } finally {
            DbUtils.closeQuietly(stmt);
            DbUtils.closeQuietly(result);
            DbUtils.closeQuietly(connection);
        }
        return theQuestion;
    }

    /**
     * Get specific nbr of random questions with a certain theme, from the database
     *
     * @return random question
     */
    public ArrayList<Question> getRandomQuestion(String theme, int nbrOfRandom) {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet result = null;
        ArrayList<Question> questions = new ArrayList<>();
        try {
            connection = cpds.getConnection();
            stmt = connection
                    .prepareStatement("SELECT question_id FROM question WHERE theme = (SELECT theme_id FROM theme WHERE theme = ?) ORDER BY RAND() LIMIT ?");
            stmt.setString(1, theme);
            stmt.setInt(2, nbrOfRandom);
            result = stmt.executeQuery();
            while (result.next()) {
                questions.add(getQuestion(result.getString(1)));
            }
        } catch (SQLException e) {
            logger.error(e);
        } finally {
            DbUtils.closeQuietly(stmt);
            DbUtils.closeQuietly(result);
            DbUtils.closeQuietly(connection);
        }
        return questions;
    }

    /**
     * Get specific nbr of random questions from the database
     *
     * @return random question
     */
    public ArrayList<Question> getRandomQuestion(int nbrOfRandom) {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet result = null;
        ArrayList<Question> questions = new ArrayList<>();
        try {
            connection = cpds.getConnection();
            stmt = connection
                    .prepareStatement("SELECT question_id FROM question ORDER BY RAND() LIMIT ?");
            stmt.setInt(1, nbrOfRandom);
            result = stmt.executeQuery();
            while (result.next()) {
                questions.add(getQuestion(result.getString(1)));
            }
        } catch (SQLException e) {
            logger.error(e);
        } finally {
            DbUtils.closeQuietly(stmt);
            DbUtils.closeQuietly(result);
            DbUtils.closeQuietly(connection);
        }
        return questions;
    }

    /**
     * Delete one question from the database
     *
     * @param questionID question to delete
     */
    public void deleteQuestion(String questionID) {
        Connection connection = null;
        PreparedStatement stmtQuestion = null;
        PreparedStatement stmtAnswers = null;
        PreparedStatement stmtMedia = null;
        try {
            connection = cpds.getConnection();
            connection.setAutoCommit(false);
            // Media
            stmtMedia = connection.prepareStatement("DELETE FROM media WHERE question_id = ?");
            stmtMedia.setString(1, questionID);
            stmtMedia.executeUpdate();
            // wrong answers
            stmtAnswers = connection.prepareStatement("DELETE FROM wrong_answer WHERE question_id = ?");
            stmtAnswers.setString(1, questionID);
            stmtAnswers.executeUpdate();
            // Question
            stmtQuestion = connection.prepareStatement("DELETE FROM question WHERE question_id = ?");
            stmtQuestion.setString(1, questionID);
            stmtQuestion.executeUpdate();

            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    logger.error("Transaction is being rolled back");
                    logger.error(e);
                    connection.rollback();
                    throw new RequestException(Constants.HTTP_BAD_REQUEST, "Delete question with question id: " + questionID + " failed");
                } catch (SQLException e2) {
                    logger.error(e2);
                } catch (RequestException e1) {
                    e1.printStackTrace();
                }
            }
        } finally {
            DbUtils.closeQuietly(stmtMedia);
            DbUtils.closeQuietly(stmtQuestion);
            DbUtils.closeQuietly(stmtAnswers);
            DbUtils.closeQuietly(connection);
        }
    }

    /**
     * Delete theme and all it's related questions
     *
     * @param theme the theme to delete
     */
    public ArrayList<Question> deleteTheme(String theme) {
        Connection connection = null;
        ArrayList<Question> questionList = getThemeQuestions(theme);
        for (Question question : questionList) {
            deleteQuestion(question.getId());
        }
        PreparedStatement stmt = null;
        try {
            connection = cpds.getConnection();
            stmt = connection.prepareStatement("DELETE FROM theme WHERE theme = ?");
            stmt.setString(1, theme);
            stmt.executeUpdate();
        } catch (SQLException e) {
            try {
                throw new RequestException(Constants.HTTP_BAD_REQUEST, "Delete theme: " + theme + " failed");
            } catch (RequestException e1) {
                e1.printStackTrace();
            }
            logger.error(e);
        } finally {
            DbUtils.closeQuietly(stmt);
            DbUtils.closeQuietly(connection);
        }
        return questionList;
    }

    /**
     * Update Theme
     *
     * @param oldTheme - theme to update
     * @param newTheme - theme after update
     */
    public void updateTheme(String oldTheme, String newTheme) throws RequestException {
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            connection = cpds.getConnection();
            stmt = connection.prepareStatement("UPDATE theme SET theme = ? WHERE theme = ?");
            stmt.setString(1, newTheme);
            stmt.setString(2, oldTheme);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error(e);
            if (e instanceof SQLIntegrityConstraintViolationException) {
                if (e.getMessage().startsWith("Duplicate entry")) {
                    throw new RequestException(Constants.HTTP_BAD_REQUEST,
                            String.format("Cannot update %s to %s. Will become Duplicate entry", oldTheme, newTheme));
                }
            }
        } finally {
            DbUtils.closeQuietly(connection);
            DbUtils.closeQuietly(stmt);
        }
    }

    /**
     * @param question to update and all its attributes
     * @return questionId of updated question, or -1 if something went wrong
     */
    public int updateQuestion(Question question) {
        Connection connection = null;
        PreparedStatement stmtQuestion = null;
        PreparedStatement stmtAnswers = null;
        PreparedStatement stmtMedia = null;
        int questionID = -1;
        try {
            connection = cpds.getConnection();
            connection.setAutoCommit(false);
            stmtQuestion = connection.prepareStatement("UPDATE question SET the_question = ?, correct_answer = ?, theme = (select theme_id from theme where theme = ?) WHERE question_id = ?");
            stmtQuestion.setString(1, question.getQuestion());
            stmtQuestion.setString(2, question.getCorrectAnswer());
            stmtQuestion.setString(3, question.getTheme());
            stmtQuestion.setInt(4, Integer.parseInt(question.getId()));
            stmtQuestion.executeUpdate();

            // for every wrong answer
            stmtAnswers = connection.prepareStatement("DELETE FROM wrong_answer WHERE question_id = ?");
            stmtAnswers.setInt(1, Integer.parseInt(question.getId()));
            stmtAnswers.executeUpdate();
            for (String incorrectAnswer : question.getIncorrectAnswers()) {
                stmtAnswers = connection.prepareStatement("INSERT INTO wrong_answer VALUES (?, ?)");
                stmtAnswers.setInt(1, Integer.parseInt(question.getId()));
                stmtAnswers.setString(2, incorrectAnswer);
                stmtAnswers.executeUpdate();
            }
            // Media
               // If update isn't null and update isn't delete and there is no media in DB
            if (question.getMediaFileName() != null && !question.getMediaFileName().equals(Constants.MEDIA_DELETE)&& ifMediaExist(question.getId()) == false) {
                stmtMedia = connection.prepareStatement("INSERT INTO media (media_type, file_name, question_id) VALUES (?,?,?)");
                stmtMedia.setString(1, question.getMediaType());
                stmtMedia.setString(2, question.getMediaFileName());
                stmtMedia.setInt(3, Integer.parseInt(question.getId()));
                stmtMedia.executeUpdate();
                // Delete existing media
            } else if (question.getMediaFileName() != null && question.getMediaFileName().equals(Constants.MEDIA_DELETE)) {
                stmtMedia = connection.prepareStatement("DELETE FROM media WHERE question_id = ?");
                stmtMedia.setInt(1, Integer.parseInt(question.getId()));
                stmtMedia.executeUpdate();
                //  Update existing media
            } else if (question.getMediaFileName() != null) {
                stmtMedia = connection.prepareStatement("UPDATE media SET media_type = ?, file_name = ? WHERE question_id = ?");
                stmtMedia.setString(1, question.getMediaType());
                stmtMedia.setString(2, question.getMediaFileName());
                stmtMedia.setInt(3, Integer.parseInt(question.getId()));
                stmtMedia.executeUpdate();
            }
            questionID = Integer.parseInt(question.getId());
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    logger.error("Transaction is being rolled back", e);
                    questionID = -1;
                    logger.error(e);
                    connection.rollback();
                } catch (SQLException e1) {
                    logger.error(e1);
                }
            }
        } finally {
            DbUtils.closeQuietly(stmtMedia);
            DbUtils.closeQuietly(stmtQuestion);
            DbUtils.closeQuietly(stmtAnswers);
            DbUtils.closeQuietly(connection);
        }
        return questionID;
    }

    /**
     * Check if question has media object in DB
     * @param id question id
     * @return boolean
     */
    private boolean ifMediaExist(String id) {
        Connection connection = null;
        boolean isMedia = false;
        PreparedStatement stmt = null;
        ResultSet result = null;
        try {
            connection = cpds.getConnection();
            stmt = connection.prepareStatement("SELECT media_type FROM media where question_id = ?");
            stmt.setString(1, id);
            result = stmt.executeQuery();
            while (result.next()) {
                isMedia = true;
            }
        } catch (SQLException e) {
            logger.error(e);
            isMedia = false;
        } finally {
            DbUtils.closeQuietly(stmt);
            DbUtils.closeQuietly(result);
            DbUtils.closeQuietly(connection);
        }
        return isMedia;
    }

    /**
     * Save new user
     *
     * @param email       user mail
     * @param key         api key
     * @param description what the user needs the api for
     */
    public void saveNewUser(String email, String key, String description) {
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            connection = cpds.getConnection();
            stmt = connection.prepareStatement("INSERT INTO User VALUES (?,?,?)");
            stmt.setString(1, email);
            stmt.setString(2, key);
            stmt.setString(3, description);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error(e);
        } finally {
            DbUtils.closeQuietly(stmt);
            DbUtils.closeQuietly(connection);
        }
    }

    /**
     * Saves a new API admin
     *
     * @param userName String
     * @param email    String
     * @param passWord String
     */
    public int saveNewAdmin(String userName, String email, String passWord) {
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            connection = cpds.getConnection();
            stmt = connection.prepareStatement("INSERT INTO admin VALUES (?,?,?)");
            stmt.setString(1, userName);
            stmt.setString(2, email);
            stmt.setString(3, passWord);
            stmt.executeUpdate();
            logger.info("New admin added: " + userName);
        } catch (SQLIntegrityConstraintViolationException e) {
            logger.error("error saving new admin", e);
            return -2;
        } catch (SQLException e) {
            logger.error("error saving new admin", e);
            return -1;
        } finally {
            DbUtils.closeQuietly(stmt);
            DbUtils.closeQuietly(connection);
        }
        return 0;
    }


    /**
     * Get all users in the database
     *
     * @return userList ArrayList with all users
     */
    public ArrayList<User> getAllUsers() {
        Connection connection = null;
        ArrayList<User> userList = new ArrayList<>();
        PreparedStatement stmt = null;
        ResultSet result = null;
        try {
            connection = cpds.getConnection();
            stmt = connection.prepareStatement("SELECT * FROM user");
            result = stmt.executeQuery();
            while (result.next()) {
                User user = new User(result.getString(1), result.getString(2), result.getString(3));
                userList.add(user);
            }
        } catch (SQLException e) {
            logger.error(e);
        } finally {
            DbUtils.closeQuietly(stmt);
            DbUtils.closeQuietly(result);
            DbUtils.closeQuietly(connection);
        }
        return userList;
    }

    /**
     * Returns a password related to the username parameter. Returns empty String
     * if the username is not found
     *
     * @param username
     * @return password String
     */
    public String getPassword(String username) {
        Connection connection = null;
        String passWord = "";
        PreparedStatement stmt = null;
        ResultSet result = null;
        try {
            connection = cpds.getConnection();
            stmt = connection.prepareStatement("SELECT password FROM admin where username = ?");
            stmt.setString(1, username);
            result = stmt.executeQuery();
            while (result.next()) {
                passWord = result.getString(1);
            }
        } catch (SQLException e) {
            logger.error("could not get password", e);
        } finally {
            DbUtils.closeQuietly(stmt);
            DbUtils.closeQuietly(result);
            DbUtils.closeQuietly(connection);
        }
        return passWord;
    }

    /**
     * Delete user object from the database
     *
     * @param email user to delete
     */
    public void deleteUser(String email) {
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            connection = cpds.getConnection();
            stmt = connection.prepareStatement("DELETE FROM user WHERE email = ?");
            stmt.setString(1, email);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error(e);
        } finally {
            DbUtils.closeQuietly(stmt);
            DbUtils.closeQuietly(connection);
        }
    }

    /**
     * Check if key exist in database
     *
     * @param key to check
     * @return true if key exist in database
     */
    public boolean ifKeyExist(String key) {
        Connection connection = null;
        boolean isKey = false;
        PreparedStatement stmt = null;
        ResultSet result = null;
        try {
            connection = cpds.getConnection();
            stmt = connection.prepareStatement("SELECT api_key FROM user where api_key = ?");
            stmt.setString(1, key);
            result = stmt.executeQuery();
            while (result.next()) {
                if (result.getString(1).equals(key)) {
                    isKey = true;
                }
            }
        } catch (SQLException e) {
            logger.error(e);
        } finally {
            DbUtils.closeQuietly(stmt);
            DbUtils.closeQuietly(result);
            DbUtils.closeQuietly(connection);
        }
        return isKey;
    }

}