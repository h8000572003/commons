package io.github.h800572003.concurrent;

public interface ErrorCallable<T> {
	void execute(T t, Throwable throwable);
}
