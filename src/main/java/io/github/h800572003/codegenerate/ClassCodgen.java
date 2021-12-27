package io.github.h800572003.codegenerate;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Sets;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(exclude = { "name" })
public class ClassCodgen {

	private final String name;
	private String packageValue;
	private final String protectedValue = "public";
	private final Set<MethodCodegen> methodCodegens = Sets.newLinkedHashSet();
	private final Set<ImportCodegen> imports = Sets.newLinkedHashSet();
	private final Set<FieldCodegen> fileds = Sets.newLinkedHashSet();

	public ClassCodgen(String name) {
		super();
		this.name = name;
	}

	public MethodCodegen createMethod(String name) {
		final MethodCodegen methodCodegen = new MethodCodegen(name);
		this.methodCodegens.add(methodCodegen);
		return methodCodegen;
	}

	public ImportCodegen addImport(String name) {
		final ImportCodegen importCodegen = new ImportCodegen(name);
		this.imports.add(importCodegen);
		return importCodegen;
	}

	public FieldCodegen addFiel(String name) {
		final FieldCodegen fieldCodegen = new FieldCodegen(name);
		this.fileds.add(fieldCodegen);
		return fieldCodegen;
	}

	@Override
	public String toString() {
		final StringBuffer buffer = new StringBuffer();
		if (StringUtils.isNotBlank(this.packageValue)) {
			buffer.append(String.format("    package %s;\n", this.packageValue));
		}
		this.imports.forEach(i -> buffer.append(i));
		buffer.append(String.format("    %s class %s{\n", this.protectedValue, this.name));
		this.fileds.forEach(i -> buffer.append(i));
		this.methodCodegens.forEach(i -> buffer.append(i));
		buffer.append("    }\n");
		return buffer.toString();
	}
}
