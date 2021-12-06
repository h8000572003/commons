package io.github.h800572003.commons.properties;

import org.apache.commons.lang3.builder.EqualsExclude;

import lombok.Data;

@Data
public class Code {

	private String key;
	@EqualsExclude
	private String value;

}
