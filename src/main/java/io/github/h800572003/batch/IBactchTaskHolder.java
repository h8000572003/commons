package io.github.h800572003.batch;

public interface IBactchTaskHolder extends IBactchTaskConfig {

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

	String getNextSec();

}
