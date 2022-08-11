package io.github.h800572003.crypto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class AscCryptoStrategyTest {

	private static final String EN_CODE = "9ePirQkp+ztTBTXXCC87qQ==";
	private static final String TEXT = "MyPASS";
	private static final String SALT = "SALT001234567890";

	@Test
	void test_GiveContextWhenEnCodeThenGetEnCode() {
		// GIVE
		ICryptoStrategy cryptoStrategy = new AesCryptoStrategy(SALT);

		// WHEN
		String enCode = cryptoStrategy.toEnCode(TEXT);

		// GIVE
		assertThat(enCode).isEqualTo(EN_CODE);
	}

	@Test
	void test_Give() {
		// GIVE
		ICryptoStrategy cryptoStrategy = new AesCryptoStrategy(SALT);

		// WHEN
		String deCode = cryptoStrategy.toDeCode(EN_CODE);

		// GIVE
		assertThat(deCode).isEqualTo(TEXT);
	}

}
