package io.github.h800572003.cmd;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.h800572003.exception.ApBusinessExecpetion;
import io.github.h800572003.properties.IPropertiesService;
import io.github.h800572003.scheduling.ISchedulingManager;

class CmdServiceTest {

	private ISchedulingManager schedulingManager = Mockito.mock(ISchedulingManager.class);
	private IPropertiesService propertiesService = Mockito.mock(IPropertiesService.class);
	private ObjectMapper objectMapper = Mockito.mock(ObjectMapper.class);
	private ICmdService cmdService = new CmdService();

	public CmdServiceTest() {
		new UpCmdHander(cmdService, schedulingManager);
		new DownCmdHander(cmdService, schedulingManager);
		new ClearHandler(cmdService, propertiesService);
		new DeletePropHandler(cmdService, propertiesService);
		new GetPropHandler(cmdService, propertiesService, objectMapper);
		new RreshCmdHander(cmdService, schedulingManager);
		new StartCmdHander(cmdService, schedulingManager);
		new UpPropHandler(cmdService, propertiesService);
		new WatchCmdHander(cmdService, schedulingManager, objectMapper);
	}

	/**
	 * 輸入無定義參數
	 */
	@Test
	void cmdUnkownCommadWithException() {
		assertThatExceptionOfType(ApBusinessExecpetion.class).isThrownBy(() -> {
			this.cmdService.execute("UNKOWN", "");
		}).withMessageContaining("無定義");
	}

	/**
	 * 1.輸入down 服務 2.參數:無 3.回傳 ok 4.呼叫 schedulingManager .down()
	 */
	@Test
	void cmdDownExecuteschedulingManagerDown() {
		assertThat(this.cmdService.execute(CmdCodesCofing.Codes.DOWN.name(), "")).isEqualTo(CmdContract.OK);
		Mockito.verify(schedulingManager).down();
	}

	/**
	 * 
	 */
	@Test
	void cmdClearWithoutParmatersWithExceptoin() {
		assertThatExceptionOfType(ApBusinessExecpetion.class).isThrownBy(() -> {
			this.cmdService.execute(CmdCodesCofing.Codes.CLEAR.name(), "");
		}).withMessageContaining("不提供");

	}

	@Test
	void cmdClearWithParmaters() {
		assertThat(this.cmdService.execute(CmdCodesCofing.Codes.CLEAR.name(), "XXX")).isEqualTo(CmdContract.OK);
		Mockito.verify(propertiesService).clear("XXX");
	}

	@Test
	void cmddelteWithoutParmatersWithExceptoin() {
		assertThatExceptionOfType(ApBusinessExecpetion.class).isThrownBy(() -> {
			this.cmdService.execute(CmdCodesCofing.Codes.DEPRO.name(), "");
		}).withMessageContaining("不提供");
	}

	@Test
	void cmddelteParmater() {
		assertThat(this.cmdService.execute(CmdCodesCofing.Codes.DEPRO.name(), "CATEGORY,KEY1"))
				.isEqualTo(CmdContract.OK);
		Mockito.verify(propertiesService).delete("CATEGORY", "KEY1");
	}

	@Test
	void cmdgetProParmater() {
		assertThat(this.cmdService.execute(CmdCodesCofing.Codes.GETPRO.name(), "CATEGORY"));
		Mockito.verify(propertiesService).getPropertie("CATEGORY");
	}

	@Test
	void cmdRefrshParmater() {
		assertThat(this.cmdService.execute(CmdCodesCofing.Codes.REFRESH.name(), "CATEGORY")).isEqualTo(CmdContract.OK);
		Mockito.verify(schedulingManager).refresh("CATEGORY");
	}

	@Test
	void cmdStartAll() {
		assertThat(this.cmdService.execute(CmdCodesCofing.Codes.START.name(), "")).isEqualTo(CmdContract.OK);
		Mockito.verify(schedulingManager).startAll();
	}

	@Test
	void cmdStartByCode() {
		assertThat(this.cmdService.execute(CmdCodesCofing.Codes.START.name(), "CATEGORY")).isEqualTo(CmdContract.OK);
		Mockito.verify(schedulingManager).start("CATEGORY");
	}

	@Test
	void cmdUp() {
		assertThat(this.cmdService.execute(CmdCodesCofing.Codes.UP.name(), "")).isEqualTo(CmdContract.OK);
		Mockito.verify(schedulingManager).up();
	}

	@Test
	void cmdWatch() {
		assertThat(this.cmdService.execute(CmdCodesCofing.Codes.WATCH.name(), ""));
		Mockito.verify(schedulingManager).getContext();
	}
}
