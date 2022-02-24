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
	 * @param acion 行為定義
	 * @param function   行為
	 */
	void register(StatusAction acion, Function<T, R> function);

	/**
	 * 取得行為
	 * 
	 * @param statusAction 行為定義
	 * @return
	 */
	Function<T, R> getAction(StatusAction statusAction);

}