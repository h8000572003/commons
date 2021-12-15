package io.github.h800572003.order;

import java.util.List;

import io.github.h800572003.order.IOrderBlockPool.OrderKey;

/**
 * 
 * @author 6407
 *
 * @param <T>
 */
public interface IOrderBlockPool<T extends OrderKey> {

	public interface OrderKey {
		String toKey();
	}

	void addAll(List<? extends T> list);

	void add(T t);

	T take() throws InterruptedException;

	List<T> takeAll() throws InterruptedException;

	boolean hasNext();

	void removeKey(T key);

	int size();

}