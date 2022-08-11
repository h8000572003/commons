package io.github.h800572003.check;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.Test;

class CheckRolesTest {

	@Test
	void testRoles() {
		assertThat(CheckRoles.isNotNull(null)).isFalse();
		assertThat(CheckRoles.isNotNull("abc")).isTrue();

		assertThat(CheckRoles.isBetween(10, 10, 10)).isTrue();
		assertThat(CheckRoles.isBetween(5, 0, 10)).isTrue();
		assertThat(CheckRoles.isBetween(5, 6, 10)).isFalse();

		assertThat(CheckRoles.isNotBlank("hello")).isTrue();

		assertThat(CheckRoles.isBiggerThan(10, 5)).isTrue();
		assertThat(CheckRoles.isBiggerThan(10, 11)).isFalse();

		assertThat(CheckRoles.isLessThan(10, 5)).isFalse();
		assertThat(CheckRoles.isLessThan(10, 11)).isTrue();

		Calendar instance = Calendar.getInstance();
		instance.add(Calendar.DAY_OF_MONTH, 5);
		assertThat(CheckRoles.isLessThan(new Date(), instance.getTime(), 6)).isTrue();
	}

}
