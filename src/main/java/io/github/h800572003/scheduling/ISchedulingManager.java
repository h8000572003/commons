package io.github.h800572003.scheduling;

/**
 * 排程管理介面
 * 
 * @author 6407
 *
 */
public interface ISchedulingManager {

	/**
	 * 參數變更
	 */
	void propertiesChange();

	/**
	 * 
	 * 更新資訊
	 * 
	 * @param code
	 *            代碼
	 */
	void refresh(String code);

	/**
	 * 個別啟動
	 * 
	 * @param code
	 *            代碼
	 */
	void start(String code);

	/**
	 * 執行一次
	 * 
	 * @param code
	 *            代碼
	 */
	void runOnce(String code);

	/**
	 * 關閉
	 * 
	 * @param code
	 *            代碼
	 */
	void cancel(String code);

	/**
	 * 全部關閉
	 */
	void cancelAll();

	/**
	 * 啟動全部
	 */
	void startAll();

	/**
	 * 取得相關資訊
	 * 
	 * @return ISchedulingContext 內容
	 */
	ISchedulingContext getContext();

	/**
	 * 排程服務關閉
	 */
	void down();

	/**
	 * 排程服務啟動
	 */
	void up();
}
