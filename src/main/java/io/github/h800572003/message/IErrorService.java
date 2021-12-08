package io.github.h800572003.message;

public interface IErrorService {
	/**
	 * 發送[Exception]錯誤訊息
	 * 
	 * @param throwable
	 */
	public void sendUrgentMessage(Throwable throwable);

	/**
	 * 發送一搬信件
	 * 
	 * @param errorText
	 */
	public void sendMessage(IErrorText errorText);

	/**
	 * 發送[緊急]訊息
	 * 
	 * @param errorText
	 */
	public void sendUrgentMessage(IErrorText errorText);

	/**
	 * 錯誤文件
	 * 
	 * @author 6407
	 *
	 */
	static public interface IErrorText {

		String toMessage();

		String toKeyWord();
	}
}
