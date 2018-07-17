package server;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import server.exceptions.*;
import spark.Request;
import spark.Response;

public class QuestionHandler implements HandlerInterface {
	
	private static Logger logger = LogManager.getLogger(QuestionHandler.class);

	@Override
	public Response add(Request request, Response response, DBManager db) throws RequestException {
		logger.info("add question");
		if(!Utility.isJSON(request.contentType())) {
			throw new RequestException(Constants.HTTP_BAD_REQUEST, Constants.ERROR_INVALID_CONTENTYPE);
		}
		Question question = new Gson().fromJson(request.body(), Question.class);
		if(!question.isValid()) {
			throw new RequestException(Constants.HTTP_BAD_REQUEST, "invalid question");
		}
		question.logQuestion(question);
		if(question.getMedia() != null) {
			response = processFile(response, question);
		}

		int questionId = db.saveNewQuestion(question);
		if(questionId == -1) {
			throw new RequestException(
					Constants.HTTP_INTERNAL_SERVER_ERROR, Constants.ERROR_INTERNALDATA);
		}
		response.status(Constants.HTTP_CREATED);
		response.header("Location", Paths.API.QUESTIONS+"/"+ questionId);
		response.body("");
		logger.info("new question saved");
		return response;
	}

	@Override
	public Response update(Request request, Response response, DBManager db) throws RequestException {
		if(!Utility.isJSON(request.contentType())) {
			throw new RequestException(Constants.HTTP_BAD_REQUEST, Constants.ERROR_INVALID_CONTENTYPE);
		}
		if(!Utility.validateAdmin(request.headers("Authorization"), db)) {
			throw new RequestException(Constants.HTTP_BAD_REQUEST, Constants.ERROR_INVALD_ADMIN);
		}
		Question question = null;
		String paramId = request.params(":id");
		question = new Gson().fromJson(request.body(), Question.class);
		if(!question.isValid()) {
			throw new RequestException(Constants.HTTP_BAD_REQUEST, "invalid question");
		}
		if(!paramId.equals(question.getId())) {
			throw new RequestException(
					Constants.HTTP_BAD_REQUEST, "question id and param id must be equal");
		}

		if(question.getMedia() == null && question.getMediaAction() != null) {
			if(question.getMediaAction().equals(Constants.MEDIA_DELETE)) {
				FileManager fileManager = new FileManager();
				Question temp = db.getQuestion(question.getId());
				fileManager.removeFile(temp.getMediaFileName());
				question.setMediaFileName(Constants.MEDIA_DELETE);
				fileManager = null;
			}
		} else if(question.getMedia() != null) {
			response = processFile(response, question);
		}

		int res = db.updateQuestion(question);
		if(res == -1) {
			throw new RequestException(Constants.HTTP_INTERNAL_SERVER_ERROR, "could not update question");
		}
		logger.info("question " + question.getId() + " updated");
		response.body("");
		response.status(Constants.HTTP_OK);
		return response;
	}

	@Override
	public Response remove(Request request, Response response, DBManager db) throws RequestException {
		if(!Utility.validateAdmin(request.headers("Authorization"), db)) {
			throw new RequestException(Constants.HTTP_BAD_REQUEST, Constants.ERROR_INVALD_ADMIN);
		}
		String id = request.params(":id");
		if(id == null) {
			throw new RequestException(
						Constants.HTTP_BAD_REQUEST, "id can not be null in remove question request");
		}
		Question question = db.getQuestion(id);
		String fileName = question.getMediaFileName();
		if(fileName != null) {
			FileManager fileManager = new FileManager();
			boolean res = fileManager.removeFile(fileName);
			if(res == false) {
				//TODO client response?
			}
			fileManager = null;
		}
		db.deleteQuestion(id);
		response.body("");
		response.status(Constants.HTTP_NO_CONTENT);
		logger.info("removing question: " + request.params(":id"));
		return response;
	}

	@Override
	public Response getAll(Request request, Response response, DBManager db) throws RequestException {
		return null;
	}

	@Override
	public Response get(Request request, Response response, DBManager db) throws RequestException {
		logger.info("get question");
		String id = request.params(":id");
		if(id == null) {
			logger.info("get question request with null value");
			throw new RequestException(Constants.HTTP_BAD_REQUEST, "must specify id");
		}
		Question question = db.getQuestion(id);
		if(question.getMediaFileName() != null) {
			question = Utility.buildQuestionWithURL(question);
		}
		if(question.getId().equals("-1")) {
			logger.info("could not get question from db");
			throw new RequestException(Constants.HTTP_INTERNAL_SERVER_ERROR, "could not get question");
		} else {
			response.status(Constants.HTTP_OK);
			response.body(new Gson().toJson(question));
		}
		return response;
	}

	public Response getRandom(Request request, Response response, DBManager db) throws RequestException {
		if(!Utility.isJSON(request.contentType())) {
			throw new RequestException(Constants.HTTP_BAD_REQUEST, Constants.ERROR_INVALID_CONTENTYPE);
		}
		logger.info("getRandom");
		ArrayList<Question> qList = null;
		String theme = request.queryParams("theme");
		int pageSize = Utility.convertToInt(request.queryParams("pagesize"));
		logger.info("requested theme: " + theme);
		logger.info("requested pagesize: " + pageSize);
		if(pageSize == -1) {
			pageSize = Constants.DEFAULT_PAGESIZE_QUESTIONS;
		}
		if(theme == null) {
			qList = db.getRandomQuestion(pageSize);
			for(int i = 0; i < qList.size(); i++) {
				Question temp = qList.get(i);
				if(!temp.isValid()) {
					logger.info("invalid question: " + temp.getId());
					throw new RequestException(Constants.HTTP_INTERNAL_SERVER_ERROR, Constants.ERROR_INTERNALDATA);
				}
				if(temp.getMediaFileName() != null) {
					temp = Utility.buildQuestionWithURL(temp);
				}
			}
		} else {
			logger.info("random with theme");
			qList = db.getRandomQuestion(theme, pageSize);
			for(int i = 0; i < qList.size(); i++) {
				Question temp = qList.get(i);
				if(!temp.isValid()) {
					logger.info("invalid question: " + temp.getId());
					throw new RequestException(Constants.HTTP_INTERNAL_SERVER_ERROR, Constants.ERROR_INTERNALDATA);
				}
				if(temp.getMediaFileName() != null) {
					temp = Utility.buildQuestionWithURL(temp);
				}
			}
		}
		response.body(new Gson().toJson(qList));
		return response;
	}
	
	private Response processFile(Response response, Question question) throws RequestException {
		FileManager fileManager = new FileManager();
		
		String file = question.getMedia();
		String mediaURL = "";
		
		byte[] decodedBytes = fileManager.decodeFile(file);
		int fileSize = decodedBytes.length;
		String fileType = fileManager.getFileType(decodedBytes);
		if(fileType.equals("image/png") || fileType.equals("image/jpeg") || fileType.equals("image/gif")) {
			question.setMediaType("image");
			logger.info("image file size: " + fileSize);
			if(fileSize > UserConfig.MAXIMAGESIZE) {
				logger.info("image file is too big");
				throw new RequestException(Constants.HTTP_UNSUPPORTED_MEDIA, "file too large. max 2MB");
			}
			
			float ratio = fileManager.checkRatio(decodedBytes);
			if(ratio < UserConfig.MINIMAGERATIO 
					|| ratio > UserConfig.MAXIMAGERATIO) {
				logger.info("invalid image ratio");
				throw new RequestException(Constants.HTTP_UNSUPPORTED_MEDIA, "invalid aspect ratio");
			}
		} else if(fileType.equals("audio/mpeg")) {
			question.setMediaType("audio");	//TODO constants
			if(fileSize > UserConfig.MAXAUDIOSIZE) {
				logger.info("audio file too large");
				throw new RequestException(Constants.HTTP_UNSUPPORTED_MEDIA, "file too large");
			}
		} else if(fileType.equals("video/mp4")) {
			question.setMediaType("video");	//TODO constants
			fileManager.getMp4Data(decodedBytes);
			if(fileSize > UserConfig.MAXVIDEOSIZE) {
				logger.info("video file too large");
				throw new RequestException(Constants.HTTP_UNSUPPORTED_MEDIA, "file too large");
			}
		} else {
			logger.info("unsupported media type");
			fileManager = null;
			throw new RequestException(Constants.HTTP_UNSUPPORTED_MEDIA, "unsupported media type");
		}
		
		mediaURL = fileManager.saveFile(decodedBytes, fileType);
		
		if(mediaURL.equals("-1")) {
			fileManager = null;
			throw new RequestException(Constants.HTTP_INTERNAL_SERVER_ERROR, "could not save media");
		}
		
		logger.info("mediatype: " + question.getMediaType());
		question.setMediaFileName(mediaURL);
		fileManager = null;
		return response;
	}

}