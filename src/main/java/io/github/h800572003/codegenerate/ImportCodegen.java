package io.github.h800572003.codegenerate;

/**
 * import產生
 * 
 * @author 6407
 *
 */
public class ImportCodegen {

	private String name;

	public ImportCodegen(String name) {
		super();
		this.name = name;
	}

	@Override
	public String toString() {
		final StringBuffer buffer = new StringBuffer();
		buffer.append(String.format("import %s", this.name));
		buffer.append("    }\n");
		return buffer.toString();
	}
}
