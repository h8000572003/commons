package io.github.h800572003.scheduling;

import io.github.h800572003.exception.ApBusinessException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.awaitility.Awaitility;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public abstract class BaseLatchTask implements IScheduingTask, IScheduingCron {

    private final StatusCache statusCache;
    private final int milliSeconds;
    private boolean isDone = false;

    @Setter
    @Getter
    private boolean isDoneOk = false;

    private final AtomicInteger times = new AtomicInteger();

    private CountDownLatch self;

    private final AtomicInteger counts = new AtomicInteger();
    private final AtomicBoolean singleDoor = new AtomicBoolean(false);

    public BaseLatchTask(StatusCache statusCache, int milliSeconds) {
        this.statusCache = statusCache;
        this.milliSeconds = milliSeconds;

    }

    public BaseLatchTask(StatusCache statusCache) {
        this(statusCache, 1);
    }

    @Override
    public void execute(IScheduingTaskContext context) {
        if (!singleDoor.get()) {
            singleDoor.set(true);
        } else {
            throw new ApBusinessException("任務同時執行");
        }
        setDoneOk(false);
        try {
          this.getCountDownLatch();


            log.info("{} Executing  running", getClass().getSimpleName());
            statusCache.countDown(Status.RUNNING);
            this.myExecute(context);
        } finally {
            counts.decrementAndGet();
            isDone = true;
            times.incrementAndGet();
            statusCache.countDown(Status.FIN);

            if (singleDoor.get()) {
                singleDoor.set(false);
            } else {
                throw new ApBusinessException("任務同時執行");
            }
            self.countDown();
        }
    }
    private synchronized CountDownLatch  getCountDownLatch(){
        if(self==null){
            self = new CountDownLatch(1);
        }
        return this.self;
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

    public void awaitSelf() {
        try {
            getCountDownLatch().await();
        } catch (InterruptedException e) {
            throw new ApBusinessException("服務中斷");
        }
    }


}
