package io.github.h800572003.status;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.Test;

import io.github.h800572003.exception.ApBusinessExecpetion;
import io.github.h800572003.scheduling.Sample;
import io.github.h800572003.status.TypeService.StatusKey;

public class TestTypeService {

	static class ActionA implements StatusKey {

		@Override
		public String toStatus() {
			return ActionA.class.getName();
		}

		public Out function(DTO dto) {
			return new Out(dto);
		}

	}

	class DTO {
		String value;

		public DTO(String value) {
			super();
			this.value = value;
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

		ActionA actionA = new ActionA();
		TypeService<DTO, Out> createService = TypeService.createService();
		createService.register(actionA, actionA::function);
		DTO dto = new DTO("A");
		Out action = createService.action(actionA, dto);

		assertThat(action.dto).isEqualTo(dto);
	}

	@Test
	void testWithoutRegisterStatus() {
		ActionA actionA = new ActionA();
		TypeService<DTO, Out> createService = TypeService.createService();
		DTO dto = new DTO("A");

		assertThatExceptionOfType(ApBusinessExecpetion.class).isThrownBy(() -> {
			createService.action(actionA, dto);
		}).withMessageContaining("未定義該狀態");
		
		
	}

}
