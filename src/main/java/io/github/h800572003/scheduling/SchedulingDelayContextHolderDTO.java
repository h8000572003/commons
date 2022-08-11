package io.github.h800572003.scheduling;

import java.util.Calendar;

import io.github.h800572003.exception.ApBusinessException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import io.github.h800572003.scheduling.SpringSchedulingManager.ISpringSchedulingProperites;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SchedulingDelayContextHolderDTO extends AbstractSchedulingCronContextHolder {

	IScheduingDelay medidata;
	ISchedulingRepository schedulingRepository;
	ISchedulingContext schedulingContext;
	public SchedulingDelayContextHolderDTO(IScheduingDelay medidata, TaskScheduler taskScheduler,
			IScheduingTask task, IScheduingMonitor scheduingTaskLog,
			ISchedulingRepository schedulingRepository, ISchedulingContext schedulingContext) {
		super(medidata, taskScheduler, task, scheduingTaskLog, schedulingContext);
		this.medidata = medidata;
		this.schedulingRepository = schedulingRepository;
		this.schedulingContext=schedulingContext;
	}

	@Override
	protected void myStart() {
		log.debug("呼叫啟動服務:{}開始", this.scheduingKey.getCode());
		synchronized (this) {
			try {
				this.mainContext.ifExecuter(() -> {
					if (this.medidata.isActived()) {
						if (this.scheduledFuture == null) {
							try {

								if (StringUtils.isNumeric(this.medidata.getCron())) {
									final Calendar instance = Calendar.getInstance();
									ISpringSchedulingProperites properites = this.schedulingContext.getProperites();
									instance.add(Calendar.SECOND, properites.getDelayStart());
									this.scheduledFuture = this.taskScheduler.scheduleWithFixedDelay(this,
											instance.getTime(), Long.parseLong(this.medidata.getCron()));
								} else {
									final CronTrigger t = new CronTrigger(this.medidata.getCron());
									this.scheduledFuture = this.taskScheduler.schedule(this, t);
								}

								this.status = SchedulingStatusCodes.RUNNABLE;
								log.info("服務啟動成功{}", this.scheduingKey.getCode());
							} catch (final TaskRejectedException e) {
								throw new ApBusinessException("已關機不提供作業新增{0}", this.scheduingKey.getCode());

							}

						} else {
							throw new ApBusinessException("{0}已啟動，請先關閉", this.scheduingKey.getCode());
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
		return this.medidata.getCron() + "";
	}

	@Override
	public void myRefresh() {
		if (this.scheduledFuture != null) {
			throw new ApBusinessException("{0}已啟動，請先關閉", this.scheduingKey.getCode());
		}
		this.medidata = this.schedulingRepository.getCcheduingDelayTask(this.medidata.getCode());
		log.info("服務更新成功{}", this.scheduingKey.getCode());

	}

	@Override
	public boolean isActived() {
		return this.medidata.isActived();
	}

	@Override
	public String getName() {
		return this.medidata.getName();
	}

}
