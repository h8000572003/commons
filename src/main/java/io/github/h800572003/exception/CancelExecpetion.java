package io.github.h800572003.exception;

import java.text.MessageFormat;

public class CancelExecpetion extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CancelExecpetion(String pattern, Object... arguments) {
		super(MessageFormat.format(pattern, arguments));
	}

	public CancelExecpetion(String string, Throwable throwable) {
		super(string, throwable);
	}

}
