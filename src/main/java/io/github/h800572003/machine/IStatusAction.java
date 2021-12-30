package io.github.h800572003.machine;

import java.util.function.Function;

public interface IStatusAction<T extends IStatus, R> {

	/**
	 * 註冊行為
	 * 
	 * @param acion
	 * @param consumer
	 */
	void register(StatusAction acion, Function<T, R> consumer);

	/**
	 * 
	 * @param statusAction
	 * @return
	 */
	Function<T, R> getAction(StatusAction statusAction);

}