package io.github.h800572003.concurrent;

import java.util.LinkedList;

import com.google.common.collect.Lists;

public class BlockQueue<T> implements IQueue<T> {
	private final int blockSize;// 最大比數
	private final LinkedList<T> items = Lists.newLinkedList();

	/**
	 * 
	 * @param blockSize
	 *            堵塞筆數
	 */
	public BlockQueue(int blockSize) {
		super();
		this.blockSize = blockSize;
	}

	@Override
	public void add(T item) {
		synchronized (this.items) {
			while (this.items.size() > this.blockSize) {
				try {
					this.items.wait();
				} catch (final InterruptedException e) {
					break;
				}
			}
			this.items.addLast(item);
			this.items.notifyAll();
		}
	}

	@Override
	public T take() throws InterruptedException {
		synchronized (this.items) {
			while (this.items.isEmpty()) {
				this.items.wait();
			}
			return this.items.removeFirst();
		}
	}

	@Override
	public boolean isEmpty() {
		synchronized (this.items) {
			return this.items.isEmpty();
		}
	}

	@Override
	public int size() {
		synchronized (this.items) {
			return this.items.size();
		}
	}

	@Override
	public void remove(T src) {
		// TODO Auto-generated method stub

	}
}
