package io.github.h800572003.mybatis;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

public class MybatisGeneratorHelper {

	private static final String GENERATOR_CONFIG_XML = "generatorConfig.xml";

	public void generator(String generatorConfigXml) {
		try {
			final List<String> warnings = new ArrayList<>(2);
			final ConfigurationParser cp = new ConfigurationParser(warnings);
			final URL url = MybatisGeneratorHelper.class.getResource(GENERATOR_CONFIG_XML);
			final File configFile = new File(url.getFile());
			final Configuration config = cp.parseConfiguration(configFile);

			final DefaultShellCallback callback = new DefaultShellCallback(true);

			final MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
			myBatisGenerator.generate(null);
		} catch (Exception e) {
			throw new MybatisException("轉換失敗", e);
		}
	}

	public void generator() {
		generator(GENERATOR_CONFIG_XML);
	}

}
