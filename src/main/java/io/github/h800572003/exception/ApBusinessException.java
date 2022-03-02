package io.github.h800572003.exception;

import java.text.MessageFormat;

public class ApBusinessException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ApBusinessException(String pattern, Object... arguments) {
		super(MessageFormat.format(pattern, arguments));
	}

	public ApBusinessException(String string, Throwable throwable) {
		super(string, throwable);
	}

	public ApBusinessException(String pattern, Object[] arguments, Throwable throwable) {
		super(MessageFormat.format(pattern, arguments), throwable);
	}

}
