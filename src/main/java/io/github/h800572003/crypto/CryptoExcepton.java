package io.github.h800572003.crypto;

import java.text.MessageFormat;

/**
 * 加密異常
 * 
 * @author andy
 *
 */
public class CryptoExcepton extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CryptoExcepton(String pattern, Object... arguments) {
		super(MessageFormat.format(pattern, arguments));
	}

	public CryptoExcepton(String string, Throwable throwable) {
		super(string, throwable);
	}

}
