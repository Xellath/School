package server;

import java.util.ArrayList;
import java.util.HashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.google.gson.Gson;
import server.exceptions.*;
import spark.Request;
import spark.Response;

public class ThemeHandler implements HandlerInterface {
	private static Logger logger = LogManager.getLogger(ThemeHandler.class);
	private static final int DEFAULT_NUM_OF_QUESTIONS = 10;

	@Override
	public Response add(Request request, Response response, DBManager db) throws RequestException {
		if(!Utility.isJSON(request.contentType())) {
			throw new RequestException(Constants.HTTP_BAD_REQUEST, Constants.ERROR_INVALID_CONTENTYPE);
		}
		Theme theme = new Gson().fromJson(request.body(), Theme.class); 
		if(!theme.isValid()) {
			throw new RequestException(Constants.HTTP_BAD_REQUEST, Constants.ERROR_INVALID_KEYVALUE);
		}
		db.saveTheme(theme);
		response.status(Constants.HTTP_CREATED);
		response.header("Location", Paths.API.THEMES+"/"+theme.getTheme());
		response.body("");
		logger.info("adding theme: " + theme.getTheme());
		return response;
	}

	@Override
	public Response update(Request request, Response response, DBManager db) throws RequestException  {
		if(!Utility.isJSON(request.contentType())) {
			throw new RequestException(Constants.HTTP_BAD_REQUEST, Constants.ERROR_INVALID_CONTENTYPE);
		}
		if(!Utility.validateAdmin(request.headers("Authorization"), db)) {
			throw new RequestException(Constants.HTTP_BAD_REQUEST, Constants.ERROR_INVALD_ADMIN);
		}
		Theme theme = new Gson().fromJson(request.body(), Theme.class);
		if(!theme.isValid()) {
			throw new RequestException(Constants.HTTP_BAD_REQUEST, Constants.ERROR_INVALID_KEYVALUE);
		}
		db.updateTheme(request.params(":id"), theme.getTheme());
		response.status(Constants.HTTP_OK);
		response.header("Location", Paths.API.THEMES+"/"+theme.getTheme()); 
		response.body("");
		logger.info("updating theme: " + theme.getTheme());
		return response;
	}

	@Override
	public Response remove(Request request, Response response, DBManager db) throws RequestException {
		if(!Utility.validateAdmin(request.headers("Authorization"), db)) {
			throw new RequestException(Constants.HTTP_BAD_REQUEST, Constants.ERROR_INVALD_ADMIN);
		}
		String theme = request.params(":id");
		ArrayList<Question> removedList = db.deleteTheme(theme);
		FileManager fileManager = new FileManager();
		for(int i = 0; i < removedList.size(); i++) {
			String fileName = removedList.get(i).getMediaFileName();
			fileManager.removeFile(fileName);
		}
		response.status(Constants.HTTP_NO_CONTENT);
		response.body("");
		logger.info("deleting theme: " + theme);
		return response;
	}	

	@Override
	public Response getAll(Request request, Response response, DBManager db) throws RequestException {
		ArrayList<Theme> themes;
		int minNumOfQuestions = -1, pageSize = -1;
		minNumOfQuestions = Utility.convertToInt(request.queryParams("minimumNumberOfQuestions"));
		pageSize = Utility.convertToInt(request.queryParams("pageSize"));
		minNumOfQuestions = (minNumOfQuestions >= 0 && minNumOfQuestions <= 10) ? minNumOfQuestions : DEFAULT_NUM_OF_QUESTIONS; 
		if(pageSize == -1) {
			themes = db.getAllThemes();
		} else {
			themes = db.getThemes(minNumOfQuestions, pageSize);
		}
		response.status(Constants.HTTP_OK);
		response.body(new Gson().toJson(themes));
		return response;
	}

	@Override
	public Response get(Request request, Response response, DBManager db) throws RequestException {
		ArrayList<Question> questions;
		String theme = request.params(":id");
		int pageSize = Utility.convertToInt(request.queryParams("pageSize"));
		int pageStart = Utility.convertToInt(request.queryParams("pageStart"));
		pageStart = (pageStart > 1) ? pageStart: 0;
		if (pageSize == -1) {
			questions = db.getThemeQuestions(theme);
			for(int i = 0; i < questions.size(); i++) {
				Question q = questions.get(i);
				if(!q.isValid()) {
					logger.info("invalid question: " + q.getId());
					throw new RequestException(Constants.HTTP_INTERNAL_SERVER_ERROR, Constants.ERROR_INTERNALDATA);
				}
				if(q.getMediaFileName() != null) {
					q = Utility.buildQuestionWithURL(q);
				}
			}
		} else {
			questions = db.getThemeQuestions(theme, pageSize, pageStart);
			for(int i = 0; i < questions.size(); i++) {
				Question q = questions.get(i);
				if(!q.isValid()) {
					logger.info("invalid question: " + q.getId());
					throw new RequestException(Constants.HTTP_INTERNAL_SERVER_ERROR, Constants.ERROR_INTERNALDATA);
				}
				if(q.getMediaFileName() != null) {
					q = Utility.buildQuestionWithURL(q);
				}
			}
		}
		response.status(Constants.HTTP_OK);
		response.body(new Gson().toJson(questions));
		return response;
	}
}
