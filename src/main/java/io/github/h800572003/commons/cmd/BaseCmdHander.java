package io.github.h800572003.commons.cmd;

public abstract class BaseCmdHander implements ICmdHander {

	public BaseCmdHander(String code, ICmdService service) {
		service.register(code, this);
	}
}
