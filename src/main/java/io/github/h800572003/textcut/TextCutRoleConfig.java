package io.github.h800572003.textcut;

import java.util.List;
import java.util.function.Function;

import com.google.common.collect.Lists;

public class TextCutRoleConfig<T> {
	protected List<TextCutRole> roles = Lists.newArrayList();
	protected TextCutType textCutType;
	protected Function<TextLine, T> mapper;
	protected String charsets;

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

	TextLine toTextLine(TextLine textLine) {
		return textLine;
	}
}
