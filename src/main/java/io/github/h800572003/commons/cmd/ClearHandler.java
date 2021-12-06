package io.github.h800572003.commons.cmd;

import org.apache.commons.lang3.StringUtils;

import io.github.h800572003.commons.ApBusinessExecpetion;
import io.github.h800572003.commons.properties.IPropertiesService;
	
public class ClearHandler extends BaseCmdHander implements ICmdHander {

	private IPropertiesService propertiesService;

	public ClearHandler(ICmdService service, IPropertiesService propertiesService) {
		super(CmdCodesCofing.Codes.CLEAR.name(), service);
		this.propertiesService = propertiesService;
	}

	@Override
	public String cmd(String action) {
		if (StringUtils.isBlank(action)) {
			throw new ApBusinessExecpetion("不提供全部清除:{0}", action);
		}
		this.propertiesService.clear(action);
		return CmdContract.OK;
	}

}
