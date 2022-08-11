package io.github.h800572003.scheduling;

import io.github.h800572003.exception.ApBusinessException;
import org.apache.commons.lang3.StringUtils;

import lombok.Data;

@Data
public class Scheduling {

	private String type;
	private String code;
	private String name;
	private String cron;
	private boolean actived;

	public SchedulinChangedEvent change(SchuduingChangeInputDTO schuduingChangeInputDTO) {
		if (StringUtils.isBlank(schuduingChangeInputDTO.getCron())) {
			throw new ApBusinessException("排程時間不得空白");
		}
//		if (StringUtils.isBlank(schuduingChangeInputDTO.getName())) {
//			throw new ApBusinessExecpetion("名稱不得空白");
//		}
		SchedulinChangedEvent schedulingCronChangedEvent = new SchedulinChangedEvent();
		schedulingCronChangedEvent.setCode(this.code);
		schedulingCronChangedEvent.setCron(schuduingChangeInputDTO.getCron());
		schedulingCronChangedEvent.setActived(schuduingChangeInputDTO.isActived());
		this.handle(schedulingCronChangedEvent);
		return schedulingCronChangedEvent;
	}

	public void handle(SchedulinChangedEvent schedulingCronChangedEvent) {
		this.cron = schedulingCronChangedEvent.getCron();
//		this.name = schedulingCronChangedEvent.getName();
		this.actived = schedulingCronChangedEvent.isActived();
	}
}
