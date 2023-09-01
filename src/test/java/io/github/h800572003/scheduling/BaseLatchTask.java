package io.github.h800572003.scheduling;

import io.github.h800572003.exception.ApBusinessException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public abstract class BaseLatchTask implements IScheduingTask, IScheduingCron {

    private final StatusCache statusCache;
    @Setter

    private  int milliSeconds;
    private boolean isDone = false;

    @Setter
    @Getter
    private boolean isDoneOk = false;

    private final AtomicInteger times = new AtomicInteger();
    private final AtomicInteger okTimes = new AtomicInteger();

    private CountDownLatch self = new CountDownLatch(1);


    private final AtomicInteger counts = new AtomicInteger();

    public BaseLatchTask(StatusCache statusCache, int milliSeconds) {
        this.statusCache = statusCache;
        this.milliSeconds = milliSeconds;

    }

    public BaseLatchTask(StatusCache statusCache) {
        this(statusCache, 1);
    }

    public void refreshLatchSize(int latch) {
        self = new CountDownLatch(latch);
    }

    @Override
    public void execute(IScheduingTaskContext context) {

        setDoneOk(false);
        try {

            log.info("{} Executing  running", getClass().getSimpleName());
            statusCache.countDown(Status.RUNNING);
            this.myExecute(context);
        } finally {
            counts.decrementAndGet();
            isDone = true;
            times.incrementAndGet();
            log.info("{} Finished", getClass().getSimpleName());
            statusCache.countDown(Status.FIN);
            self.countDown();
        }
    }


    abstract void myExecute(IScheduingTaskContext context);


    protected void executeMilliSeconds() {

        try {
            TimeUnit.MILLISECONDS.sleep(this.milliSeconds);
        } catch (InterruptedException e) {
            throw new ApBusinessException("服務中斷");
        }


    }

    @Override
    public boolean isActived() {
        return true;
    }

    @Override
    public Class<? extends IScheduingTask> getPClass() {
        return this.getClass();
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getCode() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getCon() {
        return String.valueOf(1000000000L);
    }

    public boolean isDone() {
        return isDone;
    }

    /**
     * 執行次數
     *
     * @return
     */
    public int getTimes() {
        return times.get();
    }

    public void awaitSelfLatch() {
        try {
            self.await();
        } catch (InterruptedException e) {
            throw new ApBusinessException("服務中斷");
        }
    }

    protected int incrementOkTimes() {
        return okTimes.incrementAndGet();
    }

    public int getOkTimes() {
        return okTimes.get();
    }

}
