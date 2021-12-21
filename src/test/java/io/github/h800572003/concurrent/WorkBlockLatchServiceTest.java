package io.github.h800572003.concurrent;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.h800572003.concurrent.WorkBlockLatchServiceTest.BlockItem;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class WorkBlockLatchServiceTest implements WorkExecutor<BlockItem>, WorkAdpaterCallBackend<BlockItem> {
	static Integer value = 0;
	static Object MUTE = new Object();

	@EqualsAndHashCode
	@ToString
	static class BlockItem implements IBlockKey {

		int workExe = 1;
		String key = "1";

		public BlockItem(int workExe, String key) {
			super();
			this.workExe = workExe;
			this.key = key;
		}

		public BlockItem(int workExe) {
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
	void testExecuteWhen2() {
		IOrderKeyQueue<BlockItem> blockQueue = new OrderQueue<>();

		try (WorkLatchService<BlockItem> newService = WorkLatchService.newService("WORK_", blockQueue, 2, this, this)) {
			try {
				List<BlockItem> collect = IntStream.range(0, 10).mapToObj(i -> new BlockItem(i))
						.collect(Collectors.toList());

				newService.execute(blockQueue, collect);
			} catch (InterruptedException e) {
				log.info("任務中斷");
			}
		}
		assertThat(value).isEqualTo(10);
	}

	@Override
	public void execute(BlockItem t) {
		synchronized (MUTE) {
			value++;
		}
		log.info("exeucte item:{}", t);

	}

	@Override
	public void call(BlockItem src, Throwable throwable) {
		log.info("exeucte item:{}", src);

	}

}
