package server;

public final class Paths {
	
	public final class API {
		public static final String WILDCARD = "/api/*";
		public static final String THEMES = "/api/themes";
		public static final String THEMES_ID = "/api/themes/:id";
		public static final String QUESTIONS = "/api/questions";
		public static final String QUESTIONS_ID = "/api/questions/:id";
		public static final String KEYS = "/api/keys";
		public static final String KEYS_ID = "/api/keys/:id";
		public static final String ENDPOINTS = "/api/endpoints";
	}
	
	public final class PublicEndpoints {
		public static final String THEMES = API.THEMES;
		public static final String QUESTIONS = API.QUESTIONS;
		public static final String AUTHENTICATE = ADMIN.AUTHENTICATE;
	}
	
	public final class ADMIN {
		public static final String ADMIN = "/admin";
		public static final String AUTHENTICATE = "/admin/authenticate";
		public static final String TEMP = "/admin/temp";
		public static final String KEY = "/admin/key";
	}
}
