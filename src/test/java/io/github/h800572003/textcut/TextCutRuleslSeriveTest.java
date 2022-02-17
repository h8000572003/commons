package io.github.h800572003.textcut;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class TextCutRuleslSeriveTest {
	private final String UTF8 = "utf-8";
	ITextCutRulesSerive<TextLine> textCutRuleslSerive = new TextCutRuleslSerive<>();

	public TextCutRuleslSeriveTest() {
		textCutRuleslSerive.addRule(i -> i.startsWith("ST01"), getST01());
		textCutRuleslSerive.addRule(i -> i.startsWith("ST02"), getST02());
		textCutRuleslSerive.addRule(i -> i.startsWith("ST03"), getST03());

	}

	/**
	 * 使用長度分割
	 */
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
		List<TextLine> list = textCutRuleslSerive.to(line);

		// THEN
		log.info("list:{}", list);

		assertThat(list.get(0).next().endsWith("ST01"));
		assertThat(list.get(0).next().endsWith("1234567890"));

		assertThat(list.get(1).next().endsWith("ST02"));
		assertThat(list.get(1).next().endsWith("12345"));

		assertThat(list.get(2).next().endsWith("ST03"));
		assertThat(list.get(2).next().endsWith("12"));

		assertThat(list.get(3).next().endsWith("ST01"));
		assertThat(list.get(3).next().endsWith("0987654321"));

	}
	
	TextCutRoleConfig<TextLine> getST01() {
		TextCutRoleConfig<TextLine> textCutRoleConfig = new TextCutRoleConfig<>(TextCutType.BYTE, UTF8, line -> line);
		textCutRoleConfig.addRole(4);
		textCutRoleConfig.addRole(10);
		return textCutRoleConfig;

	}

	TextCutRoleConfig<TextLine> getST02() {
		TextCutRoleConfig<TextLine> textCutRoleConfig = new TextCutRoleConfig<>(TextCutType.BYTE, UTF8, line -> line);
		textCutRoleConfig.addRole(4);
		textCutRoleConfig.addRole(5);
		return textCutRoleConfig;

	}

	TextCutRoleConfig<TextLine> getST03() {
		TextCutRoleConfig<TextLine> textCutRoleConfig = new TextCutRoleConfig<>(TextCutType.BYTE, UTF8, line -> line);
		textCutRoleConfig.addRole(4);
		textCutRoleConfig.addRole(2);
		return textCutRoleConfig;

	}

}
