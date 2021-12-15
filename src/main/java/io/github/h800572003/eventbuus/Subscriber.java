package io.github.h800572003.eventbuus;

import java.lang.reflect.Method;

public class Subscriber {

	private boolean isDisable = false;
	private final Object subscriber;
	private final Method method;

	public Subscriber(Object subscriber, Method method) {
		this.subscriber = subscriber;
		this.method = method;
	}

	public boolean isDisable() {
		return this.isDisable;
	}

	public void setDisable(boolean isDisable) {
		this.isDisable = isDisable;
	}

	public Object getSubscriber() {
		return this.subscriber;
	}

	public Method getMethod() {
		return this.method;
	}

}
