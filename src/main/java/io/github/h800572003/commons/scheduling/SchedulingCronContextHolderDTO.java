package io.github.h800572003.commons.scheduling;

import java.util.Calendar;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import io.github.h800572003.commons.ApBusinessExecpetion;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SchedulingCronContextHolderDTO extends AbstractSchedulingCronContextHolder
		implements ISchedulingItemContext, Runnable {

	IScheduingCron scheduingCron;
	ISchedulingRepository schedulingRepository;

	public SchedulingCronContextHolderDTO(IScheduingCron scheduingCron, TaskScheduler taskScheduler,
			IScheduingTask task, MyScheduingMonitors scheduingTaskLog,
			ISchedulingRepository schedulingRepository, ISchedulingContext schedulingContext) {

		super(scheduingCron, taskScheduler, task, scheduingTaskLog, schedulingContext);
		this.scheduingCron = Objects.requireNonNull(scheduingCron);
		this.taskScheduler = Objects.requireNonNull(taskScheduler);
		this.schedulingRepository = schedulingRepository;

	}

	@Override
	protected void myStart() {
		log.debug("呼叫啟動服務:{}開始", this.scheduingKey.getCode());
		synchronized (this) {
			try {
				this.mainContext.ifExecuter(() -> {
					if (this.scheduingCron.isActived()) {
						if (this.scheduledFuture == null) {
							try {
								if (StringUtils.isNumeric(this.scheduingCron.getCon())) {
									final Calendar instance = Calendar.getInstance();
									instance.add(Calendar.SECOND, 10);
									this.scheduledFuture = this.taskScheduler.scheduleWithFixedDelay(this,
											instance.getTime(), Long.parseLong(this.scheduingCron.getCon()));
								} else {
									final CronTrigger t = new CronTrigger(this.scheduingCron.getCon());
									this.scheduledFuture = this.taskScheduler.schedule(this, t);
								}
								this.status = SchedulingStatusCodes.RUNNABLE;
							} catch (final TaskRejectedException e) {
								throw new ApBusinessExecpetion("已關機不提供作業新增{0}", this.scheduingKey.getCode());
							}
						} else {
							throw new ApBusinessExecpetion("{0}已啟動，請先關閉", this.scheduingKey.getCode());
						}
					}
				});
			} finally {
				log.debug("呼叫啟動服務:{}結束", this.scheduingKey.getCode());
			}

		}
	}

	@Override
	public String getCron() {
		return this.scheduingCron.getCon();
	}

	@Override
	public void myRefresh() {
		synchronized (this) {
			if (this.scheduledFuture != null) {
				throw new ApBusinessExecpetion("{0}已啟動，請先關閉", this.scheduingKey.getCode());
			}
			this.scheduingCron = this.schedulingRepository.getCcheduingCronsTask(this.scheduingCron.getCode());
			log.info("服務更新成功{}", this.scheduingKey.getCode());
		}
	}

	@Override
	public boolean isActived() {
		return this.scheduingCron.isActived();
	}

	@Override
	public String getName() {
		return this.scheduingCron.getName();
	}
}
