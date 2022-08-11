package io.github.h800572003.textcut;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import io.github.h800572003.objects.ApObjects;

public class TextCutRuleslSerive<T> implements ITextCutRulesSerive<T> {

	private List<TextCutHolder<T>> holders = new ArrayList<>();

	@Override
	public void addRule(Predicate<String> line, TextCutRoleConfig<T> config) {
		this.holders.add(new TextCutHolder<T>(line, config));
	}

	private T to(TextCutRoleConfig<T> config, String text) {
		ApObjects.requireNonNull(config);
		ApObjects.requireNonNull(text);
		return new TextCutBuilder<T>(config.textCutType, config, text).get();

	}

	@Override
	public List<T> to(List<String> lines) {
		List<T> result = new ArrayList<T>();
		for (String line : lines) {
			for (TextCutHolder<T> holder : this.holders) {
				if (holder.line.test(line)) {
					result.add(this.to(holder.config, line));
				}
			}
		}
		return result;

	}

	@Override
	public List<T> to(Path path, Charset charset) throws IOException {
		return to(Files.lines(path, charset).collect(Collectors.toList()));
	}
}
