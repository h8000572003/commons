package io.github.h800572003.check;

import java.util.Objects;
import java.util.function.BooleanSupplier;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * 檢查規則
 * 
 * @author andy
 *
 */
public class CheckResult {

	private String message = null;
	private String code = null;
	private boolean isOk = true;

	private CheckResult(String code, String message) {
		super();
		this.code = Objects.requireNonNull(code);
		this.message = Objects.requireNonNull(message);
		this.isOk = true;
	}

	/**
	 * 
	 * @param code
	 *            代碼
	 * @param message
	 *            訊息
	 * @param checkPredicate
	 *            檢查規則
	 * @return
	 */
	public static CheckResult of(String code, String message, BooleanSupplier checkPredicate) {
		final CheckResult checkStatus = new CheckResult(code, message);
		try {
			if (!checkPredicate.getAsBoolean()) {
				checkStatus.error();
			}
			return checkStatus;
		} catch (final Exception e) {
			checkStatus.error();
		}
		return checkStatus;

	}

	public static CheckResult of(String code, BooleanSupplier checkPredicate) {
		return of(code, StringUtils.EMPTY, checkPredicate);
	}

	protected void ok() {
		this.isOk = true;
	}

	protected void error() {
		this.isOk = false;
	}

	public boolean isError() {
		return !this.isOk;
	}

	public String getMessage() {
		return this.message;
	}

	public String getCode() {
		return this.code;
	}

	public boolean isOk() {
		return this.isOk;
	}

}
