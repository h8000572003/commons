package io.github.h800572003.scheduling;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.util.concurrent.atomic.AtomicInteger;

public class SchedulingProgress implements ISchedulingProgress {
    private final IScheduingTaskContext context;
    private int total = 0;
    private AtomicInteger counter = new AtomicInteger(0);

    public SchedulingProgress(IScheduingTaskContext context) {
        this.context = context;
        this.setMessage();
    }

    @Override
    public void setTotal(int total) {
        this.total = total;
        this.setMessage();
    }

    @Override
    public void incrementCounter() {
        counter.incrementAndGet();
        this.setMessage();
    }

    @Override
    public void reset() {
        setTotal(0);
        counter.set(0);
        this.setMessage();
    }

    protected int getProgress(int total, AtomicInteger counter) {
        if (total == 0) {
            return 0;
        }
        int i = new BigDecimal(counter.get()).divide(new BigDecimal(total), 2, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100)).setScale(0).intValue();
        return i >= 100 ? 100 : i;
    }

    public String getMessage(int total, AtomicInteger counter) {
        return MessageFormat.format("{0}/{1}", counter.get(), total);
    }

    public void setMessage() {
        int progress = getProgress(total, counter);
        String message = this.getMessage(total, counter);
        context.setProgress(progress);
        context.updateMessage(message);
    }


}
