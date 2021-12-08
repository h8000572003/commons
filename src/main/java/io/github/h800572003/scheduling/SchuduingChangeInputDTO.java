package io.github.h800572003.scheduling;

import lombok.Data;

@Data
public class SchuduingChangeInputDTO {
	private String code;
	private String cron;
	private boolean actived;

}
