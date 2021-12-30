package io.github.h800572003.concurrent;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.stream.IntStream;

import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import com.google.common.collect.Lists;

import io.github.h800572003.exception.ApBusinessException;
import lombok.extern.slf4j.Slf4j;

/**
 * 倒數工作池[任務完成關閉作業]
 * 
 * @author andy tsai
 *
 * @param <T>
 */
@Slf4j
public class CountDownLatchWorkPool<T> implements IWorkPool {

	private List<Work> works = null;
	private final CountDownLatch countDownLatch;
	private final CountDownLatch workDownLatch;
	private final IQueue<T> queue;
	private final WorkExecutor<T> workExecutor;
	private final String name;
	private final WorkAdpaterCallBackend<T> workAdpaterCallBackend;
	private final int workSize;
	private final ExecutorService executorService;
	private volatile ThreadFactory threadFactory;

	public CountDownLatchWorkPool(String name, //
			int workSize, //
			WorkExecutor<T> workExecutor, //
			CountDownLatch countDownLatch, //
			IQueue<T> queue, //
			WorkAdpaterCallBackend<T> workAdpaterCallBackend) {//
		super();
		this.threadFactory = new CustomizableThreadFactory(name);
		this.name = name;
		this.workExecutor = workExecutor;
		this.countDownLatch = countDownLatch;
		this.queue = queue;
		this.workSize = workSize;
		this.workDownLatch = new CountDownLatch(workSize);
		this.workAdpaterCallBackend = workAdpaterCallBackend;
		this.executorService = Executors.newFixedThreadPool(workSize, this.threadFactory);

	}

	@Override
	public synchronized void start() {
		if (this.works != null) {
			throw new ApBusinessException("服務已啟動");
		}
		this.works = Lists.newArrayList();
		IntStream.range(0, this.workSize).forEach(i -> {
			this.works.add(this.createWork(i));
		});
	}

	protected Work createWork(int index) {
		return new Work(this.queue, this.name + "_" + index, this.workExecutor);
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
						CountDownLatchWorkPool.this.countDownLatch.countDown();// 項目完成
					}
				}
				CountDownLatchWorkPool.this.workDownLatch.countDown();// 工人回收
			});
		}

		protected void execute(T t) {
			try {
				this.workExecutor.execute(t);
				CountDownLatchWorkPool.this.workAdpaterCallBackend.call(t, null);
			} catch (final Exception e) {
				if (CountDownLatchWorkPool.this.workAdpaterCallBackend != null) {
					CountDownLatchWorkPool.this.workAdpaterCallBackend.call(t, e);
				}
			}

		}

		public void stop() {
			this.submit.cancel(true);
		}

	}

	@Override
	public void close() {
		this.executorService.shutdown();
		if (!executorService.isTerminated()) {
			List<Runnable> shutdownNow = executorService.shutdownNow();
			log.info("shutdownNow size:{}", shutdownNow.size());
		}
		try {
			this.workDownLatch.await();
		} catch (final InterruptedException e) {
			// 中斷直接結束
		}

	}

}
