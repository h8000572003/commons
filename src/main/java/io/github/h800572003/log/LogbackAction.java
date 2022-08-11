package io.github.h800572003.log;

import java.util.List;

import io.github.h800572003.exception.ApBusinessException;
import org.slf4j.Logger;

import com.google.common.collect.Lists;

import ch.qos.logback.classic.Level;
import io.github.h800572003.log.Slf4jUpdateLogLeavelService.UpdateLogAction;

public class LogbackAction implements UpdateLogAction {

	private List<String> logbackLeavels = Lists.newArrayList(///
			Level.TRACE.levelStr, //
			Level.DEBUG.levelStr, //
			Level.ERROR.levelStr, //
			Level.WARN.levelStr, //
			Level.INFO.levelStr, //
			Level.OFF.levelStr//
	);

	@Override
	public void update(Logger logger, String leavel) {
		if (!logbackLeavels.contains(leavel)) {
			throw new ApBusinessException("LEAVEL:{0}限制不在範圍內，僅提供:{1}", leavel, logbackLeavels);
		}
		if (logger instanceof ch.qos.logback.classic.Logger) {
			((ch.qos.logback.classic.Logger) logger).setLevel(Level.toLevel(leavel));
		}

	}

}
