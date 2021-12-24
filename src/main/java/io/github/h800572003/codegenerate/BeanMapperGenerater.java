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
		String collect = line.stream().collect(Collectors.joining("\n"));
		System.out.print(collect);
		return collect;
	}


	
}
