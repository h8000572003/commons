package io.github.h800572003.scheduling;

import org.apache.commons.lang3.StringUtils;

public class TaskHolder {

	private static ThreadLocal<ISchedulingItemContext> LOCAL = new ThreadLocal<>();

	public static void set(ISchedulingItemContext context) {
		LOCAL.set(context);
	}

	/**
	 * 取得作業代碼，若空白表示後端共用服務，非排程
	 * 
	 * @return
	 */
	public static String getName() {
		ISchedulingItemContext iSchedulingItemContext = LOCAL.get();
		if (iSchedulingItemContext == null) {
			return StringUtils.EMPTY;
		} else {
			return iSchedulingItemContext.getCode();
		}
	}

	public static void clear() {
		LOCAL.remove();
	}
}
