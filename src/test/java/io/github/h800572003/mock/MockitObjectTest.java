package io.github.h800572003.mock;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import io.github.h800572003.codegenerate.AppleVoDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class MockitObjectTest {

	@Test
	void test() {
		AppleVoDTO appleVoDTO = new AppleVoDTO();
		MockitObject mockitObject = new MockitObject();
		mockitObject.registerName("name", () -> "tome");
		AppleVoDTO random = (AppleVoDTO) mockitObject.random(appleVoDTO);
		log.info("{}", random);

		assertThat(random.getName()).isEqualTo("tome");
		assertThat(random.getName1()).isNotEmpty();
	}

}
