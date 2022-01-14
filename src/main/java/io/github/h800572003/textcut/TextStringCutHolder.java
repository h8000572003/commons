package io.github.h800572003.textcut;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;

import io.github.h800572003.exception.ApBusinessException;

public class TextStringCutHolder<T> implements Supplier<T> {

	TextCutRoleConfig<T> config;
	String text;

	public TextStringCutHolder(TextCutRoleConfig<T> config, String text) {
		super();
		Objects.requireNonNull(config.mapper);
		Objects.requireNonNull(config.textCutType);
		Objects.requireNonNull(config.roles);
		if (config.roles.isEmpty()) {
			throw new ApBusinessException("規則至少一項");
		}
		this.config = config;
		this.text = text;
	}

	@Override
	public T get() {
		List<String> lines = new ArrayList<String>();
		List<TextCutRole> roles = config.roles;
		int local = 0;

		try {
			for (TextCutRole role : roles) {
				String value = StringUtils.substring(text, local, local + role.size);
				if (StringUtils.isBlank(value)) {
					value = role.defValue;
				}
				lines.add(value.trim());
				local += role.size;
			}
			return config.mapper.apply(new TextLine(lines));
		} catch (Exception e) {
			throw new ApBusinessException(config.charsets + "編碼不存在", e);
		}

	}
}
