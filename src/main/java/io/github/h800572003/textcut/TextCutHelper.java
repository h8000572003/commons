package io.github.h800572003.textcut;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import io.github.h800572003.exception.ApBusinessException;

/**
 * 文字切割Helper
 * 
 * @author 6407
 *
 */
public class TextCutHelper {

	public static <T> T to(TextCutRoleConfig<T> config, String text) {
		Objects.requireNonNull(config);
		Objects.requireNonNull(text);
		return new TextCutBuilder<T>(config.textCutType, config, text).get();

	}

	public static <T> List<T> to(TextCutRoleConfig<T> config, List<String> lines) {
		return lines.stream().map(i -> TextCutHelper.to(config, i)).collect(Collectors.toList());
	}

}
