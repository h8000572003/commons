package io.github.h800572003.codegenerate;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.function.Supplier;

public class CodeGenerater {

	private Supplier<List<String>> supplier;

	public CodeGenerater(Supplier<List<String>> supplier) {
		super();
		this.supplier = supplier;
	}

	public void write(OutputStream outputStream) throws IOException {
		for (String line : this.supplier.get()) {
			outputStream.write((line + "\n").getBytes());
		}
	}

}
