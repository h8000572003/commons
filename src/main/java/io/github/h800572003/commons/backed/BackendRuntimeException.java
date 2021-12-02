package io.github.h800572003.commons.backed;

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
