package io.github.h800572003.cmd;

import io.github.h800572003.exception.ApBusinessException;
import org.apache.commons.lang3.StringUtils;

import io.github.h800572003.scheduling.ISchedulingManager;

/**
 * 任務更新Handler
 * 
 * @author andy tsai
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
			throw new ApBusinessException("不提供全部更新{0}", action);
		} else {
			this.schedulingManager.refresh(action);
		}
		return CmdContract.OK;

	}

}
