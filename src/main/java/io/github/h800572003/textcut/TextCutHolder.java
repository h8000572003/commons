package io.github.h800572003.textcut;

import java.util.function.Predicate;

public class TextCutHolder<T> {

	public Predicate<String> line;
	public TextCutRoleConfig<T> config;

	public TextCutHolder(Predicate<String> line, TextCutRoleConfig<T> config) {
		super();
		this.line = line;
		this.config = config;
	}

}
