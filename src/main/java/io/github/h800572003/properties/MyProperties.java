package io.github.h800572003.properties;

import org.apache.commons.lang3.StringUtils;

import lombok.EqualsAndHashCode;
import lombok.Setter;

@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MyProperties implements IProperties {
	@EqualsAndHashCode.Include
	protected String key;
	protected String value1;
	protected String value2;
	protected String value3;
	protected String meno = "";

	

	

	@Override
	public String getKey() {
		return this.key;
	}

	@Override
	public String getValue1() {
		return StringUtils.defaultString(this.value1);
	}

	@Override
	public String getValue2() {
		return StringUtils.defaultString(this.value2);
	}

	@Override
	public String getValue3() {
		return StringUtils.defaultString(this.value3);
	}

	public String getMemo() {
		return StringUtils.defaultString(this.meno);
	}
}
