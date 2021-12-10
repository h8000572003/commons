package io.github.h800572003.order;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;

import io.github.h800572003.order.OrderPool.OrderKey;

public class OrderPool<T extends OrderKey> {

	private LinkedList<T> waitPool = Lists.newLinkedList();
	private LinkedList<T> cachePool = Lists.newLinkedList();
	private Set<String> waitPoolSet = new HashSet<>();// 待處理集合

	public static <T extends OrderKey> OrderPool<T> getPool() {
		return new OrderPool<T>();
	}

	private Object mute = new Object();

	interface OrderKey {
		String toKey();
	}

	public void addAll(List<? extends T> list) {
		list.stream().forEach(i -> this.add(i));
	}

	public void add(T t) {
		synchronized (mute) {
			if (!this.isKeyInWait(t)) {
				this.waitPool.addLast(t);
				this.waitPoolSet.add(t.toKey());
			} else {
				this.cachePool.addLast(t);
			}
			mute.notifyAll();
		}
	}

	private void addWaitPool(T t) {
		this.waitPool.addLast(t);
		this.waitPoolSet.add(t.toKey());
	}

	public T take() throws InterruptedException {
		synchronized (mute) {
			while (waitPool.isEmpty()) {// 緩衝池也無東西
				mute.wait(5);
			}
			T removeFirst = waitPool.removeFirst();
			return removeFirst;
		}

	}

	public List<T> takeAll() throws InterruptedException {
		synchronized (mute) {
			List<T> list = Lists.newArrayList();
			while (!waitPool.isEmpty()) {
				T removeFirst = waitPool.removeFirst();
				list.add(removeFirst);
			}
			return list;

		}

	}

	public boolean hasNext() {
		synchronized (mute) {
			return !cachePool.isEmpty() || !waitPoolSet.isEmpty();
		}
	}

	public void removeKey(T key) {
		synchronized (mute) {
			waitPoolSet.remove(key.toKey());
			Iterator<T> it = cachePool.iterator();
			while (it.hasNext()) {
				T next = it.next();
				if (!this.isKeyInWait(next)) {
					this.addWaitPool(next);
					it.remove();// 從cache池移除
				}
			}
			mute.notifyAll();
		}

	}

	private boolean isKeyInWait(T key) {
		synchronized (waitPoolSet) {
			return waitPoolSet.contains(key.toKey());
		}
	}
}
