package io.github.h800572003.commons.backed;

public class BackendCancelException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BackendCancelException(String error, Throwable throwable) {
		super(error, throwable);
	}

	public BackendCancelException(String error) {
		super(error);
	}
}
