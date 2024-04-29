package io.github.h800572003.scheduling;

import org.apache.commons.lang3.StringUtils;

public class TaskHolder {

    private static ThreadLocal<String> LOCAL = new ThreadLocal<>();

    public static void set(ISchedulingItemContext context) {
        LOCAL.set(context.getCode());
    }

    public static void set(String code) {
        LOCAL.set(code);
    }

    /**
     * 取得作業代碼，若空白表示後端共用服務，非排程
     *
     * @return
     */
    public static String getName() {
        return StringUtils.defaultString(LOCAL.get());
    }

    public static void clear() {
        LOCAL.remove();
    }
}
