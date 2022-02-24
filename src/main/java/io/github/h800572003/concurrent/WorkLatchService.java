package io.github.h800572003.concurrent;

import java.io.Closeable;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import io.github.h800572003.exception.ApBusinessException;
import lombok.extern.slf4j.Slf4j;

/**
 * 門閂工人服務
 * 
 * @author andy tsai
 *
 * @param <T> 鎖定物件
 */
@Slf4j
public class WorkLatchService<T> implements Closeable, IWorkService<T> {

	private CountDownLatch countDownLatch;
	protected final WorkExecutor<T> workExecutor;
	protected final WorkAdpaterCallBackend<T> workAdpaterCallBackend;
	protected final String prefName;// 執行序名稱
	protected final int workSize;

	protected IWorkPool workPool;

	public static <T> WorkLatchService<T> newService(String prefName, IQueue<T> queue, int workSize,
			WorkExecutor<T> workListener, WorkAdpaterCallBackend<T> workAdpaterCallBackend) {
		return new WorkLatchService<>(prefName, workSize, workListener, workAdpaterCallBackend);
	}

	protected WorkLatchService(String prefName, int workSize, WorkExecutor<T> workListener,
			WorkAdpaterCallBackend<T> workAdpaterCallBackend) {
		this.prefName = prefName;
		this.workExecutor = workListener;
		this.workAdpaterCallBackend = workAdpaterCallBackend;
		this.workSize = workSize;
	}

	@Override
	public void close() {
		this.workPool.close();
		log.debug("close down");
	}

	/**
	 * 堵住，執行清單完成，才繼續
	 */
	@Override
	public void execute(IQueue<T> queue, List<? extends T> items) throws InterruptedException {

		if (countDownLatch != null) {
			throw new ApBusinessException("每次僅此執行一次");
		}
		this.countDownLatch = new CountDownLatch(items.size());//
		items.forEach(i -> {
			queue.add(i);
		});
		this.workPool = this.createPool(queue);

		this.workPool.start();
		this.countDownLatch.await();
		log.debug("item down done");
	}

	/**
	 * 工人池工廠
	 * 
	 * @param queue
	 * @return 工人池
	 */
	protected IWorkPool createPool(IQueue<T> queue) {
		IWorkPool workPool = new CountDownLatchWorkPool<>(//
				this.prefName, //
				this.workSize, //
				this.workExecutor, //
				this.countDownLatch, //
				queue, //
				this.workAdpaterCallBackend);
		return workPool;
	}

	@Override
	public void execute(List<? extends T> items) throws InterruptedException {
		this.execute(new BlockQueue<T>(100), items);
	}

}
