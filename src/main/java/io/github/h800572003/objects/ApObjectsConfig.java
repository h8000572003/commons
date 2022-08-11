package io.github.h800572003.objects;

public class ApObjectsConfig {

	/**
	 * 設定系統預設錯誤
	 * 
	 * @param message
	 */
	public static void setDefaluteMessage(String message) {
		System.setProperty(ApObjects.class.getName() + ApObjects.DEFALUT_MESSAGE_CONFIG, message);
	}
}
