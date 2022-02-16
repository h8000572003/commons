package io.github.h800572003.textcut;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

class TextCutRuleslSeriveTest {
	private final String UTF8 = "utf-8";
	ITextCutRuleslSerive<List<String>> textCutRuleslSerive = new TextCutRuleslSerive<>();

	public TextCutRuleslSeriveTest() {
		textCutRuleslSerive.addRule(i -> i.startsWith("ST01"), getST01());
		textCutRuleslSerive.addRule(i -> i.startsWith("ST02"), getST02());
		textCutRuleslSerive.addRule(i -> i.startsWith("ST03"), getST03());

	}

	@Test
	public void test_give_4Line_reutrn_4Line() {

		// GIVE
		// ST011234567890,ST0212345,ST0312
		List<String> line = new ArrayList<String>();
		line.add("ST011234567890");
		line.add("ST0212345");
		line.add("ST0312");
		line.add("ST010987654321");

		// WHEN 轉換
		List<List<String>> list = textCutRuleslSerive.to(line);

		// THEN
		System.out.println(list);

		assertThat(list.get(0).get(0).endsWith("ST01"));
		assertThat(list.get(0).get(1).endsWith("1234567890"));

		assertThat(list.get(1).get(0).endsWith("ST02"));
		assertThat(list.get(1).get(1).endsWith("12345"));

		assertThat(list.get(2).get(0).endsWith("ST03"));
		assertThat(list.get(2).get(1).endsWith("12"));

		assertThat(list.get(3).get(0).endsWith("ST01"));
		assertThat(list.get(3).get(1).endsWith("0987654321"));

	}

	TextCutRoleConfig<List<String>> getST01() {
		TextCutRoleConfig<List<String>> textCutRoleConfig = new TextCutRoleConfig<>(TextCutType.BYTE, UTF8,
				line -> line.getLines());
		textCutRoleConfig.addRole(4);
		textCutRoleConfig.addRole(10);
		return textCutRoleConfig;

	}

	TextCutRoleConfig<List<String>> getST02() {
		TextCutRoleConfig<List<String>> textCutRoleConfig = new TextCutRoleConfig<>(TextCutType.BYTE, UTF8,
				line -> line.getLines());
		textCutRoleConfig.addRole(4);
		textCutRoleConfig.addRole(5);
		return textCutRoleConfig;

	}

	TextCutRoleConfig<List<String>> getST03() {
		TextCutRoleConfig<List<String>> textCutRoleConfig = new TextCutRoleConfig<>(TextCutType.BYTE, UTF8,
				line -> line.getLines());
		textCutRoleConfig.addRole(4);
		textCutRoleConfig.addRole(2);
		return textCutRoleConfig;

	}

}
