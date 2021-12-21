package io.github.h800572003.type;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.Test;

import io.github.h800572003.exception.ApBusinessException;

public class TypeServiceTest {

	static class ActionA {

		public Out function(DTO dto) {
			return new Out(dto);
		}

	}

	class DTO implements ITypeContext {
		String value;

		public DTO(String value) {
			super();
			this.value = value;
		}

		@Override
		public String toTypeId() {
			return value;
		}

	}

	static class Out {
		DTO dto;

		public Out(DTO dto) {
			super();
			this.dto = dto;
		}

	}

	@Test
	void testWithRegisterStatus() {

		ActionA actionA = new ActionA();// TYPE_A >ACTION

		TypeService<DTO> createService = TypeService.createService();
		createService.register("TYPE_A", actionA::function);

		DTO dto = new DTO("TYPE_A");
		Out dispatch = (Out) createService.dispatch(dto);

		assertThat(dispatch.dto).isEqualTo(dto);
	}

	@Test
	void testWithoutRegisterStatus() {
		ActionA actionA = new ActionA();
		TypeService<DTO> createService = TypeService.createService();
		DTO dto = new DTO("NONE_TYPE");

		assertThatExceptionOfType(ApBusinessException.class).isThrownBy(() -> {
			createService.dispatch(dto);
		}).withMessageContaining("未定義該狀態");

	}

}
