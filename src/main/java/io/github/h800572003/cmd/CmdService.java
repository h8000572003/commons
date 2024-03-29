package io.github.h800572003.cmd;

import java.util.Map;

import io.github.h800572003.exception.ApBusinessException;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Maps;

/**
 * 命令實作
 * 
 * @author andy tsai
 *
 */
public class CmdService implements ICmdService {

	private Map<String, ICmdHander> map = Maps.newConcurrentMap();

	@Override
	public String execute(String cmd, String value) {
		if (StringUtils.isBlank(cmd)) {
			throw new ApBusinessException("請輸入命令");
		}
		String key = StringUtils.substringBefore(cmd, "_");
		if (!map.containsKey(key)) {
			throw new ApBusinessException("該{0}無定義", key);
		}
		return this.executeCmd(cmd, value);
	}

	private String executeCmd(String cmd, String value) {
		return this.map.get(cmd).cmd(value);
	}

	@Override
	public void register(String code, ICmdHander command) {
		this.map.put(code, command);
	}

}
