package io.github.h800572003.textcut;

import java.util.Map;
import java.util.function.Supplier;

import com.google.common.collect.Maps;
import io.github.h800572003.exception.ApBusinessException;

public class TextCutBuilder<T> implements Supplier<T> {

	private TextCutType textCutType;
	private TextCutRoleConfig<T> config;
	private String text;
	private Map<TextCutType, Supplier<T>> map = Maps.newConcurrentMap();

	public TextCutBuilder(TextCutType textCutType, TextCutRoleConfig<T> config, String text) {
		super();
		initType(config, text);
		this.textCutType = textCutType;
		this.config = config;
		this.text = text;

	}

	private void initType(TextCutRoleConfig<T> config, String text) {
		this.map.put(TextCutType.BYTE, () -> new TextByteCutHolder<T>(config, text).get());
		this.map.put(TextCutType.CHAR, () -> new TextStringCutHolder<T>(config, text).get());
		this.map.put(TextCutType.PATTERN, () -> new TextPatternCutHolder<T>(config, text).get());
	}

	@Override
	public T get() {
		if (this.map.containsKey(textCutType)) {
			return this.map.get(textCutType).get();
		} else {
			throw new ApBusinessException("無此定義規則{0}", textCutType);
		}

	}

}
