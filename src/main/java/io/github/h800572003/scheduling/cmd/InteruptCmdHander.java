package io.github.h800572003.scheduling.cmd;

import org.apache.commons.lang3.StringUtils;

import io.github.h800572003.scheduling.ISchedulingManager;

/**
 * 啟動命令Handler
 * 
 * @author 6407
 *
 */
public class InteruptCmdHander extends BaseCmdHander implements ICmdHander {

	private final ISchedulingManager schedulingManager;

	public InteruptCmdHander(ICmdService service, //
			ISchedulingManager schedulingManager) {
		super(CmdCodesCofing.Codes.INTERUPT.name(), service);
		this.schedulingManager = schedulingManager;
	}

	@Override
	public String cmd(String action) {
		if (StringUtils.isBlank(action)) {
			this.schedulingManager.cancelAll();
		} else {
			this.schedulingManager.cancel(action);
		}
		return CmdContract.OK;

	}

}
