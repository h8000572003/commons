package io.github.h800572003.commons.cmd;

import org.apache.commons.lang3.StringUtils;

import io.github.h800572003.commons.ApBusinessExecpetion;
import io.github.h800572003.commons.scheduling.ISchedulingManager;

/**
 * 任務更新Handler
 * 
 * @author 6407
 *
 */
public class RreshCmdHander extends BaseCmdHander implements ICmdHander {

	private final ISchedulingManager schedulingManager;

	public RreshCmdHander(ICmdService service, //
			ISchedulingManager schedulingManager) {
		super(CmdCodesCofing.Codes.REFRESH.name(), service);
		this.schedulingManager = schedulingManager;
	}

	@Override
	public String cmd(String action) {
		if (StringUtils.isBlank(action)) {
			throw new ApBusinessExecpetion("不提供全部更新{0}", action);
		} else {
			this.schedulingManager.refresh(action);
		}
		return CmdContract.OK;

	}

}
