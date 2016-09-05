package com.sleep.binlog.net;

public class NetworkException extends RuntimeException {

	private static final long serialVersionUID = 5216918944562682007L;
	
	public NetworkException(String message) {
		super(message);
	}
	
	public NetworkException(String message, Throwable cause) {
		super(message, cause);
	}

}
