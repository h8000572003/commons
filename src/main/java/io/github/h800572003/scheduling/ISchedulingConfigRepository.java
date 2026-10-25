package io.github.h800572003.scheduling;

public interface ISchedulingConfigRepository {
    /**
     * 專斷任務
     *
     * @param abstractSchedulingCronContextHolder
     * @return
     */
    boolean getCancel(AbstractSchedulingCronContextHolder abstractSchedulingCronContextHolder);

    /**
     * SchedulingItemContext 建立
     *
     * @param schedulingItemContext
     * @return
     */
    default ISchedulingItemContext newItem(ISchedulingItemContext schedulingItemContext) {
        return schedulingItemContext;
    }

    /**
     *  執行一次重複點擊關閉任務
     * @return
     */
    default boolean isDoubleClickOneRunningCancel() {
        return false;
    }

    /**
     * 執行一次是否限執行者主機，預設不限制，非執行者主機也可執行一次(供測試)
     *
     * @return true 時非執行者主機執行一次會拋出非執行者無權限操作
     */
    default boolean isRunOnceExecuterOnly() {
        return false;
    }

    /**
     * 取得註冊服務
     * @param pclass
     * @return
     * @param <T>
     */
    default <T> T getService(Class<T> pclass) {
        return null;
    }
}
