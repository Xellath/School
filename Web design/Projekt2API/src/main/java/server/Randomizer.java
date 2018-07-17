package server;

import java.security.SecureRandom;

public class Randomizer {
	
	private String validCharacters = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvXxYyZz1234567890";
	private String validReadableCharacters = "AaBbCcDdEeFfGgHhJjKkLMmNnPpQqRrSsTtUuVvXxYyZz23456789";
	
	SecureRandom rand = new SecureRandom();
	
	public String getRandomName() {
		int rnd = rand.nextInt(Integer.MAX_VALUE);
		String name = Integer.toString(rnd);
		return name;
	}

	public String getRandomKey(int nbrOfCharacters) {
		String key = "";
		for(int i = 0; i < nbrOfCharacters; i++) {
			int position = (int)(rand.nextDouble()*validCharacters.length());
			key += validCharacters.substring(position, position + 1);
		}
		return key;
	}
	
	public String getRandomReadableKey(int nbrOfCharacters) {
		String key = "";
		for(int i = 0; i < nbrOfCharacters; i++) {
			int position = (int)(rand.nextDouble()*validReadableCharacters.length());
			key += validCharacters.substring(position, position + 1);
		}
		return key;
	}
	
	public static void main(String args[]) {
		Randomizer r = new Randomizer();
		r.getRandomName();
	}
}