package io.github.h800572003.cmd;

import io.github.h800572003.exception.ApBusinessException;
import org.apache.commons.lang3.StringUtils;

import io.github.h800572003.scheduling.ISchedulingManager;

/**
 * 啟動命令Handler
 * 
 * @author andy tsai
 *
 */
public class UpCmdHander extends BaseCmdHander implements ICmdHander {

	private final ISchedulingManager schedulingManager;

	public UpCmdHander(ICmdService service, //
			ISchedulingManager schedulingManager) {
		super(CmdCodesCofing.Codes.UP.name(), service);
		this.schedulingManager = schedulingManager;
	}

	@Override
	public String cmd(String action) {
		if (StringUtils.isNotBlank(action)) {
			throw new ApBusinessException("無提供此命命:{0}", action);
		} else {
			this.schedulingManager.up();
		}
		return CmdContract.OK;

	}

}
