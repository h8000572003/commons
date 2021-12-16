package io.github.h800572003.type;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(of = { "toTypeId" })
public class TypeKey implements ITypeContext {

	private String toTypeId;

	private TypeKey(String toTypeId) {
		this.toTypeId = toTypeId;
	}

	public static TypeKey create(ITypeContext object) {
		return new TypeKey(object);
	}

	public static TypeKey create(String toTypeId) {
		return new TypeKey(toTypeId);
	}

	private TypeKey(ITypeContext object) {
		super();
		this.toTypeId = object.toTypeId();
	}

	@Override
	public String toTypeId() {
		return toTypeId;
	}

}
