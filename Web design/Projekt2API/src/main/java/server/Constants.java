package src.main.java.server;

public final class Constants {
	
	public static final int SERVER_PORT = 7500;
	
	public static final String APPLICATION_JSON = "application/json";

	public static final String MEDIA_DELETE = "delete";

	public static final String MEDIA_KEEP = "keep";
	
	public static final int MAXFILESIZE_IMAGE = 2000000;
	
	public static final int MAXFILESIZE_AUDIO = 10000000;
	
	public static final int MAXFILESIZE_VIDEO = 20000000;
	
	public static final int HTTP_BAD_REQUEST = 400;
	
	public static final int HTTP_OK = 200;

	public static final int HTTP_CREATED = 201;
	
	public static final int HTTP_NO_CONTENT = 204;
	
	public static final int HTTP_UNSUPPORTED_MEDIA = 415;
	
	public static final int HTTP_INTERNAL_SERVER_ERROR = 500;
	
	public static final int DEFAULT_PAGESIZE_QUESTIONS = 3;
	
	public static final String ERROR_INVALID_CONTENTYPE = "content type must be application/json";
	
	public static final String ERROR_INVALID_API_KEY = "invalid api key";
	
	public static final String ERROR_INVALID_KEYVALUE = "invalid key or value";
	
	public static final String ERROR_INVALD_ADMIN = "invalid username or password";
	
	public static final String ERROR_INTERNALDATA = "unable to get data";
}

