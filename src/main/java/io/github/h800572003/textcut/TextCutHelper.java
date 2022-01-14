package io.github.h800572003.textcut;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

		TextCutType textCutType = config.textCutType;
		if (textCutType.equals(TextCutType.BYTE)) {
			return new TextByteCutHolder<T>(config, text).get();
		} else if (textCutType.equals(TextCutType.CHAR)) {
			return new TextStringCutHolder<T>(config, text).get();
		}

		throw new ApBusinessException("無此定義規則" + textCutType);

	}

	public static <T> List<T> to(TextCutRoleConfig<T> config, List<String> lines) {
		return lines.stream().map(i -> TextCutHelper.to(config, i)).collect(Collectors.toList());
	}

}
