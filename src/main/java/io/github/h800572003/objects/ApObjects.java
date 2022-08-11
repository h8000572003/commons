package io.github.h800572003.objects;

import io.github.h800572003.exception.ApBusinessException;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Ap物件檢查
 * 
 * @author andy
 *
 */
public class ApObjects {

	protected static final String DEFALUT_MESSAGE_CONFIG = ".defalutMessage";
	private final static String DEFALUT_MESSAGE = System.getProperty(ApObjects.class.getName() + DEFALUT_MESSAGE_CONFIG,
			"不得空值");

	public static <T> T requireNonNull(T obj) {
		if (obj == null) {
			throw new ApBusinessException(DEFALUT_MESSAGE);
		}
		return obj;
	}

	public static <T> T requireNonNull(T obj, String pattern, Object... arguments) {
		if (obj == null) {
			throw new ApBusinessException(pattern, arguments);
		}
		return obj;
	}

	public static <T> T requireNonNull(T obj, String pattern) {
		if (obj == null) {
			throw new ApBusinessException(pattern, pattern);
		}
		return obj;
	}

	public static <T> T requireNonNull(T obj, Supplier<String> messageSupplier) {
		if (obj == null) {
			throw new ApBusinessException(messageSupplier.get());
		}
		return obj;
	}

	public static <T> T check(T obj, Predicate<T> predicate, String message) {

		if (!predicate.test(obj)) {
			throw new ApBusinessException(message);
		}
		return obj;

	}

	public enum CheckRule {
		IS_NOT_NULL(i -> i != null),//

		;

		Predicate<Object> predicate;

		private <T> CheckRule(Predicate<Object> predicate) {
			this.predicate = predicate;
		}

	}

}
