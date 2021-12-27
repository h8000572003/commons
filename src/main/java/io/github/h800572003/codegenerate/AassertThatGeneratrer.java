package io.github.h800572003.codegenerate;

import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

public class AassertThatGeneratrer {
	public void generateAssertThat(Class<?> excep, Class<?> source) {
		try (PrintStream out = System.out) {
			new CodeGenerater(new Holder(excep, source)::getLines).write(out);
		} catch (IOException e) {
			System.out.print("產製錯誤" + e.getMessage());
		}
	}

	
	public void generateAssertThat(Class<?> source) {
		try (PrintStream out = System.out) {
			new CodeGenerater(new Holder(source, null)::getLines).write(out);
		} catch (IOException e) {
			System.out.print("產製錯誤" + e.getMessage());
		}
	}
	class Holder {
		Class<?> excep;
		Class<?> source;

		public Holder(Class<?> excep, Class<?> source) {
			super();
			this.excep = excep;
			this.source = source;
		}

		public List<String> getLines() {
			List<String> line = Lists.newArrayList();
			Method[] methods = excep.getMethods();
			final String clsseNmae = excep.getSimpleName();
			final String objectName = StringUtils.uncapitalize(clsseNmae);
			for (Method mm : methods) {
				boolean startsWith = mm.getName().startsWith("get");
				if (startsWith) {
					String methodName = mm.getName();
					if (this.source == null) {
						line.add(String.format("assertThat(%s.%s()).isEqualTo(-); ", objectName, methodName));
					} else {
						line.add(String.format("assertThat(%s.%s()).isEqualTo(%s.%s()); ", objectName, methodName,
								StringUtils.uncapitalize(source.getSimpleName()), methodName));
					}
				}
			}
			return line;
		}

	}

}
