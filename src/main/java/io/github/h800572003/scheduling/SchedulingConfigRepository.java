package io.github.h800572003.scheduling;


import org.springframework.beans.factory.annotation.Value;

import java.util.List;

public class SchedulingConfigRepository implements ISchedulingConfigRepository {

    @Value("${backend.task.cancel:false}")
    private boolean isTaskCancel;
    @Value("${backend.task.interrupt.codes:}#{T(java.util.Collections).emptyList()}")
    private List<String> interruptTask;


    @Value("${backend.task.double-click-one:true}")
    private boolean isDoubleClick;

    @Override
    public boolean getCancel(AbstractSchedulingCronContextHolder abstractSchedulingCronContextHolder) {
        if (interruptTask.contains(abstractSchedulingCronContextHolder.getCode())) {
            return true;
        }
        return isTaskCancel;
    }

    @Override
    public boolean isDoubleClickOneRunningCancel() {
        return isDoubleClick;
    }
}
