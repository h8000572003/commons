package io.github.h800572003.concurrent;

/**
 * Queue
 * 
 * @author 6407
 *
 * @param <T>
 */
public interface IQueue<T> {
	void remove(T src);

	/**
	 * 加入
	 * 
	 * @param item
	 */
	void add(T item);

	/**
	 * 取出
	 * 
	 * @return
	 */
	T take() throws InterruptedException;

	/**
	 * 是否空
	 * 
	 * @return
	 */
	boolean isEmpty();

	int size();

}