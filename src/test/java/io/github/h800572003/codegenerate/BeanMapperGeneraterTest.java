package io.github.h800572003.codegenerate;

import org.junit.jupiter.api.Test;

class BeanMapperGeneraterTest {

	@Test
	void test() {

		BeanMapperGenerater beanGenerater = new BeanMapperGenerater();
		beanGenerater.generate(AppleVoDTO.class, AppleVo.class);

		AppleVoDTO appleVoDTO = new AppleVoDTO();
		AppleVo appleVo = new AppleVo();
		appleVo.setName(appleVoDTO.getName());
	}

	public AppleVo to(AppleVoDTO appleVoDTO) {
		if (appleVoDTO == null) {
			return null;
		}

		AppleVo appleVo = new AppleVo();
		appleVo.setName(appleVoDTO.getName());
		return appleVo;
	}

}
