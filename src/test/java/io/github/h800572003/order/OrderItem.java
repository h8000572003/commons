package io.github.h800572003.order;

import io.github.h800572003.order.IOrderBlockPool.OrderKey;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class OrderItem implements OrderKey {

	private String value1;
	private String value2;
	private int order = 0;

	public OrderItem(String value1, String value2, int order) {
		super();
		this.value1 = value1;
		this.value2 = value2;
		this.order = order;
	}

	@Override
	public String toKey() {
		return value1 + value2;
	}

}
