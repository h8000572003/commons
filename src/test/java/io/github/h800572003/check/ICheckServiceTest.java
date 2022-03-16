package io.github.h800572003.check;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class ICheckServiceTest {

	private boolean isCustomHandle = false;

	public ICheckServiceTest() {
		this.isCustomHandle = false;
		log.info("ICheckServiceTest");
	}

	private void handle(List<CheckResult> checkStatuses) {
		log.info("call customer checkStatuses:{}", ToStringBuilder.reflectionToString(checkStatuses));
		this.isCustomHandle = true;
	}

	private void commonhandle(List<CheckResult> checkStatuses) {
		log.info("call customer checkStatuses:{}", ToStringBuilder.reflectionToString(checkStatuses));
		this.isCustomHandle = true;
	}

	@BeforeEach
	public void init() {
		log.info("init");

		this.isCustomHandle = false;
	}

	private ICheckService createService(CreateServiceParameter parameterObject) {

		final CheckRolesBuilder<CheckDTO> checkRolesBuilder = new CheckRolesBuilder<>(CheckDTO.class);
		checkRolesBuilder//
				.next(i -> CheckResult.of("X1", "名稱不得空白", () -> CheckRoles.isNotNull(i.getName())), parameterObject.isBeak())//
				.next(i -> CheckResult.of("X2", "名稱不得小於6", () -> CheckRoles.isLessThan(i.getName().length(), 6)),
						parameterObject.isBeak())//
				.next(i -> CheckResult.of("X3", "名稱大於2",
						() -> CheckRoles.isBiggerThanOrEqualTo(i.getName().length(), 2)), parameterObject.isBeak())//
				.setErrorHandle(this::handle);

		final CheckService checkService = new CheckService();
		if (parameterObject.isUseCommon()) {
			checkRolesBuilder.setErrorHandle(null);
			checkService.setCommonHandler(this::commonhandle);
		}
		checkService.add(checkRolesBuilder);

		return checkService;
	}

	/**
	 * 輸入姓名：A 規則：姓名 2~6碼之間 檢查結果： X1 [O] X2 [O] X3 [X]
	 * 
	 */
	@Test
	void test_give_name_when_chech_return_false() {

		// GIVE
		final CheckDTO dto = new CheckDTO();
		dto.setName("A");

		// WHEN

		final CheckResults checkResult = this.createService(new CreateServiceParameter(false, false)).check(dto);

		// THEN
		assertThat(checkResult.isAllOk()).isFalse();
		assertThat(checkResult.isAllError()).isFalse();
		assertThat(checkResult.getErrors()).size().isEqualTo(1);
		assertThat(checkResult.getOks()).size().isEqualTo(2);
		assertThat(checkResult.getErrors()//
				.stream().map(i -> i.getCode())//
				.collect(Collectors.joining(",")))//
						.isEqualTo("X3");//

		assertThat(this.isCustomHandle).isFalse();
	}

	/**
	 * 輸入姓名：A 規則：姓名 2~6碼之間 檢查結果： X1 [O] X2 [O] X3 [X]
	 * 
	 */
	@Test
	void test_give_name_when_chech_return_true() {

		// GIVE
		final CheckDTO dto = new CheckDTO();
		dto.setName("Andy");

		// WHEN
		CreateServiceParameter parameterObject = new CreateServiceParameter(false, false);
		
		
		
		final CheckResults checkResult = this.createService(parameterObject).check(dto);

		// THEN
		assertThat(checkResult.isAllOk()).isTrue();// =>all true
		assertThat(checkResult.isAllError()).isFalse();// all error fasle
		assertThat(checkResult.getErrors()).size().isEqualTo(0);
		assertThat(checkResult.getOks()).size().isEqualTo(3);
	}

	/**
	 * 輸入姓名：Andy 呼叫 handleError
	 */
	@Test
	void test_handelError() {

		this.createService(new CreateServiceParameter(false, false)).handleError(new CheckDTO());

		assertThat(this.isCustomHandle).isTrue();

	}

}
