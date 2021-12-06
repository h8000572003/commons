package io.github.h800572003.commons.batch;

import java.util.List;

/**
 * 後端服務
 */
public interface IBactchManager {
	/**
	 * 註冊後端服務
	 * 
	 * @param config
	 * @param bactchTaskExecuter
	 */
	void register(IBactchTaskConfig config, IBactchTaskExecuter bactchTaskExecuter);

	/**
	 * 啟動
	 * 
	 * @return
	 */
	boolean isOn();

	/**
	 * 啟動
	 */
	void on();

	/**
	 * 關閉
	 */
	void off();

	void on(String code);

	void off(String code);
	
	List<IBactchTaskHolder>getBactchTasks();
}
