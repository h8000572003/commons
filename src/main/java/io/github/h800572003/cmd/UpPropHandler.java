package io.github.h800572003.cmd;

import org.apache.commons.lang3.StringUtils;

import io.github.h800572003.exception.ApBusinessException;
import io.github.h800572003.properties.IPropertiesService;
import io.github.h800572003.properties.MyProperties;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UpPropHandler extends BaseCmdHander implements ICmdHander {

	private IPropertiesService propertiesService;

	public UpPropHandler(ICmdService service, IPropertiesService propertiesService) {
		super(CmdCodesCofing.Codes.UPPRO.name(), service);
		this.propertiesService = propertiesService;
	}

	@Override
	public String cmd(String action) {

		if (StringUtils.isBlank(action)) {
			throw new ApBusinessException("參數錯誤:{0}", action);
		}

		String[] split = StringUtils.split(action, ",");
		if (split.length != 5) {
			throw new ApBusinessException("參數數量{0}錯誤", split.length);
		}

		final String category = split[0];
		final String key = split[1];
		final String value1 = split[2];
		final String value2 = split[3];
		final String value3 = split[4];

		final MyProperties myProperties = new MyProperties();
		myProperties.setKey(key);
		myProperties.setValue1(value1);
		myProperties.setValue2(value2);
		myProperties.setValue3(value3);

		log.info("category:{},key:{} value1:{},value2:{},value3:{}", category, key, value1, value2, value3);

		this.propertiesService.saveOrUpdate(category, myProperties);
		return CmdContract.OK;
	}


}
