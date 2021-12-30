package io.github.h800572003.machine;

/**
 * 心情
 * 
 * @author andy tsai
 *
 */
public enum FeelCodes implements IStatus {

	HAPPY,//
	SAD,//
	ANGER,//
	;

	@Override
	public String toStatus() {
		return name();
	}
}
