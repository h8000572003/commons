package io.github.h800572003.backed;

import java.text.MessageFormat;

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
	public BackendCancelException(String pattern, Object... arguments) {
		super(MessageFormat.format(pattern, arguments));
	}
}
