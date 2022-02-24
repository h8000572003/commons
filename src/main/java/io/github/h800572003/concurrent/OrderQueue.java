package io.github.h800572003.concurrent;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Set;

import com.google.common.collect.Lists;

/**
 * 排序Queue
 * 
 * @author andy tsai
 *
 * @param <T> 排隊項目
 */
public class OrderQueue<T extends IBlockKey> implements IOrderKeyQueue<T> {
	private final LinkedList<T> waitPool = Lists.newLinkedList();
	private final LinkedList<T> cachePool = Lists.newLinkedList();
	private final Set<String> waitPoolSet = new HashSet<>();// 待處理集合
	private final Object mute = new Object();

	@Override
	public void add(T t) {
		synchronized (this.mute) {
			if (!this.isKeyInWait(t)) {
				this.waitPool.addLast(t);
				this.waitPoolSet.add(t.toKey());
			} else {
				this.cachePool.addLast(t);
			}
			this.mute.notifyAll();
		}

	}

	private boolean isKeyInWait(T key) {
		return this.waitPoolSet.contains(key.toKey());
	}

	@Override
	public T take() throws InterruptedException {
		synchronized (this.mute) {
			while (this.waitPool.isEmpty()) {// 緩衝池也無東西
				this.mute.wait();
			}
			try {
				final T removeFirst = this.waitPool.removeFirst();
				return removeFirst;
			} catch (final NoSuchElementException e) {
				return null;
			}

		}

	}

	@Override
	public void removeBlock(T key) {
		synchronized (this.mute) {
			this.waitPoolSet.remove(key.toKey());
			final Iterator<T> it = this.cachePool.iterator();
			while (it.hasNext()) {
				final T next = it.next();
				if (!this.isKeyInWait(next)) {
					this.addWaitPool(next);
					it.remove();// 從cache池移除
				}
			}
			this.mute.notifyAll();
		}

	}

	private void addWaitPool(T t) {
		this.waitPool.addLast(t);
		this.waitPoolSet.add(t.toKey());
	}

	@Override
	public boolean isEmpty() {
		synchronized (this.mute) {
			return this.cachePool.isEmpty() && this.waitPoolSet.isEmpty();
		}
	}

	@Override
	public int size() {
		synchronized (this.mute) {
			return this.waitPool.size() + this.cachePool.size();
		}
	}

	@Override
	public void remove(T src) {
		removeBlock(src);
	}

}
