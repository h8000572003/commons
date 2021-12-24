package io.github.h800572003.codegenerate;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import com.google.common.collect.Lists;

public class BeanMapperGenerater {

	public String generate(Class<?> source, Class<?> target) {

		List<String> line = Lists.newArrayList();
		Method[] methods = target.getMethods();
		final String clsseNmae = target.getSimpleName();
		final String objectName = StringUtils.uncapitalize(clsseNmae);
		line.add(String.format("%s %s = new %s();", clsseNmae, objectName, clsseNmae));
		for (Method mm : methods) {
			boolean startsWith = mm.getName().startsWith("set");

			if (startsWith) {
				String setName = mm.getName();
				line.add(String.format("%s.%s(%s.%s());", objectName, mm.getName(),StringUtils.uncapitalize(source.getSimpleName()), setName.replace("set", "get")));
			}
		}
		String collect = line.stream().collect(Collectors.joining("\n"));
		System.out.print(collect);
		return collect;
	}

}
