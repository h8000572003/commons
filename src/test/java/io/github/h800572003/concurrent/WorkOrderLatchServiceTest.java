package io.github.h800572003.concurrent;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import io.github.h800572003.concurrent.WorkOrderLatchServiceTest.Order;
import io.github.h800572003.order.OrderBlockPool;
import io.github.h800572003.order.IOrderBlockPool;
import io.github.h800572003.order.IOrderBlockPool.OrderKey;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class WorkOrderLatchServiceTest implements WorkExecutor<Order>, ErrorCallable<Order> {

	@Data
	static class Order implements OrderKey {

		int workExe = 1;
		int index = 0;
		String key;

		public Order(int index, int workExe, String key) {
			super();
			this.workExe = workExe;
			this.index = index;
			this.key = key;
		}

		@Override
		public String toKey() {
			return key;
		}

	}

	@Test
	void test() {

		WorkLatchDoneCallable<Order> workLatchDoneCallable = new WorkLatchDoneCallable<Order>() {
			@Override
			public void call(Order src, Throwable throwable) {
				log.info("done src:{}", ToStringBuilder.reflectionToString(src));

			}
		};
		WorkOrderLatchServiceTest sorkOrderLatchServiceTest = Mockito.spy(new WorkOrderLatchServiceTest());
		IOrderBlockPool<Order> pool = OrderBlockPool.getPool();
		WorkOrderLatchService<Order> createWorkOrderLatchService = WorkOrderLatchService.createWorkOrderLatchService(
				"WorkOrderLatchServiceTest", 1, sorkOrderLatchServiceTest, pool, null, 30, workLatchDoneCallable);
		try (WorkOrderLatchService<Order> newService = createWorkOrderLatchService) {
			try {

				for (int index = 0; index < 2; index++) {

					IntStream.range(0, 2).forEach(i -> {
						int nextInt = ThreadLocalRandom.current().nextInt(1, 3);
						newService.addItem(new Order(i, nextInt, "A"));
					});
					IntStream.range(0, 2).forEach(i -> {
						int nextInt = ThreadLocalRandom.current().nextInt(1, 3);
						newService.addItem(new Order(i, nextInt, "B"));
					});

				}
				newService.execute();

			} catch (InterruptedException e) {
				log.info("任務中斷");
			}
		}
		Mockito.verify(sorkOrderLatchServiceTest, Mockito.times(8)).execute(Mockito.any());
	}

	@Override
	public void execute(Order t, Throwable throwable) {
		log.info("order :{}", t, throwable);

	}

	@Override
	public void execute(Order t) {
		log.info("start order :{}", t);
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		log.info("end order :{}", t);

	}

}
