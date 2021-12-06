package io.github.h800572003.commons.scheduling;

import lombok.Data;

@Data
public class SchedulinChangedEvent {
	private String code;
	private String cron;
	private boolean actived;
}
