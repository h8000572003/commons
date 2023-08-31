package io.github.h800572003.scheduling;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class LongTimeTask extends BaseLatchTask implements IScheduingTask {



    public LongTimeTask(StatusCache statusCache) {
        super(statusCache);
    }


    @Override
    public void myExecute(IScheduingTaskContext context) {
        context.setProgress(0);
        this.executeMilliSeconds();
        context.setProgress(50);
        this.executeMilliSeconds();
        context.setProgress(100);
        this.setDoneOk(true);
    }


}
