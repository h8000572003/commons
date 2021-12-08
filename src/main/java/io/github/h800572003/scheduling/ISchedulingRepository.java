package io.github.h800572003.scheduling;

import java.util.List;

public interface ISchedulingRepository {

	List<IScheduingDelay> getDelayTask();

	List<IScheduingCron> getCcheduingCronsTask();

	IScheduingCron getCcheduingCronsTask(String code);

	IScheduingDelay getCcheduingDelayTask(String code);

	Scheduling findByCode(String code);

	void update(SchedulinChangedEvent event);

}
