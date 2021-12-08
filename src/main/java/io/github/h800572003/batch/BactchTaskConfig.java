package io.github.h800572003.batch;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = { "code" })
public class BactchTaskConfig implements IBactchTaskConfig {

	private String code;
	private String name;
	private long period;
	private long closeTimeout;
	private long delay;

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public long getPeriod() {
		return period;
	}

	@Override
	public long getCloseTimeout() {
		return closeTimeout;
	}

	@Override
	public long getDelay() {
		return delay;
	}

}
