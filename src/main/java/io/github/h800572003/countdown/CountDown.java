package io.github.h800572003.countdown;

import lombok.extern.slf4j.Slf4j;

/**
 * 到數器
 * 
 * @author andy tsai
 *
 */
@Slf4j
public class CountDown {

	private Object lock = new Object();

	private int value;

	/**
	 * 增加
	 */
	public void increase() {
		synchronized (lock) {
			value++;
		}
	}

	/**
	 * 減少
	 */
	public void decrease() {
		synchronized (lock) {
			if (value > 0) {
				value--;
			}
			lock.notifyAll();
		}
	}

	public int get() {
		synchronized (lock) {
			return this.value;
		}
	}

	public void await() throws InterruptedException {
		synchronized (lock) {

			while (this.value > 0) {
				lock.wait();
				if (Thread.interrupted()) {
					throw new InterruptedException();
				}
			}
		}
	}

}
