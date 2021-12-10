package io.github.h800572003.concurrent;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.h800572003.concurrent.WorkLatchService.ErrorCallable;
import io.github.h800572003.concurrent.WorkLatchService.WorkExecutor;
import io.github.h800572003.concurrent.WorkLatchServiceSampleTest.Item;
import io.github.h800572003.exception.ApBusinessExecpetion;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WorkLatchServiceSampleTest implements WorkExecutor<Item>, ErrorCallable<Item> {

	static Integer value = 0;
	static Object MUTE = new Object();

	static class Item {

		int workExe = 1;
		int index = 0;

		public Item(int index, int workExe) {
			super();
			this.workExe = workExe;
			this.index = index;
		}

	}

	@BeforeEach
	void before() {
		value = 0;
	}

	@Test
	void testExecuteWhen() {
		try (WorkLatchService<Item> newService = WorkLatchService.newService("WORK", 2, this,this,5)) {
			for (int index = 0; index < 2; index++) {
				log.info("#########index :{}########", index);

				IntStream.range(0, 5).forEach(i -> {
					int nextInt = ThreadLocalRandom.current().nextInt(1, 3);
					newService.addItem(new Item(i, nextInt));
				});
				try {
					newService.execute();
				} catch (InterruptedException e) {
					log.info("任務中斷");
					break;
				}

			}
		}
		assertThat(value).isEqualTo(10);
	}

	@Override
	public void execute(Item t) {
		synchronized (MUTE) {
			value++;
		}
		log.info("exeucte index:{} item:{} start", t.index, t.workExe);
		throw new ApBusinessExecpetion("發生錯誤");

	}

	@Override
	public void execute(Item t, Throwable throwable) {
		log.info("exeucte index:{} item:{} exception", t.index, t.workExe, throwable);

	}

}
