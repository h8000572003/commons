package io.github.h800572003.check;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class ICheckServiceTest {

	private static final String X2 = "X2";
	private static final String X1 = "X1";
	private static final String X3 = "X3";
	private Object checkSource;
	Consumer<CheckResultsContext> commonHandle;
	Consumer<CheckResultsContext> customerHandle;
	String handResult = "";

	public ICheckServiceTest() {
		log.info("ICheckServiceTest");
	}

	@BeforeEach
	public void init() {
		log.info("init");
		customerHandle = (Consumer<CheckResultsContext>) t -> {
			CheckDTO source = t.getSource(CheckDTO.class);
			checkSource = source;
			handResult = "customerHandle";
		};
		commonHandle = (Consumer<CheckResultsContext>) t -> {
			CheckDTO source = t.getSource(CheckDTO.class);
			checkSource = source;
			handResult = "commonHandle";
		};

	}

	private ICheckService createService(CreateServiceParameter parameterObject) {

		final CheckRolesBuilder<ICheckDTO> checkRolesBuilder = new CheckRolesBuilder<>(ICheckDTO.class);
		checkRolesBuilder//
				.next(i -> CheckResult.of(X1, "名稱不得空白", () -> CheckRoles.isNotNull(i.getName())),
						parameterObject.isBeak())//
				.next(i -> CheckResult.of(X2, "名稱不得小於6", () -> CheckRoles.isLessThan(i.getName().length(), 6)),
						parameterObject.isBeak())//
				.next(i -> CheckResult.of(X3, "名稱大於2", () -> CheckRoles.isBiggerThanOrEqualTo(i.getName().length(), 2)),
						parameterObject.isBeak())//
				.setErrorHandle(customerHandle);

		final CheckService checkService = new CheckService(commonHandle);
		if (parameterObject.isUseCommon()) {
			checkRolesBuilder.setErrorHandle(null);
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

		final CheckResultsContext checkResult = this.createService(new CreateServiceParameter(false, false)).check(dto);

		// THEN
		assertThat(checkResult.isAllOk()).isFalse();
		assertThat(checkResult.isAllError()).isFalse();
		assertThat(checkResult.getErrors()).size().isEqualTo(1);
		assertThat(checkResult.getOks()).size().isEqualTo(2);
		assertThat(checkResult.getErrors()//
				.stream().map(i -> i.getCode())//
				.collect(Collectors.joining(",")))//
						.isEqualTo(X3);//
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

		final CheckResultsContext checkResult = this.createService(parameterObject).check(dto);

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
	void test_handelCommonError() {

		this.createService(new CreateServiceParameter(false, true)).handleError(new CheckDTO());
		assertThat(handResult).isEqualTo("commonHandle");
	}

	/**
	 * 輸入姓名：Andy 呼叫 handleError
	 */
	@Test
	void test_handelCustomer() {

		this.createService(new CreateServiceParameter(false, false)).handleError(new CheckDTO());
		assertThat(handResult).isEqualTo("customerHandle");
	}

}
