package io.github.h800572003.objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.h800572003.exception.ApBusinessException;
import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class ApObjectsTest {

	@Test
	void give_null_when_requireNonNull_then_defalutMessage() {


		// GIVE NULL
		String value = null;

		// WHEN
		ApBusinessException exception = assertThrows(ApBusinessException.class, () -> {
			ApObjects.requireNonNull(value);
		});

		// THEN
		assertThat(exception).isNotNull();
		assertThat(exception.getMessage()).isNotBlank();

		log.info(exception.getMessage());
	}

	@Test
	void give_null_when_requireNonNullWithMessage_then_apBusinessException() {

		final String expected = "該欄位不得空白";

		// GIVE NULL
		String value = null;

		// WHEN

		ApBusinessException exception = assertThrows(ApBusinessException.class, () -> {
			ApObjects.requireNonNull(value, expected);
		});

		// THEN
		assertThat(exception).isNotNull();
		assertThat(exception.getMessage()).isEqualTo(expected);
	}

	@Test
	void give_null_when_requireNonNullWithSupplierMessage_then_apBusinessException() {

		final String expected = "該欄位不得空白";

		// GIVE NULL
		String value = null;

		// WHEN

		ApBusinessException exception = assertThrows(ApBusinessException.class, () -> {
			ApObjects.requireNonNull(value, () -> expected);
		});

		// THEN
		assertThat(exception).isNotNull();
		assertThat(exception.getMessage()).isEqualTo(expected);
	}

}
