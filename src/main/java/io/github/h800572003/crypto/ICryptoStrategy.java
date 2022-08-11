package io.github.h800572003.crypto;

/**
 * 加密策略
 * 
 * @author andy 
 *
 */
public interface ICryptoStrategy {
	/**
	 * 加密
	 * 
	 * @param orgValue
	 * @return
	 */
	String toEnCode(String orgValue);

	/**
	 * 解碼
	 * 
	 * @param enCode
	 * @return
	 */
	String toDeCode(String enCode);
}
