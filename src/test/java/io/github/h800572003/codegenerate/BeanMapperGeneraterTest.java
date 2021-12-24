package io.github.h800572003.codegenerate;

import org.junit.jupiter.api.Test;

class BeanMapperGeneraterTest {

	@Test
	void test() {
		AppleVoDTO dto = new AppleVoDTO();
		AppleVo vo = new AppleVo();

		BeanMapperGenerater beanGenerater = new BeanMapperGenerater();
		beanGenerater.generate(vo, dto);
	}

}
