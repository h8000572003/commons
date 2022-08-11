package io.github.h800572003.textcut;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import io.github.h800572003.exception.ApBusinessException;
import org.apache.commons.lang3.StringUtils;

public class TextByteCutHolder<T> implements Supplier<T> {

	TextCutRoleConfig<T> config;
	String text;

	public TextByteCutHolder(TextCutRoleConfig<T> config, String text) {
		super();
		this.config = config;
		this.text = text;
	}

	@Override
	public T get() {
		List<String> lines = new ArrayList<String>();
		List<TextCutRole> roles = config.roles;
		try {
			int local = 0;
			final byte[] tmpStrByte = text.getBytes(config.charsets);
			for (TextCutRole role : roles) {
				String value = new String(Arrays.copyOfRange(tmpStrByte, local, local + role.size), config.charsets);
				if (StringUtils.isBlank(value)) {
					value = role.defValue;
				}
				lines.add(value.trim());
				local += role.size;
			}
			return config.mapper.apply(new TextLine(lines));
		} catch (UnsupportedEncodingException e) {
			throw new ApBusinessException(config.charsets + "編碼不存在", e);
		}
	}
}
