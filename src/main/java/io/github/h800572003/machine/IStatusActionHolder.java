package io.github.h800572003.machine;

import java.util.function.Function;

public interface IStatusActionHolder<T extends IStatus, R> {

	Function<T, R> getAction(StatusAction statusAction);

	/**
	 * 註冊
	 * 
	 * @param acion
	 * @param consumer
	 */
	void register(StatusAction acion, Function<T, R> consumer);
}