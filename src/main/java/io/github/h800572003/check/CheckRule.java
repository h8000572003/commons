package io.github.h800572003.check;

import java.util.function.Predicate;

/**
 * 
 * 檢查規則
 * 
 * @author andy
 *
 */
public class CheckRule {

	private String message = null;
	private String code = null;
	private boolean isOk = true;

	public boolean isError() {
		return !isOk;
	}

	private CheckRule(String code, String message) {
		super();
		this.code = code;
		this.message = message;
		this.isOk = true;
	}

	/**
	 * 
	 * @param code
	 * @param message
	 * @param obj
	 * @return
	 */
	public static CheckRule of(String code, String message, Predicate<Object> obj) {
		CheckRule checkStatus = new CheckRule(code, message);
		try {
			boolean isOk = obj.test(checkStatus);
			if (!isOk) {
				checkStatus.error();
			}
			return checkStatus;
		} catch (Exception e) {
			checkStatus.error();
		}
		return checkStatus;

	}

	public static CheckRule of(String code, Predicate<Object> obj) {
		return of(code, "", obj);
	}

	protected void ok() {
		this.isOk = true;

	}

	protected void error() {
		this.isOk = false;
	}

	public String getMessage() {
		return message;
	}

	public String getCode() {
		return code;
	}

	public boolean isOk() {
		return isOk;
	}

}
