package io.github.h800572003.eventbuus;

import java.lang.reflect.InvocationTargetException;
import java.util.Queue;
import java.util.concurrent.ExecutorService;

/**
 * 指派
 * 
 * @author andy tsai
 *
 */
public class Dispatchar {

	private final ExecutorService executor;
	private final EventExceptionxHandler eventExceptionxhandler;
	private final IBus bus;
	private final Registry registry;

	public Dispatchar(IBus bus, Registry registry, ExecutorService executor,
			EventExceptionxHandler eventExceptionxhandler) {
		super();
		this.executor = executor;
		this.eventExceptionxhandler = eventExceptionxhandler;
		this.bus = bus;
		this.registry = registry;
	}

	/**
	 * 指派
	 * 
	 * @param topic
	 *            主題
	 * @param event
	 *            事件
	 */
	public void dispatch(String topic, Object event) {
		final Queue<Subscriber> subscribers = this.registry.getQueueByTopic(topic);
		if (subscribers == null) {
			if (this.eventExceptionxhandler != null) {
				eventExceptionxhandler.handle("無訂閱者");
			}
		} else {
			subscribers.stream().filter(i -> {
				final Class<?> class1 = i.getMethod().getParameterTypes()[0];
				return class1.isAssignableFrom(event.getClass());
			}).forEach(i -> this.invoke(this.bus, i, event));
		}
	}

	public void invoke(IBus busm, Subscriber subscriber, Object event) {
		this.executor.execute(() -> {
			try {
				subscriber.getMethod().invoke(subscriber.getSubscriber(), event);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				if (this.eventExceptionxhandler != null) {
					eventExceptionxhandler.handle("invoke error:" + event.getClass().getName(), e);
				}
			}
		});

	}

	public void close() {

		if (this.executor != null) {
			if (this.executor.isShutdown()) {
				this.executor.shutdown();
			}
		}

	}

}
