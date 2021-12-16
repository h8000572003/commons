package io.github.h800572003.eventbuus;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

public class EventBus implements IBus {

	private final Registry registry;
	private final Dispatchar dispatchar;
	private String busName;

	private static class NONEEventExceptionxHandler implements EventExceptionxHandler {

	}

	public EventBus(String busName, ExecutorService executor) {
		this(busName, executor, new NONEEventExceptionxHandler());
	}

	public EventBus(String busName) {
		this(busName, 1);
	}

	public EventBus(String busName, int size) {
		this(busName, Executors.newFixedThreadPool(size, new CustomizableThreadFactory(busName)));
	}

	public EventBus(String busName, EventExceptionxHandler eventExceptionxhandler, int size) {
		this(busName, Executors.newFixedThreadPool(size, new CustomizableThreadFactory(busName)),
				eventExceptionxhandler);
	}

	public EventBus(String busName, ExecutorService executor, EventExceptionxHandler eventExceptionxhandler) {
		this.busName = busName;
		this.registry = new Registry();
		this.dispatchar = new Dispatchar(this, this.registry, executor, eventExceptionxhandler);
	}

	@Override
	public void register(Object subscriber) {
		this.registry.bind(subscriber);

	}

	@Override
	public void unRegister(Object subscriber) {
		this.registry.ubbind(subscriber);

	}

	@Override
	public void post(Object evnet) {
		this.dispatchar.dispatch("default-topic", evnet);

	}

	@Override
	public void post(Object evnet, String topic) {
		this.dispatchar.dispatch(topic, evnet);

	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getBusName() {
		return this.busName;
	}

}
