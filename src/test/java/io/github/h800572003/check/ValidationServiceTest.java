package io.github.h800572003.check;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import io.github.h800572003.check.ValidationStrategy.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class ValidationServiceTest {

	@Test
	void testGiveBlankDTOThen2ErrorThenWith2ErrorSize() {

		// GIVE
		final CheckDTO dto = new CheckDTO();

		// THEN
		ValidationStrategy<CheckDTO> create = this.create();

		// THEN
		IValidationService validationService = new ValidationService();
		boolean execute = validationService.execute(dto, create);
		assertThat(execute).isFalse();

	}

	public ValidationStrategy<CheckDTO> create() {
		final Builder<CheckDTO> builder = new ValidationStrategy.Builder<>();
		builder.checkContinue(i -> CheckResult.of("X1", "名稱不得空白", () -> CheckRoles.isNotNull(i.getName())));
		builder.checkContinue(i -> CheckResult.of("X2", "名稱不得空白", () -> CheckRoles.isBetween(i.getName(), 0, 10)));
		builder.setHandler(t -> {
			assertThat(t.getErrors()).hasSize(2);
			assertThat(t.isAllError()).isTrue();
		});
		return builder.build();
	}
}
