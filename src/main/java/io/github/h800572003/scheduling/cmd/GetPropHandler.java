package io.github.h800572003.scheduling.cmd;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.h800572003.properties.Code;
import io.github.h800572003.properties.IProperties;
import io.github.h800572003.properties.IPropertiesService;
import lombok.extern.slf4j.Slf4j;

/**
 * 取得代碼檔案
 * 
 * @author 6407
 *
 */
@Slf4j
public class GetPropHandler extends BaseCmdHander implements ICmdHander {

	private IPropertiesService propertiesService;
	private ObjectMapper objectMapper;

	public GetPropHandler( ICmdService service, IPropertiesService propertiesService,
			ObjectMapper objectMapper) {
		super(CmdCodesCofing.Codes.GETPRO.name(), service);
		this.propertiesService = propertiesService;
		this.objectMapper = objectMapper;
	}

	@Override
	public String cmd(String action) {

		if (StringUtils.isBlank(action)) {
			List<Code> category = this.propertiesService.getCategory();
			try {
				return objectMapper.writeValueAsString(category);
			} catch (JsonProcessingException e) {
				log.error("轉換失敗", e);
				return "轉換失敗";
			}
		} else {
			List<IProperties> propertie = this.propertiesService.getPropertie(action);
			try {
				return objectMapper.writeValueAsString(propertie);
			} catch (JsonProcessingException e) {
				log.error("轉換失敗", e);
				return "轉換失敗";
			}
		}

	}

}
