package io.github.h800572003.scheduling;

/**
 * 服務介面
 * 
 * @author andy tsai
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

	/**
	 * 重新整理
	 */
	void refresh();

	/**
	 * 停止
	 */
	void destroy();

	/**
	 * 結束時間
	 * 
	 * @return
	 */
	String getStartTime();

	/**
	 * 結束時間
	 * 
	 * @return
	 */
	String getEndTime();

	/**
	 * 任務狀態
	 * 
	 * @return
	 */
	String getStatus();

	/**
	 * 任務代碼
	 * 
	 * @return
	 */
	String getCode();

	/**
	 * 執行訊息
	 * 
	 * @return
	 */
	String getMessage();

	/**
	 * 任務週期定義
	 * 
	 * @return
	 */
	String getCron();

	/**
	 * 任務名稱
	 * 
	 * @return
	 */
	String getName();

	/**
	 * 下次執行秒數
	 * 
	 * @return
	 */
	String getNextSec();

	/**
	 * 是否激活
	 * 
	 * @return
	 */
	boolean isActived();

	/**
	 * 處理進度
	 * 
	 * @return
	 */
	int getProgress();

}
