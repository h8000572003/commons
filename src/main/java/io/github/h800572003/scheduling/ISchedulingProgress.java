package io.github.h800572003.scheduling;

public interface ISchedulingProgress {
    void setTotal(int total);

    void incrementCounter();


    void reset();

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
