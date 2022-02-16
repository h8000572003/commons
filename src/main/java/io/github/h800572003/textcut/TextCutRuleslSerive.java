package io.github.h800572003.textcut;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import io.github.h800572003.exception.ApBusinessException;

public class TextCutRuleslSerive<T> implements ITextCutRuleslSerive<T> {

	private List<TextCutHolder<T>> holders = new ArrayList<>();

	@Override
	public void addRule(Predicate<String> line, TextCutRoleConfig<T> config) {
		holders.add(new TextCutHolder<T>(line, config));
	}

	private T to(TextCutRoleConfig<T> config, String text) {
		Objects.requireNonNull(config);
		Objects.requireNonNull(text);

		TextCutType textCutType = config.textCutType;
		if (textCutType.equals(TextCutType.BYTE)) {
			return new TextByteCutHolder<T>(config, text).get();
		} else if (textCutType.equals(TextCutType.CHAR)) {
			return new TextStringCutHolder<T>(config, text).get();
		}

		throw new ApBusinessException("無此定義規則" + textCutType);
	}

	@Override
	public List<T> to(List<String> lines) {
		List<T> result = new ArrayList<T>();
		for (String line : lines) {
			for (TextCutHolder<T> holder : holders) {
				if (holder.line.test(line)) {
					result.add(this.to(holder.config, line));
				}
			}
		}
		return result;

	}
}
