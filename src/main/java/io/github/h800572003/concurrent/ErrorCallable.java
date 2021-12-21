package io.github.h800572003.concurrent;

public interface ErrorCallable<T> {
	void call(T t, Throwable throwable);
}
