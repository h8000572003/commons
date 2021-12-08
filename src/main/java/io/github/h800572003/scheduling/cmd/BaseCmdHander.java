package io.github.h800572003.scheduling.cmd;

public abstract class BaseCmdHander implements ICmdHander {

	public BaseCmdHander(String code, ICmdService service) {
		service.register(code, this);
	}
}
