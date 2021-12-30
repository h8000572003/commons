package io.github.h800572003.order;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import io.github.h800572003.concurrent.OrderQueue;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OrderItemTest {

	private List<OrderItem> items = new ArrayList<>();

	@Test
	void testOrder() throws InterruptedException {
		OrderQueue<OrderItem> blockQueue = new OrderQueue<>();

		IntStream.range(0, 10)//
		.mapToObj(i -> new OrderItem(i + "", i + "", i))//
		.forEach(blockQueue::add);//

		while (!blockQueue.isEmpty()) {
			OrderItem item = blockQueue.take();
			log.info("item:{}", item);
			blockQueue.remove(item);
		}

	}

}
