package io.github.h800572003.codegenerate;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Set;

import com.google.common.collect.Sets;

import io.github.h800572003.exception.ApBusinessException;

public class CodeCodegen {

	private final Set<MethodCodegen> methodCodegens = Sets.newLinkedHashSet(); // option
	private final Set<ClassCodgen> classCodgens = Sets.newLinkedHashSet();// option

	public ClassCodgen createClass(String name) {
		if (this.methodCodegens.size() > 0) {
			throw new ApBusinessException("請於class內實作method");
		}
		final ClassCodgen classCodgen = new ClassCodgen(name);
		this.classCodgens.add(classCodgen);
		return classCodgen;
	}

	public MethodCodegen createMethod(String name) {
		if (this.classCodgens.size() > 0) {
			throw new ApBusinessException("請於class內實作method");
		}
		final MethodCodegen methodCodegen = new MethodCodegen(name);
		this.methodCodegens.add(methodCodegen);
		return methodCodegen;
	}

	public String toString() {
		final StringBuffer buffer = new StringBuffer();
		this.methodCodegens.forEach(i -> buffer.append(i));
		this.classCodgens.forEach(i -> buffer.append(i));
		return buffer.toString();
	}

	public final void write(OutputStream outputStream) throws IOException {
		outputStream.write(this.toString().getBytes());
	}

	public final void print() {
		try (PrintStream out = System.out) {
			new CodeCodegen().write(out);
		} catch (final IOException e) {
			System.out.print("產製錯誤" + e.getMessage());
		}
	}

}
