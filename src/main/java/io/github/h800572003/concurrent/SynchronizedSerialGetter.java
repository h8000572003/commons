package io.github.h800572003.concurrent;

import org.apache.commons.lang3.StringUtils;

/**
 * 序號產生非隨機 0~maxSize
 * 
 * @author andy tsai
 *
 */
public class SynchronizedSerialGetter implements IValueGetter {

	int nowValue = 0;
	int maxSize = 999;
	int length = 3;
	private String padString = "0";

	public SynchronizedSerialGetter(int initValue, int maxSize, int length, String padString) {
		super();
		this.nowValue = initValue;
		this.maxSize = maxSize;
		this.length = length;
		this.padString = padString;
	}

	@Override
	public synchronized String getValue() {

		if (this.nowValue > this.maxSize) {
			this.nowValue = 0;
		}
		this.nowValue++;
		final StringBuilder buffer = new StringBuilder();
		buffer.append(StringUtils.leftPad(String.format("%d", this.nowValue), this.length, this.padString));
		return buffer.toString();
	}

}
