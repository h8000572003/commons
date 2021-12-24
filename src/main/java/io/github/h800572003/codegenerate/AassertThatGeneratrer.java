package io.github.h800572003.codegenerate;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

public class AassertThatGeneratrer {
	public String generateAssertThat(Class<?> excep, Class<?> source) {
		List<String> line = Lists.newArrayList();
		Method[] methods = excep.getMethods();
		final String clsseNmae = excep.getSimpleName();
		final String objectName = StringUtils.uncapitalize(clsseNmae);
		for (Method mm : methods) {
			boolean startsWith = mm.getName().startsWith("get");
			if (startsWith) {
				String methodName = mm.getName();
				line.add(String.format("assertThat(%s.%s()).isEqualTo(%s.%s()); ", objectName, methodName,
						StringUtils.uncapitalize(source.getSimpleName()), methodName));
			}
		}
		String collect = line.stream().collect(Collectors.joining("\n"));
		System.out.print(collect);
		return collect;
	}

	public String generateAssertThat(Class<?> source) {
		List<String> line = Lists.newArrayList();
		Method[] methods = source.getMethods();
		final String clsseNmae = source.getSimpleName();
		final String objectName = StringUtils.uncapitalize(clsseNmae);
		for (Method mm : methods) {
			boolean startsWith = mm.getName().startsWith("get");
			if (startsWith) {
				String methodName = mm.getName();
				line.add(String.format("assertThat(%s.%s()).isEqualTo(-); ", objectName, methodName));
			}
		}
		String collect = line.stream().collect(Collectors.joining("\n"));
		System.out.print(collect);
		return collect;
	}

}
