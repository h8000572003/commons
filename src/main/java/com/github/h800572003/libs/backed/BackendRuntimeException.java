package com.github.h800572003.libs.backed;

public class BackendRuntimeException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BackendRuntimeException(String error, Throwable throwable) {
		super(error, throwable);
	}

	public BackendRuntimeException(String error) {
		super(error);
	}
}
