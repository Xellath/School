package server;

/**
 * Created by spillberg on 2017-05-03.
 */
public class User {
    private String userEmail;
    private String apiKey;
    private String description;

    public User(String userEmail, String apiKey, String description) {
        this.userEmail = userEmail;
        this.apiKey = apiKey;
        this.description = description;
    }

    public String getEmail() {
        return userEmail;
    }

    public String getKey() {
        return apiKey;
    }

    public String getDescription() {
        return description;
    }
    
    public boolean isValid() {
    	if(userEmail !=null && userEmail.contains("@")) {
    		return true;
    	}
    	return false;
    }
}
