package io.github.h800572003.textcut;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;

class TextCutHelperTest {

	@Test
	void test_inputByteCut2DTO() {

		// GIVE
		String text = "A BBCCC";

		TextCutRoleConfig<TextDTO> textCutRoleConfig = new TextCutRoleConfig<TextDTO>(TextCutType.BYTE, "utf-8",
				this::to);

		textCutRoleConfig.addRole(2);
		textCutRoleConfig.addRole(2);
		textCutRoleConfig.addRole(3);

		// THEN
		TextDTO textDTO = TextCutHelper.to(textCutRoleConfig, text);
		assertThat(textDTO.getValue1()).isEqualTo("A");
		assertThat(textDTO.getValue2()).isEqualTo("BB");
		assertThat(textDTO.getValue3()).isEqualTo("CCC");

	}

	@Test
	void test_inputCharCut2DTO() {

		// GIVE
		String text = "國家BBCCC";

		TextCutRoleConfig<TextDTO> textCutRoleConfig = new TextCutRoleConfig<TextDTO>(TextCutType.CHAR, "utf-8",
				this::to);

		textCutRoleConfig.addRole(2);
		textCutRoleConfig.addRole(2);
		textCutRoleConfig.addRole(3);

		// THEN
		TextDTO textDTO = TextCutHelper.to(textCutRoleConfig, text);
		assertThat(textDTO.getValue1()).isEqualTo("國家");
		assertThat(textDTO.getValue2()).isEqualTo("BB");
		assertThat(textDTO.getValue3()).isEqualTo("CCC");
	}

	@Test
	void test_inputListCut2DTO() {

		// GIVE
		List<String> text = Lists.newArrayList("國家BBCCC", "0 11222");

		TextCutRoleConfig<TextDTO> textCutRoleConfig = new TextCutRoleConfig<TextDTO>(TextCutType.CHAR, "utf-8",
				this::to);

		textCutRoleConfig.addRole(2);
		textCutRoleConfig.addRole(2);
		textCutRoleConfig.addRole(3);

		// THEN
		List<TextDTO> textDTOs = TextCutHelper.to(textCutRoleConfig, text);
		TextDTO textDTO = textDTOs.get(0);
		assertThat(textDTO.getValue1()).isEqualTo("國家");
		assertThat(textDTO.getValue2()).isEqualTo("BB");
		assertThat(textDTO.getValue3()).isEqualTo("CCC");
		
		
	}

	TextDTO to(TextLine line) {
		List<String> values = line.getLines();

		TextDTO textDTO = new TextDTO();

		textDTO.setValue1(values.get(0));
		textDTO.setValue2(values.get(1));
		textDTO.setValue3(values.get(2));
		return textDTO;

	}

	@Test
	void test() {

	}
}
