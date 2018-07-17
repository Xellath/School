package server;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Utility {

	private static Logger logger = LogManager.getLogger(Utility.class);

	/**
	 * Turns a string into an int. 
	 * @param parameter String to be converted.
	 * @return int value of string or -1.
	 */
	public static int convertToInt(String parameter) {
		int res = -1;
		try { 
			res = Integer.parseInt(parameter);
		} catch (NumberFormatException e) {
			if(parameter != null)
				logger.error("number format error", e.getMessage());
		}
		return res;
	}

	public static boolean isJSON(String contentType) {
		if(contentType != null && contentType == Constants.APPLICATION_JSON) {
			return true;
		}
		return false;
	}

	public static boolean validateKey(String key, DBManager db) {
		if(key!=null)
			return db.ifKeyExist(key);
		return false;
	}
	
	public static boolean validateAdmin(String AuthorizationHeader, DBManager db) {
		HashMap<String, String> map = Utility.parseHeader(AuthorizationHeader);
		if(Utility.validateAdmin(map.get("user"), map.get("password"), db)) {
			return true;
		}
		return false;
	}
	
	private static boolean validateAdmin(String userName, String password, DBManager db) {
		if(userName!=null) {
			String dbPass = db.getPassword(userName);
			if(dbPass.equals(password)) {
				return true;
			}	
		}
		return false;
	}

	/**
	 * Splits the header into rows and then into key, value pairs.
	 * @param header to be parsed.
	 * @return HashMap<String, String>
	 */
	public static HashMap<String, String> parseHeader(String header) {
		HashMap<String, String> map = new HashMap<>();
		if(header!=null) {
			String[] params = header.split(",");
			for (String index : params) {
				if(index.contains("=")) {
					String[] pair = index.split("=");
					map.put(pair[0], pair[1]);
				} 
			}
		}
		return map;
	}
	
	public static Question buildQuestionWithURL(Question question) {
		if(question.getMediaFileName() != null) {
			String url;
			String fileName = question.getMediaFileName();
			question.setMediaFileName(null);
			url = UserConfig.SERVER_ADDRESS + fileName;
			question.setMediaURL(url);
		}
		return question;
	}
}
