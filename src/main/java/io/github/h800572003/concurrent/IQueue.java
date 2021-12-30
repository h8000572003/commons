package io.github.h800572003.concurrent;

/**
 * Queue
 * 
 * @author andy tsai
 *
 * @param <T>
 */
public interface IQueue<T> {
	/**
	 * 通過移除柱列
	 * @param src
	 */
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