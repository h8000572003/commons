package io.github.h800572003.codegenerate;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(exclude = "name")
public class FieldCodegen {
	private final String name;
	private String protectedValue = "public";
	private String typeString = "String";
	private final String defalut = "null";

	public FieldCodegen(String name) {
		super();
		this.name = name;
	}

	public String getProtectedValue() {
		return this.protectedValue;
	}

	public void setProtectedValue(String protectedValue) {
		this.protectedValue = protectedValue;
	}

	public String getTypeString() {
		return this.typeString;
	}

	public void setTypeString(String typeString) {
		this.typeString = typeString;
	}

	@Override
	public String toString() {
		final StringBuffer buffer = new StringBuffer();
		buffer.append(String.format("    %s %s %s =%s;\n", this.protectedValue, this.typeString, this.name, this.defalut));
		return buffer.toString();
	}

}
