package io.github.h800572003.eventbuus;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class EventBus implements IBus {

	private Registry registry;
	private final Dispatchar dispatchar;
	private final String busName;

	public final class MyThreadFactory implements ThreadFactory {
		private int index = 0;

		@Override
		public Thread newThread(Runnable r) {
			return new Thread(r, busName + "_" + getSel());
		}

		public synchronized int getSel() {
			return index++;
		}

	}

	private static class NONEEventExceptionxHandler implements EventExceptionxHandler {

	}

	public EventBus(String busName, ExecutorService executor) {
		this(busName, executor, new NONEEventExceptionxHandler());
	}

	public EventBus(String busName) {
		this(busName, Executors.newSingleThreadExecutor());
	}

	public EventBus(String busName, ExecutorService executor, EventExceptionxHandler eventExceptionxhandler) {
		this.registry = new Registry();
		this.dispatchar = new Dispatchar(this, registry, executor, eventExceptionxhandler);
		this.busName = busName;
	}

	@Override
	public void register(Object subscriber) {
		registry.bind(subscriber);

	}

	@Override
	public void unRegister(Object subscriber) {
		registry.ubbind(subscriber);

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
