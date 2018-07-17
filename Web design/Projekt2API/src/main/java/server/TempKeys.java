package server;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TempKeys {
	
	private final long TWENTY_FOUR_HOURS = 86400000;
	
	private static Logger logger = LogManager.getLogger(TempKeys.class);
	
	private HashMap<String, Long> keys = new HashMap<>();
	
	public void put(String key, long time) {
		keys.put(key, time);
		logger.info("adding new admin temp key");
	}
	
	public void removeKey(String key) {
		keys.remove(key);
	}
	
	public boolean validateKey(String key) {
		long currentTime = System.currentTimeMillis();
		if(keys.containsKey(key)) {
			long keyCreated = keys.get(key);
			long age = currentTime - keyCreated;
			if(age > TWENTY_FOUR_HOURS) {
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}
}
