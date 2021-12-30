package io.github.h800572003.machine;

import java.util.function.Function;

/**
 * 狀態行為
 * 
 * @author andy tsai
 *
 * @param <T>
 * @param <R>
 */
public interface IStatusAction<T extends IStatus, R> {

	/**
	 * 註冊行為
	 * 
	 * @param acion
	 * @param consumer
	 */
	void register(StatusAction acion, Function<T, R> consumer);

	/**
	 * 取得行為
	 * 
	 * @param statusAction
	 * @return
	 */
	Function<T, R> getAction(StatusAction statusAction);

}