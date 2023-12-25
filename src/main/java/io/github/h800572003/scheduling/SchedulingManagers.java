package io.github.h800572003.scheduling;

import java.util.Objects;

import org.springframework.context.ApplicationContext;

import io.github.h800572003.scheduling.SpringSchedulingManager.ISpringSchedulingProperites;
/**
 * 排程服務集合
 * @author andy tsai
 *
 */
public class SchedulingManagers {

	static ISchedulingManager createSchedulingManager(ISchedulingRepository schedulingRepository,
			ApplicationContext applicationContext, IScheduingMonitor myScheduingMonitors,
			ISpringSchedulingProperites springSchedulingProperites,ISchedulingConfigRepository schedulingConfigRepository) {
		Objects.requireNonNull(schedulingRepository);
		Objects.requireNonNull(applicationContext);
		Objects.requireNonNull(myScheduingMonitors);
		Objects.requireNonNull(springSchedulingProperites);
		return new SpringSchedulingManager(schedulingRepository, applicationContext, myScheduingMonitors,
				springSchedulingProperites,schedulingConfigRepository);
	}
}
