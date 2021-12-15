package io.github.h800572003.concurrent;

public interface WorkExecutor<T> {
	void execute(T t);
}
