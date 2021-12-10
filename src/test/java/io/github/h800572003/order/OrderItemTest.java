package io.github.h800572003.order;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import io.github.chungtsai.cmd.TestCmdService;
import io.github.chungtsai.cmd.TestCmdService.CmdRunnable;
import io.github.h800572003.order.OrderPool.OrderKey;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OrderItemTest {

	private List<OrderItem> items = new ArrayList<>();

	@Test
	void testOrder() {

		this.add("A", "B");
		this.add("A", "B");
		this.add("A", "B");
		this.add("A", "B");
		this.add("A", "B");
		this.add("B", "1");
		this.add("B", "2");
		this.add("B", "3");
		OrderPool<OrderKey> pool = OrderPool.getPool();
		items.forEach(i -> pool.add(i));

		try {
			while (pool.hasNext()) {
				List<OrderKey> takeAll = pool.takeAll();
				log.info("########TAKE_ALL");
				takeAll.forEach(i -> {
					log.info("item:{}", i);
					try {
						TimeUnit.SECONDS.sleep(1);
						pool.removeKey(i);
					} catch (InterruptedException e) {
					}
				});
			}
		} catch (InterruptedException e) {

		}

		log.info("end");
	}

	public void add(String value1, String value2) {
		this.items.add(new OrderItem(value1, value2));
	}
}
