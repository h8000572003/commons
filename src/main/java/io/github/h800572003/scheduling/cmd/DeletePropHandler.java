package io.github.h800572003.scheduling.cmd;

import org.apache.commons.lang3.StringUtils;

import io.github.h800572003.exception.ApBusinessExecpetion;
import io.github.h800572003.properties.IPropertiesService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeletePropHandler extends BaseCmdHander implements ICmdHander {

	private IPropertiesService propertiesService;

	public DeletePropHandler(ICmdService service, IPropertiesService propertiesService) {
		super(CmdCodesCofing.Codes.DEPRO.name(), service);
		this.propertiesService = propertiesService;
	}

	@Override
	public String cmd(String action) {

		if (StringUtils.isBlank(action)) {
			throw new ApBusinessExecpetion("不提供作業");
		}

		log.info("category:{}", action);
		String[] split = StringUtils.split(action, ",");
		if (split.length != 2) {
			throw new ApBusinessExecpetion("參數數量{0}錯誤", split.length);
		}
		final String category = split[0];
		final String key = split[1];
		log.info("category:{},key:{} ", category, key);

		this.propertiesService.delete(category, key);
		return CmdContract.OK;
	}

	public static void main(String[] args) {

		String[] split = StringUtils.split("Catey,1,1,1", ",");

		System.out.println(split);
	}

}
