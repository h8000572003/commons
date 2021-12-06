package io.github.h800572003.commons.batch;

public interface IBactchTaskConfig {

	String getCode();

	String getName();

	long getPeriod();

	long getCloseTimeout();

	/**
	 * 第一次延遲多久
	 * 
	 * @return
	 */
	long getDelay();

}