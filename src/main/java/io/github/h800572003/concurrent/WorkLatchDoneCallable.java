package io.github.h800572003.concurrent;

public interface WorkLatchDoneCallable<T> {

	void call(T src, Throwable throwable);

}
