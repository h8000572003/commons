
package io.github.h800572003.scheduling;

import io.github.h800572003.date.DateUtlis;
import io.github.h800572003.exception.ApBusinessException;
import io.github.h800572003.exception.CancelExecpetion;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.scheduling.TaskScheduler;

import java.util.Date;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
public abstract class AbstractSchedulingCronContextHolder
        implements ISchedulingItemContext, IScheduingTaskContext, Runnable {
    protected IScheduingKey scheduingKey;
    protected TaskScheduler taskScheduler;
    protected IScheduingTask scheduingTask;
    protected String message = "";
    protected ScheduledFuture<?> scheduledFuture = null;
    protected String startTime = "";
    protected String endTime = "";
    protected SchedulingStatusCodes status = SchedulingStatusCodes.IDLE;
    protected IScheduingMonitor monitors;
    protected ISchedulingContext mainContext;
    protected boolean isUp = true;
    protected static byte[] UP_LOCK = new byte[]{};
    private int progress = -1;

    protected final boolean isTaskCancel;

    public AbstractSchedulingCronContextHolder(IScheduingKey scheduingKey, TaskScheduler taskScheduler,
                                               IScheduingTask scheduingTask, IScheduingMonitor monitors, ISchedulingContext mainContext, boolean isTaskCancel) {
        this.scheduingKey = scheduingKey;
        this.taskScheduler = taskScheduler;
        this.scheduingTask = scheduingTask;
        this.monitors = monitors;
        this.mainContext = mainContext;
        this.isTaskCancel = isTaskCancel;

    }

    @Override
    public void runOnce() {
        log.info("call runOnce code:{} ", this.scheduingKey.getCode());
        if (this.scheduledFuture == null) {
            this.scheduledFuture = this.taskScheduler.schedule(() -> {
                try {
                    AbstractSchedulingCronContextHolder.this.run();
                } finally {
                    this.scheduledFuture = null;
                }
            }, new Date());
        } else {
            throw new ApBusinessException("服務已啟動，不執行一次執行");
        }
    }

    @Override
    public final void start() {
        this.myStart();
    }

    protected abstract void myStart();

    @Override
    public final void cancel() {
        synchronized (this) {
            this.mainContext.ifExecuter(() -> {
                this.setUp(false);
                log.info("call {}服務中斷", this.scheduingKey.getCode());
                if (this.scheduledFuture != null && !this.scheduledFuture.isCancelled()) {
                    log.info("call cancel code:{} ", this.scheduingKey.getCode());
                    final boolean cancel = this.scheduledFuture.cancel(isTaskCancel);
                    if (cancel && !this.status.equals(SchedulingStatusCodes.RUNNNIG)) {
                        termiat();
                    }
                } else {
                    log.info("{}服務已關閉", this.scheduingKey.getCode());
                }
            });
        }

    }

    private void termiat() {
        try {
            log.info("服務{},中止開始", this.scheduingKey.getCode());
            log.info("termiat code:{} ", this.scheduingKey.getCode());
            this.status = SchedulingStatusCodes.DEAD;
            this.scheduledFuture = null;
            this.monitors.updateCancel(this);
            log.info("服務{},中止成功", this.scheduingKey.getCode());
        } finally {
            log.info("服務{},中止結束", this.scheduingKey.getCode());
        }

    }

    public final void refresh() {
        synchronized (this) {
            this.myRefresh();
        }

    }

    protected abstract void myRefresh();

    @Override
    public final void run() {
        synchronized (this.scheduingKey.getPClass()) {
            try {
                TaskHolder.set(this);
                this.setUp(true);
                this.setProgress(-1);
                this.message = "";
                log.info("code:{} start", this.scheduingKey.getCode());
                this.startTime = DateUtlis.getText();
                this.endTime = "";
                this.status = SchedulingStatusCodes.RUNNNIG;

                this.monitors.updateStart(this);
                this.scheduingTask.execute(this);
                // this.message = "OK";
            } catch (final Exception e) {
                this.monitors.updateError(this, e);
                this.message = ExceptionUtils.getStackTrace(e);
                log.error("code:{} error", this.scheduingKey.getCode(), e);
            } finally {

                if (this.scheduledFuture != null) {
                    if (this.scheduledFuture.isCancelled()) {
                        this.status = SchedulingStatusCodes.DEAD;
                        termiat();
                    } else {
                        this.status = SchedulingStatusCodes.RUNNABLE;

                    }
                } else {
                    this.status = SchedulingStatusCodes.IDLE;
                }
                this.endTime = DateUtlis.getText();
                log.info("code:{} end", this.scheduingKey.getCode());
                this.monitors.updateEnd(this);
                TaskHolder.clear();
            }
        }
    }

    @Override
    public String getStartTime() {
        return this.startTime;
    }

    @Override
    public String getEndTime() {
        return this.endTime;
    }

    @Override
    public String getStatus() {
        return this.status.name();
    }

    @Override
    public String getCode() {
        return this.scheduingKey.getCode();
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public abstract String getCron();

    @Override
    public String getName() {
        return this.scheduingKey.getName();
    }

    @Override
    public boolean isActived() {
        return this.scheduingKey.isActived();
    }

    @Override
    public String getNextSec() {
        if (this.scheduledFuture == null) {
            return "";

        }
        return this.scheduledFuture.getDelay(TimeUnit.SECONDS) + "";
    }

    @Override
    public void destroy() {
        synchronized (this) {
            setUp(false);
            log.info("destroy code:{}", this.scheduingKey.getCode());
            this.mainContext.ifExecuter(() -> this.monitors.destroy(this));
            log.info("服務已回收成功{}", this.scheduingKey.getCode());
        }

    }

    @Override
    public void updateMessage(String message) {
        this.message = message;
    }

    @Override
    public void checkUp() throws CancelExecpetion {
        synchronized (UP_LOCK) {
            if (isUp) {
            } else {
                this.isUp = true;
                throw new CancelExecpetion("程式中斷");
            }
        }
    }

    protected void setUp(boolean isUp) {
        synchronized (UP_LOCK) {
            this.isUp = isUp;
        }

    }

    protected boolean getUp() {
        synchronized (UP_LOCK) {
            return this.isUp;
        }

    }

    @Override
    public void setProgress(int progress) {
        if (progress > 100) {
            this.progress = 100;
        } else {
            this.progress = progress;
        }

    }

    @Override
    public int getProgress() {
        return this.progress;
    }
}