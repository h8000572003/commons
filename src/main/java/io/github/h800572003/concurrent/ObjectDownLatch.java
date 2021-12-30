package io.github.h800572003.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 物件栓
 * 
 * @author andy tsai
 *
 */
public class ObjectDownLatch {

	private List<Object> list;
	private Map<Object, AtomicInteger> downMap;

	/**
	 * 
	 * @param list
	 *            待處理項目
	 */
	public ObjectDownLatch(List<?> list) {
		super();
		this.list = new ArrayList<Object>();
		this.list.addAll(list);
		this.downMap = this.list.stream().collect(Collectors.toMap(i -> i, i -> new AtomicInteger(-1)));
	}

	/**
	 * 等待通過
	 * 
	 * @throws InterruptedException
	 */
	public synchronized void await() throws InterruptedException {
		while (!list.isEmpty()) {
			this.wait();
		}
	}

	/**
	 * 
	 * @param object
	 *            物件
	 * @param size
	 *            次數
	 */
	public synchronized void remove(Object object, int size) {
		if (downMap.containsKey(object)) {
			AtomicInteger atomicInteger = downMap.get(object);
			if (atomicInteger.get() == -1) {
				atomicInteger = new AtomicInteger(size);
				downMap.put(object, atomicInteger);
			}
			if (atomicInteger.get() > 0) {
				int decrementAndGet = atomicInteger.decrementAndGet();
				downMap.put(object, atomicInteger);
				if (decrementAndGet == 0) {
					list.remove(object);
					if (list.isEmpty()) {
						this.notifyAll();
					}
				}
			}

		}
	}

}
