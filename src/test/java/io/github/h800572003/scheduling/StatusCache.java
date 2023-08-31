package io.github.h800572003.scheduling;

import com.google.common.collect.Maps;
import io.github.h800572003.exception.ApBusinessException;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class StatusCache {

    private final Map<Status, CountDownLatch> latchMap = Maps.newConcurrentMap();
    private final int size;


    public StatusCache(int size) {
        this.size = size;
    }

    public void countDown(Status status) {
        CountDownLatch latch = getLatch(status);


        latch.countDown();


    }

    public void await(Status status) {
        try {
            synchronized (this) {
                getLatch(status).await();
                this.refresh(status);
            }

        } catch (InterruptedException e) {
            throw new ApBusinessException("Interrupted while waiting");
        }
    }

    public void refresh(Status status) {
        latchMap.put(status, new CountDownLatch(size));
    }

    private CountDownLatch getLatch(Status status) {
        return latchMap.computeIfAbsent(status, i -> new CountDownLatch(size));
    }
}
