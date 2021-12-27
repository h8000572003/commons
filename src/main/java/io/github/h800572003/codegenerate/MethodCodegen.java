package io.github.h800572003.codegenerate;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import lombok.EqualsAndHashCode;

/**
 * 方法產生
 * 
 * @author 6407
 *
 */
@EqualsAndHashCode(exclude = { "name", "argValues" })
public class MethodCodegen {
	private final String name;
	private List<String> body = Lists.newArrayList();
	private String protectedValue = "public";
	private String returnValue = "void";
	private Set<ArgValue> argValues = Sets.newLinkedHashSet();

	@EqualsAndHashCode
	class ArgValue {
		String declarType;// 型態
		String declarName;// 類別名稱

		@Override
		public String toString() {
			return declarType + " " + declarName;
		}
	}

	public MethodCodegen(String name) {
		this.name = name;
	}

	public MethodCodegen addBody(String Body) {
		this.body.add(Body);
		return this;
	}

	public String getProtectedValue() {
		return this.protectedValue;
	}

	public MethodCodegen setProtectedValue(String protectedValue) {
		this.protectedValue = protectedValue;
		return this;
	}

	public String getReturnValue() {
		return this.returnValue;
	}

	public MethodCodegen setReturnValue(String returnValue) {
		this.returnValue = returnValue;
		return this;
	}

	public MethodCodegen add(String declarType, String declarName) {
		final ArgValue argValue = new ArgValue();
		argValue.declarName = declarName;
		argValue.declarType = declarType;
		this.argValues.add(argValue);
		return this;
	}

	@Override
	public String toString() {
		final StringBuffer buffer = new StringBuffer();
		final String args = argValues.stream().map(Object::toString).collect(Collectors.joining(","));
		buffer.append(String.format("    %s %s %s(%s) {\n", this.protectedValue, this.returnValue, this.name, args));
		this.body.forEach(i -> buffer.append(i + "\n"));
		buffer.append("    }\n");
		return buffer.toString();
	}

}
