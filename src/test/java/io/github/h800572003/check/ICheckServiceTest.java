package io.github.h800572003.check;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import io.github.h800572003.exception.ApBusinessException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class ICheckServiceTest {

	ICheckService checkService = new CheckService();

	public ICheckServiceTest() {
		final CheckRolesBuilder<CheckDTO> checkRolesBuilder = new CheckRolesBuilder<>(CheckDTO.class);
		checkRolesBuilder//
				.breakNext(i -> CheckResult.of("X1", "名稱不得空白", () -> CheckRoles.isNotNull(i.getName())))//
				.breakNext(i -> CheckResult.of("X1", "名稱不得小於6", () -> CheckRoles.isLessThan(i.getName().length(), 6)));//
		this.checkService.add(checkRolesBuilder);
	}

	@Test
	void test_give_name_when_chech_return_ok() {

		// GIVE
		final CheckDTO dto = new CheckDTO();
		dto.setName("Andy000");

		// WHEN
		final CheckResults checkResult = this.checkService.check(dto);

		// THEN
		assertThat(checkResult.isOk()).isEqualTo(true);
	}

	@Test
	void test_give_name_when_chech_return_error() {

		// GIVE
		final CheckDTO dto = new CheckDTO();

		// WHEN
		final CheckResults checkResult = this.checkService.check(dto);

		// THEN

		assertThat(checkResult.isError()).isEqualTo(true);

		checkResult.isError(i -> {
			i.stream().forEach(value -> log.info("{}", value.getMessage()));
			assertThat(i.size()).isEqualTo(1);
			assertThat(i.get(0).getCode()).isEqualTo("X1");
			assertThat(i.get(0).getMessage()).isEqualTo("名稱不得空白");
		});

	}

	@Test
	void test_give_name_when_chech_return_error2() {

		// GIVE
		final CheckDTO dto = new CheckDTO();

		// WHEN
		final CheckResults checkResult = this.checkService.check(dto);

		// THEN
		assertThatThrownBy(() -> {
			checkResult.ifError(r -> {
				return new ApBusinessException(r.getMessage());
			});
		}).isInstanceOf(ApBusinessException.class).hasMessage("名稱不得空白");

	}
}
