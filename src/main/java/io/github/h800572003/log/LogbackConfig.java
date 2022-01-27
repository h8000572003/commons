package io.github.h800572003.log;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;
import ch.qos.logback.core.util.OptionHelper;
import io.github.h800572003.properties.Code;

public class LogbackConfig {
	private final Map<String, String> packageNameMap = new HashMap<>();
	private final Set<String> set = new HashSet<>();
	private final List<Code> codes = new ArrayList<>();

	public void registerPackage(String code, String name, Class<?> pClass) {
		this.register(code, name, true, pClass);
	}

	public void registerSlef(String code, String name, Class<?> pClass) {
		this.register(code, name, false, pClass);
	}

	public void register(String code, String name, boolean isPackage, Class<?> pClass) {
		final String packageName = isPackage ? pClass.getPackage().getName() : pClass.getName();
		this.packageNameMap.put(code, packageName);
		final Code myCode = new Code();
		myCode.setKey(code);
		myCode.setValue(name);
		this.codes.add(myCode);
		this.set.add(code);
	}

	public void setup() {
		for (final Map.Entry<String, String> entry : this.packageNameMap.entrySet()) {
			this.appender(entry.getKey(), entry.getValue());
		}
	}

	private void appender(String name, String pClass) {
		final Logger log = (Logger) LoggerFactory.getLogger(pClass);
		final LoggerContext loggerContext = log.getLoggerContext();
		log.addAppender(createAppender(name, loggerContext));
	}

	private RollingFileAppender<ILoggingEvent> createAppender(String name, LoggerContext loggerContext) {
		final RollingFileAppender<ILoggingEvent> appender = new RollingFileAppender<>();
		// 這裡設置級別過濾器
		// appender.addFilter(createLevelFilter(level));
		// 設置上下文，每個logger都關聯到logger上下文，默認上下文名稱為default。
		// 但可以使用<scope="context">設置成其他名字，用於區分不同應用程序的記錄。一旦設置，不能修改。
		appender.setContext(loggerContext);
		// appender的name屬性
		appender.setName(name);
		// 讀取logback配置文件中的屬性值，設置文件名
		final String logPath = OptionHelper.substVars("${logPath}" + name + "/" + name + ".log", loggerContext);
		appender.setFile(logPath);
		appender.setAppend(true);
		appender.setPrudent(false);
		// 加入下面兩個節點
		appender.setRollingPolicy(createRollingPolicy(name, loggerContext, appender));
		appender.setEncoder(createEncoder(loggerContext));
		appender.start();
		return appender;
	}

	private PatternLayoutEncoder createEncoder(LoggerContext context) {
		final PatternLayoutEncoder encoder = new PatternLayoutEncoder();
		encoder.setContext(context);
		// 設置格式
		final String pattern = OptionHelper.substVars("${pattern}", context);
		encoder.setPattern(pattern);
		encoder.setCharset(Charset.forName("utf-8"));
		encoder.start();
		return encoder;
	}

	private TimeBasedRollingPolicy<Object> createRollingPolicy(String name, LoggerContext context,
			FileAppender<ILoggingEvent> appender) {
		// 讀取logback配置文件中的屬性值，設置文件名

		final StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("${logPath}");
		stringBuilder.append(name);
		stringBuilder.append("/");
		stringBuilder.append(name);
		stringBuilder.append(".%d{yyyy-MM-dd}.%i.log");
		final String fp = OptionHelper.substVars(stringBuilder.toString(), context);

		final TimeBasedRollingPolicy<Object> rollingPolicyBase = new TimeBasedRollingPolicy<>();
		// 設置上下文，每個logger都關聯到logger上下文，默認上下文名稱為default。
		// 但可以使用<scope="context">設置成其他名字，用於區分不同應用程序的記錄。一旦設置，不能修改。
		rollingPolicyBase.setContext(context);
		// 設置父節點是appender
		rollingPolicyBase.setParent(appender);
		// 設置文件名模式
		rollingPolicyBase.setFileNamePattern(fp);
		final SizeAndTimeBasedFNATP<Object> sizeAndTimeBasedFNATP = new SizeAndTimeBasedFNATP<>();
		// 最大日志文件大小
		sizeAndTimeBasedFNATP.setMaxFileSize(FileSize.valueOf(OptionHelper.substVars("${max.file.size}", context)));
		rollingPolicyBase.setTimeBasedFileNamingAndTriggeringPolicy(sizeAndTimeBasedFNATP);

		rollingPolicyBase.setMaxHistory(Integer.parseInt(OptionHelper.substVars("${max.history}", context)));
		// 總大小限制
		rollingPolicyBase.setTotalSizeCap(FileSize.valueOf(OptionHelper.substVars("${total.size.cap}", context)));
		rollingPolicyBase.start();

		return rollingPolicyBase;
	}

	public File getByLogType(String category) {

		final Logger log = (Logger) LoggerFactory.getLogger(LogbackConfig.class);
		if (this.set.contains(category)) {
			final String substVars = OptionHelper.substVars("${logPath}" + FilenameUtils.getName(category),
					log.getLoggerContext());
			final File file = new File(substVars);
			if (file.exists()) {
				return file;
			}
		}
		return null;
	}

	public File getLog(String category, String name) {
		final Logger log = (Logger) LoggerFactory.getLogger(LogbackConfig.class);
		if (this.set.contains(category)) {
			final String substVars = OptionHelper.substVars("${logPath}" + category + "/" + FilenameUtils.getName(name),
					log.getLoggerContext());
			final File file = new File(substVars);
			if (file.exists()) {
				return file;
			}
		}
		return null;
	}

	public List<Code> getAllCategory() {
		return this.codes;
	}

}
