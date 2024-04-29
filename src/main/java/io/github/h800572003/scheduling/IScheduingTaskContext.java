package io.github.h800572003.scheduling;

import io.github.h800572003.exception.CancelExecpetion;

public interface IScheduingTaskContext {


    /**
     * 檢查有無啟動
     *
     * @throws CancelExecpetion
     */
    void checkUp() throws CancelExecpetion;

    /**
     * 是否執行中
     *
     * @return
     */
    default boolean isRunning() {
        try {
            this.checkUp();
            return true;
        } catch (CancelExecpetion e) {
            return false;
        }
    }

    void updateMessage(String message);

    void setProgress(int progress);

    ISchedulingProgress getSchedulingProgress();
}
