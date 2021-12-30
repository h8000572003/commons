package io.github.h800572003.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.stream.IntStream;

import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import io.github.h800572003.exception.ApBusinessException;
import lombok.extern.slf4j.Slf4j;

/**
 * 工人保持存活池
 * 
 * @author andy tsai
 *
 * @param <T>
 */
@Slf4j
public class AlwAysAliveWorkPool<T> implements IWorkPool {
	private List<Work> works = new ArrayList<Work>();
	private final int workSize;
	private final ExecutorService executorService;
	private volatile ThreadFactory threadFactory;
	private final IQueue<T> queue;// 資料流
	private final WorkExecutor<T> workExecutor;
	private final WorkAdpaterCallBackend<T> workAdpaterCallBackend;
	private final CountDownLatch workDownLatch;//全員工紀錄
	private final String name;
	private boolean isStart = false;

	public AlwAysAliveWorkPool(int workSize, //
			String name, //
			IQueue<T> queue, //
			WorkExecutor<T> workExecutor, WorkAdpaterCallBackend<T> workAdpaterCallBackend//
	) {
		super();
		this.workSize = workSize;
		this.threadFactory = new CustomizableThreadFactory(name);
		this.executorService = Executors.newFixedThreadPool(workSize, threadFactory);
		this.queue = queue;
		this.name = name;
		this.workExecutor = workExecutor;
		this.workAdpaterCallBackend = workAdpaterCallBackend;
		this.workDownLatch = new CountDownLatch(workSize);

	}

	@Override
	public synchronized void start() {
		while (isStart) {
			throw new ApBusinessException("服務已啟動");
		}
		IntStream.range(0, this.workSize).forEach(i -> {
			this.works.add(this.createWork(i));
		});
		this.isStart = true;
	}

	protected Work createWork(int index) {
		return new Work(this.queue, this.name + "_" + index, this.workExecutor);
	}

	@Override
	public void close() {
		this.executorService.shutdownNow();
		try {
			this.workDownLatch.await();
		} catch (final InterruptedException e) {
			// 中斷直接結束
		}

	}

	public class Work {
		private final IQueue<T> queue;
		private Future<?> submit;
		private final WorkExecutor<T> workExecutor;

		public Work(IQueue<T> queue, String name, WorkExecutor<T> workExecutor) {
			super();
			this.queue = queue;
			this.workExecutor = workExecutor;
			this.submit = executorService.submit(() -> {
				while (!Thread.currentThread().isInterrupted()) {
					T t = null;
					try {
						t = this.queue.take();
						this.execute(t);
					} catch (final InterruptedException e) {
						log.info("回收工人");
						break;
					} finally {
						if (t != null) {
							this.queue.remove(t);
						}
					}
				}
				workDownLatch.countDown();// 工人回收
			});
		}

		public void stop() {
			this.submit.cancel(true);
		}

		protected void execute(T t) {
			try {
				workExecutor.execute(t);
				workAdpaterCallBackend.call(t, null);
			} catch (final Exception e) {
				if (workAdpaterCallBackend != null) {
					workAdpaterCallBackend.call(t, e);
				}
			}

		}
	}

}
