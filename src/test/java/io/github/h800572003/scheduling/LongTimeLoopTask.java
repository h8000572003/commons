package io.github.h800572003.scheduling;

import io.github.h800572003.exception.CancelExecpetion;
import lombok.extern.slf4j.Slf4j;

/**
 * {milliSeconds}
 */
@Slf4j
public class LongTimeLoopTask extends BaseLatchTask implements IScheduingTask {


    private final int loopTime;//loop times


    public LongTimeLoopTask(StatusCache statusCache) {
        super(statusCache);
        this.loopTime = 10;
    }

    @Override
    public void myExecute(IScheduingTaskContext schedulingTaskContext) {

        for (int i = 0; i < loopTime; i++) {
            log.info("index: " + i);
            try {
                schedulingTaskContext.checkUp();
                this.executeMilliSeconds();
            } catch (CancelExecpetion e) {
                log.info("中斷程式碼清單", e);
                break;
            }
        }
        incrementOkTimes();
        this.setDoneOk(true);
        log.info("end job");

    }


}
