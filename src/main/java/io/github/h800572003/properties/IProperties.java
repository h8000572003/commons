package io.github.h800572003.properties;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @author andy tsai
 *
 */
public interface IProperties {
	String getKey();

	String getValue1();

	default String toNumberValue1(String def) {
		String value = getValue1();
		if (StringUtils.isNumeric(value)) {
			return value;
		} else {
			return def;
		}

	}

	default int toIntValue1(int def) {
		String value = getValue1();
		if (StringUtils.isNumeric(value)) {
			return Integer.parseInt(value);
		} else {
			return def;
		}
	}

	default String totDefValue1(String def) {
		String value = getValue1();
		if (StringUtils.isBlank(value)) {
			return def;
		}
		return value;
	}

	String getValue2();

	default String toDefValue2(String def) {
		String value = getValue2();
		if (StringUtils.isBlank(value)) {
			return def;
		}
		return value;
	}

	String getValue3();

	default String toDefValue3(String def) {
		String value = getValue3();
		if (StringUtils.isBlank(value)) {
			return def;
		}
		return value;
	}

	default boolean toOnOff() {
		String value = getValue3();
		if (StringUtils.equalsAnyIgnoreCase(value, "Y")) {
			return true;
		}
		return false;
	}
}
