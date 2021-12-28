package io.github.h800572003.codegenerate;

import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

public class BeanMapperGenerater {

	public void generate(Class<?> source, Class<?> target) {
		try (PrintStream out = System.out) {
			new CodeGenerater(new Holder(source, target)::getLines).write(out);
		} catch (IOException e) {
			System.out.print("產製錯誤" + e.getMessage());
		}
	}

	class Holder {
		Class<?> source;
		Class<?> target;

		public Holder(Class<?> source, Class<?> target) {
			super();
			this.source = source;
			this.target = target;
		}

		public List<String> getLines() {
			List<String> line = Lists.newArrayList();
			Method[] methods = target.getMethods();
			final String clsseNmae = target.getSimpleName();
			final String objectName = StringUtils.uncapitalize(clsseNmae);

			line.add(String.format("public %s to(%s %s) {", clsseNmae, source.getSimpleName(),
					StringUtils.uncapitalize(source.getSimpleName())));
			line.add(String.format("    if ( %s == null ) {", StringUtils.uncapitalize(source.getSimpleName())));
			line.add(String.format("        return null;"));
			line.add(String.format("    }"));
			line.add(String.format(""));
			line.add(String.format("    final %s %s = new %s();", clsseNmae, objectName, clsseNmae));
			line.add(String.format(""));
			for (Method mm : methods) {
				boolean startsWith = mm.getName().startsWith("set");
				if (startsWith) {
					String setName = mm.getName();
					line.add(String.format("    %s.%s(%s.%s());", objectName, mm.getName(),
							StringUtils.uncapitalize(source.getSimpleName()), setName.replace("set", "get")));
				}
			}
			line.add(String.format("    return %s;", objectName));
			line.add(String.format("}"));
			return line;
		}

	}

}
