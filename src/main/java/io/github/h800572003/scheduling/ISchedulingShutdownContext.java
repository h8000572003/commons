package io.github.h800572003.scheduling;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 排程關閉逾時資訊
 *
 * @author andy tsai
 */
public interface ISchedulingShutdownContext {

	/**
	 * 已等待的關閉時間(秒)
	 *
	 * @return closeTimeout
	 */
	int getCloseTimeout();

	/**
	 * 執行中的任務
	 *
	 * @return 狀態為執行中的任務
	 */
	List<ISchedulingItemContext> getRunningTasks();

	/**
	 * 尚存活的排程執行緒
	 *
	 * @return 執行緒
	 */
	List<Thread> getAliveThreads();

	/**
	 * 中斷執行緒池所有執行緒
	 */
	void interrupt();

	/**
	 * 等待執行緒池終止
	 *
	 * @param timeout 等待時間
	 * @param unit    時間單位
	 * @return 是否已終止
	 * @throws InterruptedException 等待被中斷
	 */
	boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException;

	/**
	 * 執行緒池是否已終止
	 *
	 * @return 是否已終止
	 */
	boolean isTerminated();
}
