package io.github.h800572003.concurrent;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.chungtsai.cmd.TestCmdService;
import io.github.chungtsai.cmd.TestCmdService.CmdRunnable;

class GetNoServiceInMemTest {

	private static final int REPEAT_COUNT = 100;
	private String KEY = "KEY_";
	private int size = 8;

	class Holder {
		List<String> noGroup = new ArrayList<>();
		public void add(String line) {
			synchronized (noGroup) {
				noGroup.add(line);
			}
		}
	}

	class MyRunn implements Runnable {

		private Holder holder;
		private IGetNoService getNoService;

		public MyRunn(Holder holder, IGetNoService getNoService) {
			super();
			this.holder = holder;
			this.getNoService = getNoService;
		}

		@Override
		public void run() {
			String no = getNoService.getNo(KEY, size);
			holder.add(no);

		}

	}

	/**
	 * GIVE：當預設條件為0時， 
	 * WHEN：取100次 THEN： 
	 * THEN：
	 * <li>最後一次序號為100號 
	 * <li>資料不重複
	 */
	@Test
	void giveZeroWhenGiveCount_THEN_REPEAT_100() {

		// GIVE
		IGetNoService getNoService = new GetNoServiceInMem();

		// WHEN
		Holder holder = new Holder();
		TestCmdService.create()//
				.addCacheRepeat(new CmdRunnable("Get", new MyRunn(holder, getNoService)), REPEAT_COUNT)//
				.startJoin();//

		// THEN
		String lastNo = holder.noGroup.get(REPEAT_COUNT - 1);
		assertThat(lastNo).isEqualTo("KEY_00000100");
		assertThat(holder.noGroup.stream().distinct().count()).isEqualTo(100);

	}

}
