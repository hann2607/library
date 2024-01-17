package net.sparkminds.library.exception;

public class RequestException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final int statusCode;
	
	private final String messageCode;

	public RequestException(String message, int statusCode, String messageCode) {
		super(message);
		this.statusCode = statusCode;
		this.messageCode = messageCode;
	}

	public RequestException(String message, int statusCode, String messageCode, Throwable cause) {
		super(message, cause);
		this.statusCode = statusCode;
		this.messageCode = messageCode;
	}

	public int getStatusCode() {
        return statusCode;
    }
	
	public String getMessageCode() {
        return messageCode;
    }
}
