package io.github.h800572003.log;

import java.util.Map;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import io.github.h800572003.exception.ApBusinessException;

/**
 * SLFJ4 更新log leavel
 * 
 * @author Andy
 *
 */
public class Slf4jUpdateLogLeavelService {

	private Map<Class<?>, UpdateLogAction> map = Maps.newConcurrentMap();

	public void register(Class<?> logger, UpdateLogAction action) {
		map.put(logger, action);
	}

	public void registerLogback() {
		this.register(ch.qos.logback.classic.Logger.class, new LogbackAction());
	}

	public interface UpdateLogAction {
		void update(Logger logger, String leavel);
	}

	public void change(String logName, String leavel) {
		ILoggerFactory iLoggerFactory = LoggerFactory.getILoggerFactory();
		Logger logger = iLoggerFactory.getLogger(logName);
		Class<?> type = logger.getClass();
		if (!map.containsKey(type)) {
			throw new ApBusinessException("無註冊該服務{0}", type);
		}
		this.getAction(type).update(logger, leavel);

	}

	private UpdateLogAction getAction(Class<?> type) {
		return map.get(type);
	}
}
