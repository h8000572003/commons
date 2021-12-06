package io.github.h800572003.commons.scheduling;

/**
 * 服務介面
 * 
 * @author 6407
 *
 */
public interface ISchedulingItemContext {

	/**
	 * 執行一次
	 */
	void runOnce();

	/**
	 * 啟動
	 */
	void start();

	/**
	 * 關閉
	 */
	void cancel();

	void refresh();

	// /**
	// * 中斷
	// */
	// void interupt();

	void destroy();

	String getStartTime();

	String getEndTime();

	String getStatus();

	String getCode();

	String getMessage();

	String getCron();

	String getName();

	String getNextSec();

	boolean isActived();

	/**
	 * 處理進度
	 * 
	 * @return
	 */
	int getProgress();

}
