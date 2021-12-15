package io.github.h800572003.concurrent;

import java.io.Closeable;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * 門閂工人服務
 * 
 * @author 6407
 *
 * @param <T>
 */
@Slf4j
public class WorkLatchService<T> implements Closeable, IWorkService<T> {

	private LinkedList<T> items = Lists.newLinkedList();
	private List<Work> works = Lists.newArrayList();
	private CountDownLatch countDownLatch;
	private CountDownLatch startDownLatch = new CountDownLatch(1);
	private CountDownLatch closeDownLatch;
	private boolean isClose = false;
	private int closeTimeout = 5;
	private WorkExecutor<T> workExecutor;
	private ErrorCallable<T> errorCallable;


	public static <T> WorkLatchService<T> newService(String prefName, int workSize, WorkExecutor<T> workListener) {
		return new WorkLatchService<T>(prefName, workSize, workListener, new NoneErrorCallable<>(), 5);
	}

	public static <T> WorkLatchService<T> newService(String prefName, int workSize, WorkExecutor<T> workListener,
			int closeTimeout) {
		return new WorkLatchService<T>(prefName, workSize, workListener, new NoneErrorCallable<>(), closeTimeout);
	}

	public static <T> WorkLatchService<T> newService(String prefName, int workSize, WorkExecutor<T> workListener,
			ErrorCallable<T> errorCallable, int closeTimeout) {
		return new WorkLatchService<T>(prefName, workSize, workListener, errorCallable, closeTimeout);
	}

	protected WorkLatchService(String prefName, int workSize, WorkExecutor<T> workListener,
			ErrorCallable<T> errorCallable, int closeTimeout) {
		this.closeDownLatch = new CountDownLatch(workSize);// 工人數量
		this.closeTimeout = closeTimeout;
		this.workExecutor = workListener;
		this.errorCallable = errorCallable;
		IntStream.range(0, workSize).forEach(i -> {
			works.add(new Work(this, prefName + "_" + i));
		});
	}

	@Override
	public void addItem(T item) {
		synchronized (this.items) {
			this.items.addLast(item);
			this.items.notifyAll();
		}
	}

	protected T take() throws InterruptedException {
		synchronized (this.items) {
			while (this.items.isEmpty()) {
				this.items.wait();
			}
			return items.removeFirst();
		}
	}

	public class Work {

		private WorkLatchService<T> homewrokCountDownLatch;
		private Thread thread;

		public Work(WorkLatchService<T> homewrokCountDownLatch, String name) {
			super();
			this.homewrokCountDownLatch = homewrokCountDownLatch;
			this.thread = new Thread(() -> {
				while (!isClose) {
					try {
						startDownLatch.await();
						T t = this.homewrokCountDownLatch.take();
						this.execute(t);
						countDownLatch.countDown();
					} catch (InterruptedException e) {
						log.info("發現中斷事情");
					}
				}
				log.info("close work");
				closeDownLatch.countDown();

			}, name);
			this.thread.start();
		}

		protected void execute(T t) {
			try {
				workExecutor.execute(t);
			} catch (Exception e) {
				if (errorCallable != null) {
					errorCallable.execute(t, e);
				}
			}
		}

	}

	protected void start() {
		this.startDownLatch.countDown();
	}

	/**
	 * 
	 */
	@Override
	public void close() {
		synchronized (this.items) {
			this.isClose = true;
		}
		try {
			closeDownLatch.await(closeTimeout, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			log.info("close timeout:{}", e);
		}

		log.debug("close down");

	}

	/**
	 * 堵住，執行清單完成，才繼續
	 */
	@Override
	public void execute() throws InterruptedException {
		this.countDownLatch = new CountDownLatch(items.size());
		this.start();
		this.countDownLatch.await();
	}

}
