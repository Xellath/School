package server;

import static spark.Spark.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.google.gson.Gson;
import server.exceptions.RequestException;

public class SparkInterface {

	private static TempKeys tempKeys = new TempKeys();

	private static Logger logger = LogManager.getLogger(SparkInterface.class);

	public static void main(String[] args) {
		new UserConfig();
		staticFiles.externalLocation("media");
		String endpoints = readEndpoints();
		DBManager db = new DBManager();

		port(UserConfig.SERVERPORT);

		options("/*", (request, response) -> {
			logger.info("options headers: " + request.headers());
			String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
			if (accessControlRequestHeaders != null) {
				response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
			}
			String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
			if (accessControlRequestMethod != null) {
				response.header("Access-Control-Allow-Methods",	accessControlRequestMethod);
			}
			response.header("Access-Control-Allow-Credentials", "true");
			return "OK";
		});

		before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));

		before(Paths.API.WILDCARD, (request, response) -> {
			logger.info("API call " + request.ip() + " " + request.requestMethod());
			if(request.requestMethod() != "OPTIONS") {
				boolean authenticated = false;
				String params = request.headers("Authorization");
				Map<String, String> auth = Utility.parseHeader(params);
				String key = auth.get("key");
				authenticated = Utility.validateKey(key, db);
				logger.info("api key: " + key);
				if(!authenticated) {
					throw new RequestException(Constants.HTTP_BAD_REQUEST, Constants.ERROR_INVALID_API_KEY);
				}	
			}
		});

		after(Paths.API.WILDCARD, (request, response) -> {
			if(request.requestMethod().equals("GET"))
				response.type(Constants.APPLICATION_JSON);
		});

		get(Paths.API.ENDPOINTS, (request, response) -> {
			return endpoints;
		});	
		post(Paths.API.THEMES, (request, response) -> {
			return new ThemeHandler().add(request, response, db);
		});

		put(Paths.API.THEMES_ID, (request, response) -> {
			return new ThemeHandler().update(request, response, db);
		});

		get(Paths.API.THEMES, (request, response) -> {
			return new ThemeHandler().getAll(request, response, db);
		});

		get(Paths.API.THEMES_ID, (request, response) -> {
			return new ThemeHandler().get(request, response, db);
		});

		delete (Paths.API.THEMES_ID, (request, response) -> {
			return new ThemeHandler().remove(request, response, db);
		});

		post(Paths.API.QUESTIONS, (request, response) -> {
			return new QuestionHandler().add(request, response, db);
		});

		put(Paths.API.QUESTIONS_ID, (request, response) -> {
			return new QuestionHandler().update(request, response, db);
		});

		get(Paths.API.QUESTIONS, (request, response) -> {
			return new QuestionHandler().getRandom(request, response, db);
		});

		get(Paths.API.QUESTIONS_ID, (request, response) -> {
			return new QuestionHandler().get(request, response, db);
		});

		delete (Paths.API.QUESTIONS_ID, (request, response) -> {
			return new QuestionHandler().remove(request, response, db);
		});

		get(Paths.API.KEYS, (request, response) -> {
			return new KeyHandler().getUsers(request, response, db);
		});

		delete(Paths.API.KEYS_ID, (request, response) -> {
			return new KeyHandler().deleteKey(request, response, db);
		});

		post(Paths.ADMIN.KEY, (request, response) -> {
			return new KeyHandler().generateKey(request, response, db);
		});

		post(Paths.ADMIN.TEMP, (request, response) -> {
			return new KeyHandler().generateTempAdminKey(request, response, db, tempKeys);
		});

		post(Paths.ADMIN.ADMIN, (request, response) -> {
			return new KeyHandler().registerNewAdmin(request, response, db, tempKeys);
		});

		post(Paths.ADMIN.AUTHENTICATE, (request, response) -> {
			if(!Utility.validateAdmin(request.headers("Authorization"), db)) {
				throw new RequestException(Constants.HTTP_BAD_REQUEST, Constants.ERROR_INVALD_ADMIN);
			}
			response.body("");
			response.status(Constants.HTTP_OK);
			return response;
		});

		exception(RequestException.class, (exception, request, response) -> {
			logger.error(exception.getMessage());
			response.body(exception.getMessage());
			response.status(((RequestException) exception).getStatus());
			response.type(Constants.APPLICATION_JSON);
		});
	}

	private static String readEndpoints() {
		HashMap<String, String> map = new HashMap<>();
		try {
			Field[] fields = Paths.PublicEndpoints.class.getDeclaredFields();
			for(int i = 0; i < fields.length-1; i++) {
				String name = fields[i].getName().toLowerCase();
				String value;
				value = (String) fields[i].get(null);
				map.put(name, value);
			}
		} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
			logger.error(e.getMessage());
		} 
		return new Gson().toJson(map).toString();
	}
}