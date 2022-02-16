package io.github.h800572003.mybatis;

import io.github.h800572003.exception.ApBusinessException;

public class MybatisException extends ApBusinessException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MybatisException(String pattern, Object[] arguments) {
		super(pattern, arguments);
	}

	public MybatisException(String string, Throwable throwable) {
		super(string, throwable);
	}

}
