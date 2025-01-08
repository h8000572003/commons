package io.github.h800572003;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SchuedTest {

    public static void main(String[] args) {
        Executors.newScheduledThreadPool(10);


        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10, r -> {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            return thread;
        });


        executorService.schedule(() -> {
            for (int i = 0; i < 1; i++) {
                try {
                    log.info("i:{} ", i);
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

        }, 2, TimeUnit.SECONDS);





        try {
            TimeUnit.SECONDS.sleep(120);
            executorService.shutdown();
            log.info("Finished all threads");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdownNow();
        }


    }


}
