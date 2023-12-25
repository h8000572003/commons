package io.github.h800572003.scheduling;

public interface ISchedulingConfigRepository {
    boolean getCancel(AbstractSchedulingCronContextHolder abstractSchedulingCronContextHolder);

    default ISchedulingItemContext newItem(ISchedulingItemContext schedulingItemContext) {
        return schedulingItemContext;
    }
}
