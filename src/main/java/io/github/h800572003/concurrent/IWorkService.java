package io.github.h800572003.concurrent;

import java.util.List;

/**
 * 工人服務
 * 
 * @author andy tsai
 *
 * @param <T>
 */
public interface IWorkService<T> {

	/**
	 * 關閉服務
	 */
	void close();

	/**
	 * 啟動
	 * 
	 * @throws InterruptedException
	 */
	void execute(IQueue<T> queue, List<? extends T> items) throws InterruptedException;

	/**
	 * 啟動
	 * 
	 * @throws InterruptedException
	 */
	void execute(List<? extends T> items) throws InterruptedException;

}