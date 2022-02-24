package io.github.h800572003.check;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class ICheckServiceTest3 {

	ICheckService checkService = new CheckService();

	public ICheckServiceTest3() {
		CheckRolesBuilder<CheckDTO> checkRolesBuilder = new CheckRolesBuilder<>(CheckDTO.class);
		checkRolesBuilder//
				.continueNext(this::role1)//
				.continueNext(this::role2);//
		this.checkService.add(checkRolesBuilder);
	}

	@Test
	void test_give_name_when_chech_return_ok() {

		// GIVE 空白名稱
		CheckDTO dto = new CheckDTO();
		dto.setName(null);

		// WHEN：資料檢核有不通過時，不繼續檢查
		CheckResults checkResult = checkService.check(dto);

		// THEN
		assertThat(checkResult.isError()).isEqualTo(true);
		checkResult.isError(i -> {
			i.stream().forEach(value -> log.info("{}", value.getMessage()));
			assertThat(i.size()).isEqualTo(2);
		});
	}

	CheckResult role1(CheckDTO checkDTO) {
		final CheckResult status = CheckResult.of("X1", "名稱不得空白", dto -> {
			if (checkDTO.getName() == null) {
				return false;
			}
			return true;
		});

		return status;
	}

	CheckResult role2(CheckDTO checkDTO) {
		final CheckResult status = CheckResult.of("X1", "名稱不得小於6", dto -> {
			if (checkDTO.getName().length() < 6) {
				return false;
			}
			return true;
		});
		return status;
	}
}
