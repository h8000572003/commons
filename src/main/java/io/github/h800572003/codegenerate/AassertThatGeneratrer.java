package io.github.h800572003.codegenerate;

import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class AassertThatGeneratrer {
	private Set<String> excludes = Sets.newHashSet();

	public AassertThatGeneratrer() {
		super();
		this.excludes = Sets.newHashSet();
		this.add("getClass");
	}

	public void add(String excludeMethod) {
		this.excludes.add(excludeMethod);
	}

	public void generateAssertThat(Class<?> excep, Class<?>... source) {
		try (PrintStream out = System.out) {
			new CodeGenerater(new Holder(excep, source)::getLines).write(out);
		} catch (final IOException e) {
			System.out.print("產製錯誤" + e.getMessage());
		}
	}

	public void generateAssertThat(Class<?> excep) {
		try (PrintStream out = System.out) {
			new CodeGenerater(new Holder(excep)::getLines).write(out);
		} catch (final IOException e) {
			System.out.print("產製錯誤" + e.getMessage());
		}
	}

	class Holder {
		private static final String ASSERT_THAT_S_S_IS_EQUAL_TO_S_S = "assertThat(%s.%s()).isEqualTo(%s.%s()); ";
		final Class<?> excep;
		final Class<?>[] source;

		public Holder(Class<?> excep, Class<?>... source) {
			super();
			this.excep = excep;
			this.source = source;
		}

		public List<String> getLines() {
			final List<String> line = Lists.newArrayList();
			final Method[] methods = this.excep.getMethods();
			final String clsseNmae = this.excep.getSimpleName();
			final String objectName = StringUtils.uncapitalize(clsseNmae);
			for (final Method mm : methods) {
				final boolean startsWith = mm.getName().startsWith("get");
				if (startsWith) {
					final String methodName = mm.getName();
					if (AassertThatGeneratrer.this.excludes.contains(methodName)) {
						continue;
					}
					boolean isFind = false;
					for (final Class<?> pClass : this.source) {
						try {
							pClass.getMethod(methodName);
							line.add(String.format(ASSERT_THAT_S_S_IS_EQUAL_TO_S_S, objectName, methodName,
									StringUtils.uncapitalize(pClass.getSimpleName()), methodName));
							isFind = true;
							break;
						} catch (NoSuchMethodException | SecurityException e) {
							// 找不到該方法
						}
					}
					if (!isFind) {
						line.add(String.format(ASSERT_THAT_S_S_IS_EQUAL_TO_S_S, objectName, methodName, "-",
								methodName));
					}
				}
			}
			return line;
		}

	}

}
