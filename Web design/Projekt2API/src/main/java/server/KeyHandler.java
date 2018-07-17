package server;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.exceptions.RequestException;
import spark.Request;
import spark.Response;

public class KeyHandler {
	
	private static Logger logger = LogManager.getLogger(KeyHandler.class);
	
	public Response generateKey(Request request, Response response, DBManager db) throws RequestException {
			if(!Utility.isJSON(request.contentType())){
			throw new RequestException(Constants.HTTP_BAD_REQUEST, Constants.ERROR_INVALID_CONTENTYPE);
			}
			User user = new Gson().fromJson(request.body(), User.class);
			String key = new Randomizer().getRandomKey(32);
			db.saveNewUser(user.getEmail(), key, user.getDescription());
			new EmailService().sendUserKey(user.getEmail(), key);
			response.body("");
			response.status(Constants.HTTP_CREATED);
		return response;
	}
	
	public Response generateTempAdminKey(Request request, Response response, DBManager db, TempKeys tempKeys) throws RequestException {
		if(!Utility.validateAdmin(request.headers("Authorization"), db)) {
			throw new RequestException(Constants.HTTP_BAD_REQUEST, Constants.ERROR_INVALD_ADMIN);
		}
		if(!Utility.isJSON(request.contentType())){
			throw new RequestException(Constants.HTTP_BAD_REQUEST, Constants.ERROR_INVALID_CONTENTYPE);
		}
		User user = new Gson().fromJson(request.body(), User.class);
		String key = new Randomizer().getRandomReadableKey(5);
		logger.info("Key: " + key);
		long time = System.currentTimeMillis();
		tempKeys.put(key, time);
		boolean mailSent = new EmailService().sendAdminKey(user.getEmail(), key);
		if(!mailSent) {
			throw new RequestException(Constants.HTTP_BAD_REQUEST, "could not send email");
		}
		response.body("");
		response.status(Constants.HTTP_OK);
		return response;
	}

	public Response getUsers(Request request, Response response, DBManager db)  throws RequestException  {
			ArrayList<User> users = db.getAllUsers();
			response.body(new Gson().toJson(users));
			response.status(Constants.HTTP_OK);
		return response;
	}

	public Response deleteKey(Request request, Response response, DBManager db)  throws RequestException {
			String email = request.params(":id");
			db.deleteUser(email);
			response.status(Constants.HTTP_NO_CONTENT);
			response.body("");
		return response;
	}
	
	public synchronized Response registerNewAdmin(Request request, Response response, DBManager db, TempKeys tempKeys) throws RequestException {
		if(!Utility.isJSON(request.contentType())) {
			throw new RequestException(Constants.HTTP_BAD_REQUEST, Constants.ERROR_INVALID_CONTENTYPE);
		}
		HashMap<String, String> map = Utility.parseHeader(request.headers("Authorization"));
		User user = new Gson().fromJson(request.body(), User.class);
		String tempKey = map.get("tempkey");
		if(!tempKeys.validateKey(tempKey)) {
			throw new RequestException(Constants.HTTP_BAD_REQUEST, "invalid key");
		}
		int res;
		res = db.saveNewAdmin(map.get("user"), user.getEmail(), map.get("password"));
		if(res == -2) {
			throw new RequestException(Constants.HTTP_BAD_REQUEST, "user name already taken");
		}
		if(res == -1) {
			throw new RequestException(Constants.HTTP_INTERNAL_SERVER_ERROR, "could not save admin");
		}
		tempKeys.removeKey(tempKey);
		logger.info("tempkey: " + tempKey);
		response.status(Constants.HTTP_CREATED);
		return response;
	}

}