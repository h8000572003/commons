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
}
