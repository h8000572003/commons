//package io.github.h800572003.order;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.junit.jupiter.api.Test;
//
//import io.github.chungtsai.cmd.TestCmdService;
//import io.github.chungtsai.cmd.TestCmdService.CmdRunnable;
//import io.github.h800572003.concurrent.IOrderQueue;
//import io.github.h800572003.concurrent.IOrderQueue.OrderKey;
//import io.github.h800572003.concurrent.OrderQueue;
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//public class OrderItemTest {
//
//	private List<OrderItem> items = new ArrayList<>();
//
//	@Test
//	void testOrder() {
//		
//
//	
////		List<String> excepts = new ArrayList<String>();
////		excepts.add(Stream.of(add, add3, add4).map(Object::toString).collect(Collectors.joining()));
////		excepts.add(Stream.of(add2).map(Object::toString).collect(Collectors.joining()));
////
//		List<OrderKey> outList = new ArrayList<>();
////
//		List<String> outResults = new ArrayList<String>();
////		while (pool.hasNext()) {
////			OrderItem orderItem = pool.take();
////			outList.add(orderItem);
////			log.info("########TAKE_ALL");
////
////			String collect = orderItem.toString();
////			outResults.add(collect);
////
////		}
////
////		log.info("期望值");
////		excepts.forEach(i -> log.info(i));
////
////		log.info("結果值");
////		outResults.forEach(i -> log.info(i));
////
////		IntStream.range(0, 2).forEach(i -> assertThat(outResults.get(i)).isEqualTo(excepts.get(i)));
////		assertThat(outList.size()).isEqualTo(this.items.size());
////
////		log.info("end");
////		
//		IOrderQueue<OrderItem> pool = new OrderQueue<OrderItem>();
//		
//		TestCmdService.create()
//		.addCache(new CmdRunnable("add", ()->{
//			OrderItem add = this.add("A", "B", 1);
//			OrderItem add2 = this.add("A", "B", 2);
//			OrderItem add3 = this.add("B", "1", 3);
//			OrderItem add4 = this.add("B", "2", 4);
//			pool.add(add);
//			pool.add(add2);
//			pool.add(add3);
//			pool.add(add4);
//
//		})).addCache(new CmdRunnable("get", ()->{
//			
//			
//			while (pool.hasNext()) {
//				OrderItem orderItem = pool.take();
//				outList.add(orderItem);
//				log.info("########TAKE_ALL");
//
//				String collect = orderItem.toString();
//				outResults.add(collect);
//
//			}
//
//			
//		})).startJoin();
//		
//	}
//
//	public OrderItem add(String value1, String value2, int order) {
//		OrderItem orderItem = new OrderItem(value1, value2, order);
//		this.items.add(orderItem);
//		return orderItem;
//	}
//}
