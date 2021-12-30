package io.github.h800572003.machine;

import java.util.function.Function;

/**
 * 狀態事件Holder
 * 
 * @author andy tsai
 *
 * @param <T>
 * @param <R>
 */
public interface IStatusActionHolder<T extends IStatus, R> {

	/**
	 * 取得行為
	 * 
	 * @param statusAction
	 * @return
	 */
	Function<T, R> getAction(StatusAction statusAction);

	/**
	 * 註冊
	 * 
	 * @param acion
	 * @param consumer
	 */
	void register(StatusAction acion, Function<T, R> consumer);
}