package io.github.h800572003.textcut;

import org.apache.commons.lang3.StringUtils;

public class TextCutRole {
	int size;
	String defValue;

	public TextCutRole(int size, String defValue) {
		super();
		this.size = size;
		this.defValue = defValue;
	}

	public TextCutRole(int size) {
		this(size, StringUtils.EMPTY);
	}
}
