package io.github.h800572003.commons.cmd;

import org.apache.commons.lang3.StringUtils;

import io.github.h800572003.commons.scheduling.ISchedulingManager;

/**
 * 啟動命令Handler
 * 
 * @author 6407
 *
 */
public class StartCmdHander extends BaseCmdHander implements ICmdHander {

	private final ISchedulingManager schedulingManager;

	public StartCmdHander(ICmdService service, //
			ISchedulingManager schedulingManager) {
		super(CmdCodesCofing.Codes.START.name(), service);
		this.schedulingManager = schedulingManager;
	}

	@Override
	public String cmd(String action) {
		if (StringUtils.isBlank(action)) {
			this.schedulingManager.startAll();
		} else {
			this.schedulingManager.start(action);
		}
		return CmdContract.OK;

	}

}
