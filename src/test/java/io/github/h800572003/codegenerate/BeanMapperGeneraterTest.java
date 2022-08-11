package io.github.h800572003.codegenerate;

import org.junit.jupiter.api.Test;

class BeanMapperGeneraterTest {

	@Test
	void test() {

		BeanMapperGenerater beanGenerater = new BeanMapperGenerater();
		beanGenerater.generate(AppleVo.class, AppleVoDTO.class);

		AppleVoDTO appleVoDTO = new AppleVoDTO();
		AppleVo appleVo = new AppleVo();
		appleVo.setName(appleVoDTO.getName());

	}

}
