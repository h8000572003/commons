package io.github.h800572003.textcut;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import io.github.h800572003.exception.ApBusinessException;

public class TextPatternCutHolder<T> implements Supplier<T> {

	String text;
	private TextCutRoleConfig<T> config;

	public TextPatternCutHolder(TextCutRoleConfig<T> config, String text) {
		super();
		this.text = text;
		this.config = config;
	}

	@Override
	public T get() {
		String pattern = config.pattern;
		try {
			List<String> split = this.split(text, config.pattern);
			return config.mapper.apply(new TextLine(split));
		} catch (Exception e) {
			throw new ApBusinessException("pattern{0} 異常", pattern, e);
		}
	}

	public List<String> split(String input, String regex) {
		List<String> str = new ArrayList<>();
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(input);
		int index = 0;
		while (matcher.find()) {
			int start = matcher.start();
			int end = matcher.end();
			String substring = StringUtils.substring(input, index, start);
			index = end;
			str.add(substring);
		}
		String substring = StringUtils.substring(input, index);
		str.add(substring);
		return str;
	}

}
