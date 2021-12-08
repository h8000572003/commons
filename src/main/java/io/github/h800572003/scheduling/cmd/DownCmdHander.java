package io.github.h800572003.scheduling.cmd;

import org.apache.commons.lang3.StringUtils;

import io.github.h800572003.exception.ApBusinessExecpetion;
import io.github.h800572003.scheduling.ISchedulingManager;

/**
 * 服務停止命令
 * 
 * @author 6407
 *
 */
public class DownCmdHander extends BaseCmdHander implements ICmdHander {

	private final ISchedulingManager schedulingManager;

	public DownCmdHander(//
			ICmdService service, //
			ISchedulingManager schedulingManager) {//
		super(CmdCodesCofing.Codes.DOWN.name(), service);
		this.schedulingManager = schedulingManager;
	}

	@Override
	public String cmd(String action) {
		if (StringUtils.isNotBlank(action)) {
			throw new ApBusinessExecpetion("無提供此命命:{0}", action);

		}
		this.schedulingManager.down();
		return CmdContract.OK;

	}

}
