package io.github.h800572003.concurrent;

/**
 * 工人服務
 * 
 * @author 6407
 *
 * @param <T>
 */
public interface IWorkService<T> {

	/**
	 * 加入案例
	 * 
	 * @param item
	 */
	void addItem(T item);

	/**
	 * 關閉服務
	 */
	void close();

	/**
	 * 啟動
	 * 
	 * @throws InterruptedException
	 */
	void execute() throws InterruptedException;

}