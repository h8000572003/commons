package io.github.h800572003.concurrent;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.h800572003.concurrent.WorkLatchServiceSampleTest.Item;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WorkLatchServiceSampleTest implements WorkExecutor<Item>, WorkAdpaterCallBackend<Item> {

	static Integer value = 0;
	static Object MUTE = new Object();

	static class Item implements IBlockKey {

		int workExe = 1;
		String key = "1";

		public Item(int workExe, String key) {
			super();
			this.workExe = workExe;
			this.key = key;
		}

		public Item(int workExe) {
			this(workExe, workExe + "");
		}

		@Override
		public String toKey() {
			return this.key + "";
		}

	}

	@BeforeEach
	void before() {
		value = 0;
	}

	@Test
	void testExecuteWhen() {
		BlockQueue<Item> blockQueue = new BlockQueue<Item>(1000);
		try (WorkLatchService<Item> newService = WorkLatchService.newService("WORK", blockQueue, 2, this, this)) {
			try {

				List<Item> collect = IntStream.range(0, 10).mapToObj(i -> new Item(i)).collect(Collectors.toList());

				newService.execute(collect);
			} catch (InterruptedException e) {
				log.info("任務中斷");
			}
		}
		assertThat(value).isEqualTo(10);
	}

	@Override
	public void execute(Item t) {
		synchronized (MUTE) {
			value++;
		}
		log.info("exeucte key:{} item:{} start", t.key, t.workExe);

	}

	@Override
	public void call(Item src, Throwable throwable) {
		// TODO Auto-generated method stub

	}

}
