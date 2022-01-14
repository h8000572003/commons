package io.github.h800572003.backed;

import java.text.MessageFormat;

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
	public BackendRuntimeException(String pattern, Object... arguments) {
		super(MessageFormat.format(pattern, arguments));
	}
}
