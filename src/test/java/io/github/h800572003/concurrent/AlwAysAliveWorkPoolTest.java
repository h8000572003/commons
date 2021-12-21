package io.github.h800572003.concurrent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.h800572003.concurrent.AlwAysAliveWorkPoolTest.BlockItem;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class AlwAysAliveWorkPoolTest implements WorkExecutor<BlockItem>, WorkAdpaterCallBackend<BlockItem> {

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
		OrderQueue<BlockItem> blockQueue = new OrderQueue<>();

		IWorkPool pool = new AlwAysAliveWorkPool<AlwAysAliveWorkPoolTest.BlockItem>(3, "Pool", blockQueue, this, this);
		List<BlockItem> collect = IntStream.range(0, 10).mapToObj(i -> new BlockItem(i)).collect(Collectors.toList());
		for (BlockItem item : collect) {
			blockQueue.add(item);
		}
		pool.start();

		try {
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.info("close");
		pool.close();
		log.info("end");
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
