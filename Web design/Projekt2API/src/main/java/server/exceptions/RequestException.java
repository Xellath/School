package server.exceptions;


public class RequestException extends Exception {
	
	private final int status;
	private String message;
	
	public RequestException(int status, String reason) {
		message = String.format("{\"error_message\":\"%s\"}", reason);
		this.status = status;
	}
	
	public int getStatus() {
		return status;
	}
	
	@Override
	public String getMessage() {
		return message;
	}

}