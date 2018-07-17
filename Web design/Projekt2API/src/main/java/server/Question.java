package server;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Question {
	
	private String id;
	private String question;
	private String theme;
	private String correctanswer;
	private ArrayList<String> incorrectanswers;
	private String media;
	private String mediaFileName;
	private String mediaType;
	private String mediaURL;
	private String mediaAction;
	
	private static Logger logger = LogManager.getLogger(Question.class);

	public Question () {
		
	}
	public Question(String id, String question, String theme, String correctanswer, ArrayList<String> incorrectanswers, String media, String mediaFileName, String mediaType, String mediaURL, String mediaAction) {
		this.id = id;
		this.question = question;
		this.theme = theme;
		this.correctanswer = correctanswer;
		this.incorrectanswers = incorrectanswers;
		this.media = media;
		this.mediaFileName = mediaFileName;
		this.mediaType = mediaType;
		this.mediaURL = mediaURL;
		this.setMediaAction(mediaAction);
	}
	
	public Question(String id, String question, String theme, String correctanswer, ArrayList<String> incorrectanswers) {
		this.id = id;
		this.question = question;
		this.theme = theme;
		this.correctanswer = correctanswer;
		this.incorrectanswers = incorrectanswers;
	}

	public void setId(String id){ this.id = id; }

	public void setMediaFileName(String mediaFileName) {
		this.mediaFileName = mediaFileName;
	}
	
	public void setQuestion(String question){
		this.question = question;
	}

	public void setTheme(String theme){
		this.theme = theme;
	}

	public void setCorrectAnswer(String correctAnswer){
		this.correctanswer = correctAnswer;
	}

	public void setIncorrectAnswers(ArrayList<String> incorrectAnswers){
		this.incorrectanswers = incorrectAnswers;
	}

	public void setMedia(String media){
		this.media = media;
	}

	public void setMediaType(String mediaType){
		this.mediaType = mediaType;
	}

	public String getId(){ return id; }

	public String getMediaFileName() {
		return mediaFileName;
	}

	public String getQuestion(){
		return question;
	}

	public String getTheme(){
		return theme;
	}

	public String getCorrectAnswer(){
		return correctanswer;
	}

	public ArrayList<String> getIncorrectAnswers(){
		return incorrectanswers;
	}

	public String getMedia(){
		return media;
	}

	public String getMediaType(){
		return mediaType;
	}
	
	public void setMediaURL(String mediaURL) {
		this.mediaURL = mediaURL;
	}
	
	public String getMediaURL() {
		return mediaURL;
	}
	
	public String getMediaAction() {
		return mediaAction;
	}
	public void setMediaAction(String mediaAction) {
		this.mediaAction = mediaAction;
	}
	
	public boolean isValid() {
		if(question == null || incorrectanswers == null || correctanswer == null || theme == null ) {
			return false;
		}
		for (int i = 0; i < incorrectanswers.size(); i++) {
			String answer = incorrectanswers.get(i);
			if(answer.equals("")) {
				logger.info("empty answer");
				return false;
			}
		}
		if(question.equals("") || correctanswer.equals("") || theme.equals("") || incorrectanswers.size() < 2) {
			return false;
		}
		return true;
	}
	
	public void logQuestion(Question question) {
		logger.info("Question: " + this.question);
		logger.info("Theme: " + theme);
		logger.info("Correct: " + correctanswer);
		for(int i = 0; i < incorrectanswers.size(); i++) {
			logger.info("incorrect: " + incorrectanswers.get(i).toString());
		}
	}


}
