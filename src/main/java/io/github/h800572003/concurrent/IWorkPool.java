package io.github.h800572003.concurrent;

/**
 * 工人池
 * @author andy tsai
 *
 */
public interface IWorkPool {

	/**
	 * 啟動
	 */
	void start();

	/**
	 * 關閉
	 */
	void close();

}