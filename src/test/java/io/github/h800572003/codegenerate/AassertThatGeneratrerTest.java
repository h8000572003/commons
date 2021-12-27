package io.github.h800572003.codegenerate;

import org.junit.jupiter.api.Test;

class AassertThatGeneratrerTest {

	@Test
	void test() {
		new AassertThatGeneratrer().generateAssertThat(AppleVoDTO.class, AppleVo.class, AppleVo2.class);

	}

}
