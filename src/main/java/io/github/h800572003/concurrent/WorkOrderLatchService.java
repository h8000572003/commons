package io.github.h800572003.concurrent;

import java.io.Closeable;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import com.google.common.collect.Lists;

import io.github.h800572003.order.IOrderBlockPool;
import io.github.h800572003.order.IOrderBlockPool.OrderKey;
import lombok.extern.slf4j.Slf4j;

/**
 * 門閂KEY鎖工人服務
 * 
 * @author 6407 同樣OrderKey資料有依照時間執行。o
 * @param <T>
 */
@Slf4j
public class WorkOrderLatchService<T extends OrderKey> implements Closeable, IWorkService<T> {

	private final IOrderBlockPool<T> pool;
	private List<Work> works = Lists.newArrayList();
	private CountDownLatch countDownLatch;
	private CountDownLatch startDownLatch = new CountDownLatch(1);
	private CountDownLatch closeDownLatch;// 關閉
	private boolean isClose = false;
	private int closeTimeout = 5;
	private WorkExecutor<T> workExecutor;
	private ErrorCallable<T> errorCallable;

	public static <T extends OrderKey> WorkOrderLatchService<T> createWorkOrderLatchService(String prefName,
			int workSize, WorkExecutor<T> workListener, IOrderBlockPool<T> pool, ErrorCallable<T> errorCallable,
			int closeTimeout) {
		return new WorkOrderLatchService<T>(prefName, workSize, workListener, pool, errorCallable, closeTimeout);
	}

	public static <T extends OrderKey> WorkOrderLatchService<T> createWorkOrderLatchService(String prefName,
			int workSize, WorkExecutor<T> workListener, IOrderBlockPool<T> pool) {
		return new WorkOrderLatchService<T>(prefName, workSize, workListener, pool, new NoneErrorCallable<T>(), 30);
	}

	/**
	 * 
	 * @param prefName
	 *            服務名稱
	 * @param workSize
	 *            工人數量
	 * @param workListener
	 *            工作監聽
	 * @param pool
	 *            資料池
	 * @param errorCallable
	 *            異常callable
	 * @param closeTimeout
	 *            關閉timeout
	 */
	protected WorkOrderLatchService(String prefName, int workSize, WorkExecutor<T> workListener,
			IOrderBlockPool<T> pool, ErrorCallable<T> errorCallable, int closeTimeout) {
		this.closeDownLatch = new CountDownLatch(workSize);// 工人數量
		this.closeTimeout = closeTimeout;
		this.workExecutor = workListener;
		this.errorCallable = errorCallable;
		this.pool = pool;
		IntStream.range(0, workSize).forEach(i -> {
			works.add(new Work(this, prefName + "_" + i));
		});
	}

	@Override
	public void addItem(T item) {
		synchronized (this.pool) {
			this.pool.add(item);
			this.pool.notifyAll();
		}
	}

	protected T take() throws InterruptedException {
		synchronized (this.pool) {
			while (!this.pool.hasNext()) {
				this.pool.wait();
			}
			return pool.take();
		}
	}

	public class Work {

		private WorkOrderLatchService<T> homewrokCountDownLatch;
		private Thread thread = null;

		public Work(WorkOrderLatchService<T> homewrokCountDownLatch, String name) {
			super();
			this.homewrokCountDownLatch = homewrokCountDownLatch;
			this.thread = new Thread(() -> {
				while (!isClose) {
					try {
						startDownLatch.await();
						T t = this.homewrokCountDownLatch.take();
						this.execute(t);

					} catch (InterruptedException e) {
						log.debug("中斷作業");
					}
				}
				closeDownLatch.countDown();

			}, name);
			this.thread.start();
		}

		public void close() {
			thread.interrupt();
		}

		protected void execute(T t) {
			try {
				workExecutor.execute(t);
				countDownLatch.countDown();
			} catch (Exception e) {
				if (errorCallable != null) {
					errorCallable.execute(t, e);
				}

			} finally {
				pool.removeKey(t);

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
		synchronized (this.pool) {
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
		this.countDownLatch = new CountDownLatch(pool.size());
		this.start();
		this.countDownLatch.await();
		this.works.forEach(Work::close);
	}

}
