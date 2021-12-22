package io.github.h800572003.concurrent;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;

import io.github.chungtsai.cmd.TestCmdService;
import io.github.chungtsai.cmd.TestCmdService.CmdRunnable;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class ObjectDownLatchTest {

	@Test
	void testAwaitAAndB3TimePass() {

		AtomicInteger a = new AtomicInteger();
		AtomicInteger b = new AtomicInteger();

		List<String> list = Lists.newArrayList("A", "B");

		ObjectDownLatch objectDownLatch = new ObjectDownLatch(list);

		TestCmdService create = TestCmdService.create();
		for (String line : list) {
			for (int i = 0; i < 3; i++) {
				create.addCache(new CmdRunnable("work", () -> {
					log.info("remove:{} ", line);
					objectDownLatch.remove(line, 3);
					if (line.equalsIgnoreCase("A")) {
						a.incrementAndGet();
					}
					if (line.equalsIgnoreCase("B")) {
						b.incrementAndGet();
					}
				}, 3, 0));
			}

		}
		create.addCache(new CmdRunnable("wait", () -> {
			try {
				log.info("wait ");
				objectDownLatch.await();
				assertThat(a.intValue()).isEqualTo(3);
				assertThat(b.intValue()).isEqualTo(3);
				log.info("go ");
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}));
		create.startJoin();
		try {
			log.info("wait ");
			objectDownLatch.await();
			assertThat(a.intValue()).isEqualTo(3);
			assertThat(b.intValue()).isEqualTo(3);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		log.info("done ");
	}

}
