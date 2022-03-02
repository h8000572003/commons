package io.github.h800572003.mybatis;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.internal.DefaultCommentGenerator;
import org.mybatis.generator.internal.util.StringUtility;

public class CommentGenerator extends DefaultCommentGenerator {

	@Override
	public void addFieldAnnotation(Field field, IntrospectedTable introspectedTable,
			IntrospectedColumn introspectedColumn, Set<FullyQualifiedJavaType> imports) {
		String remarks = introspectedColumn.getRemarks();
		// 根據引數和備註資訊判斷是否新增備註資訊
		if (StringUtility.stringHasValue(remarks)) {
			// 資料庫中特殊字元需要轉義
			if (remarks.contains("\"")) {
				remarks = remarks.replace("\"", "'");
			}
			field.addJavaDocLine("//" + Stream.of(remarks.split("\n")).collect(Collectors.joining("\n//")));
		}

	}

}
