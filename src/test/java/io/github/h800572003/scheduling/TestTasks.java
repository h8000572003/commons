package io.github.h800572003.scheduling;

import io.github.h800572003.exception.ApBusinessException;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class TestTasks {

    private final StatusCache statusCache;
    @Getter
    private final List<BaseLatchTask> tasks = new ArrayList<>();
    private final Map<String, BaseLatchTask> taskMap = new HashMap<>();

    public TestTasks(int size) {
        this.statusCache = new StatusCache(size);
        this.add(new LongTimeLoopTask(this.statusCache));
        this.add(new LongTimeTask(this.statusCache));
        if (tasks.size() != size) {
            throw new ApBusinessException("tasks size is not equal to" + size);
        }
    }

    public <T> T getTask(Class<T> pClass) {
        return (T) taskMap.get(pClass.getName());
    }

    public void add(BaseLatchTask baseLatchTask) {
        taskMap.put(baseLatchTask.getClass().getName(), baseLatchTask);
        tasks.add(baseLatchTask);
    }

}
