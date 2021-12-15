package io.github.h800572003.concurrent;

public class NoneErrorCallable<T> implements ErrorCallable<T> {

	@Override
	public void execute(Object t, Throwable throwable) {
		// 忽略錯誤
	}

}
