package io.github.h800572003.order;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import io.github.h800572003.order.IOrderBlockPool.OrderKey;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OrderItemTest {

	private List<OrderItem> items = new ArrayList<>();

	@Test
	void testOrder() {
		IOrderBlockPool<OrderItem> pool = OrderBlockPool.getPool();

		OrderItem add = this.add("A", "B", 1);
		OrderItem add2 = this.add("A", "B", 2);
		OrderItem add3 = this.add("B", "1", 3);
		OrderItem add4 = this.add("B", "2", 4);
		pool.addAll(this.items);

		List<String> excepts = new ArrayList<String>();
		excepts.add(Stream.of(add, add3, add4).map(Object::toString).collect(Collectors.joining()));
		excepts.add(Stream.of(add2).map(Object::toString).collect(Collectors.joining()));

		List<OrderKey> outList = new ArrayList<>();

		List<String> outResults = new ArrayList<String>();
		try {
			while (pool.hasNext()) {
				List<OrderItem> takeAll = pool.takeAll();
				outList.addAll(takeAll);
				log.info("########TAKE_ALL");
				takeAll.forEach(i -> {
					log.info("item:{}", i);
					pool.removeKey(i);
				});

				String collect = takeAll.stream().map(Object::toString).collect(Collectors.joining());
				outResults.add(collect);

			}
		} catch (InterruptedException e) {

		}


		log.info("期望值");
		excepts.forEach(i -> log.info(i));

		log.info("結果值");
		outResults.forEach(i -> log.info(i));

		IntStream.range(0, 2).forEach(i -> assertThat(outResults.get(i)).isEqualTo(excepts.get(i)));
		assertThat(outList.size()).isEqualTo(this.items.size());

		log.info("end");
	}

	public OrderItem add(String value1, String value2, int order) {
		OrderItem orderItem = new OrderItem(value1, value2, order);
		this.items.add(orderItem);
		return orderItem;
	}
}
