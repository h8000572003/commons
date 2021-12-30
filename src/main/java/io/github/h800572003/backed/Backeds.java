package io.github.h800572003.backed;

/**
 * 排程服務
 * 
 * @author andy tsai
 *
 */
public class Backeds {

	/**
	 * 
	 * @param name
	 *            服務名稱
	 * @param timeout
	 *            中斷時，等待時間
	 * @return 服務
	 */
	public static IBackendService newCloseTimeoutService(String name, int timeout) {
		return new BackedOnceService(name, timeout);
	}

	/**
	 * 
	 * @param name 服務名稱
	 * @param period
	 *            週期
	 * @param timeout
	 *            關閉期限
	 * @return 服務
	 */
	public static IBackendService newPeriodSerice(String name, long period, int timeout) {
		return new BackedPeriodService(name, period, timeout);
	}
}
