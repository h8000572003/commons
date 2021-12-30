package io.github.h800572003.concurrent;

/**
 * 堵篩KeyQueue
 * 
 * @author andy tsai
 *
 * @param <T>
 */
public interface IOrderKeyQueue<T extends IBlockKey> extends IQueue<T> {

	/**
	 * 移除堵塞
	 * 
	 * @param src
	 *            KEY OBJECT
	 */
	void removeBlock(T src);

}
