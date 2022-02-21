package io.github.h800572003.check;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class ICheckServiceTest {

	ICheckService checkService = new CheckService();

	public ICheckServiceTest() {
		final CheckRolesBuilder<CheckDTO> checkRolesBuilder = new CheckRolesBuilder<>(CheckDTO.class);
		checkRolesBuilder//
				.breakNext(this::role1)//
				.breakNext(this::role2);//
		this.checkService.add(checkRolesBuilder);
	}

	@Test
	void test_give_name_when_chech_return_ok() {

		// GIVE
		final CheckDTO dto = new CheckDTO();
		dto.setName("Andy000");

		// WHEN
		final CheckResult checkResult = this.checkService.check(dto);

		// THEN
		assertThat(checkResult.isOk()).isEqualTo(true);
	}

	CheckRule role1(CheckDTO checkDTO) {
		final CheckRule status = CheckRule.of("X1", "名稱不得空白", dto -> {
			if (checkDTO.getName() == null) {
				return false;
			}
			return true;
		});

		return status;
	}

	CheckRule role2(CheckDTO checkDTO) {
		final CheckRule status = CheckRule.of("X1", "名稱不得小於6", dto -> {
			if (checkDTO.getName().length() < 6) {
				return false;
			}
			return true;
		});
		return status;
	}

	@Test
	void test_give_name_when_chech_return_error() {

		// GIVE
		final CheckDTO dto = new CheckDTO();

		// WHEN
		final CheckResult checkResult = this.checkService.check(dto);

		// THEN

		assertThat(checkResult.isError()).isEqualTo(true);

		checkResult.isError(i -> {
			i.stream().forEach(value -> log.info("{}", value.getMessage()));
			assertThat(i.size()).isEqualTo(1);
			assertThat(i.get(0).getCode()).isEqualTo("X1");
			assertThat(i.get(0).getMessage()).isEqualTo("名稱不得空白");
		});

	}
}
