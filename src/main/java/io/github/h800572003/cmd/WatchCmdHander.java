package io.github.h800572003.cmd;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.h800572003.exception.ApBusinessException;
import io.github.h800572003.scheduling.ISchedulingContext;
import io.github.h800572003.scheduling.ISchedulingManager;
import lombok.extern.slf4j.Slf4j;

/**
 * 服務停止命令
 * 
 * @author 6407
 *
 */
@Slf4j
public class WatchCmdHander extends BaseCmdHander implements ICmdHander {

	private final ISchedulingManager schedulingManager;
	private final ObjectMapper objectMapper;

	public WatchCmdHander(//
			ICmdService service, //
			ISchedulingManager schedulingManager, ObjectMapper objectMapper) {//
		super(CmdCodesCofing.Codes.WATCH.name(), service);
		this.schedulingManager = schedulingManager;
		this.objectMapper = objectMapper;
	}

	@Override
	public String cmd(String action) {
		if (StringUtils.isNotBlank(action)) {
			throw new ApBusinessException("無提供此命命:{0}", action);
		}
		ISchedulingContext context = this.schedulingManager.getContext();
		try {
			return objectMapper.writeValueAsString(context);
		} catch (JsonProcessingException e) {
			log.error("轉換失敗", e);
			return "轉換失敗";
		}

	}

}
