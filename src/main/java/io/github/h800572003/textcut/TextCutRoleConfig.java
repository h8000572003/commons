package io.github.h800572003.textcut;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

public class TextCutRoleConfig<T> {
	protected List<TextCutRole> roles = Lists.newArrayList();
	protected TextCutType textCutType;
	protected Function<TextLine, T> mapper;
	protected String charsets;
	protected String pattern;

	public TextCutRoleConfig(TextCutType textCutType, String charsets, Function<TextLine, T> mapper) {
		super();
		this.textCutType = textCutType;
		this.mapper = mapper;
		this.charsets = charsets;
	}

	public void addRole(int size, String defValue) {
		this.roles.add(new TextCutRole(size, defValue));
	}

	public void addRole(int size) {
		this.roles.add(new TextCutRole(size));
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	TextLine toTextLine(TextLine textLine) {
		return textLine;
	}

	
}
