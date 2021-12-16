package io.github.h800572003.event.deiven;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import com.google.common.collect.Maps;

import io.github.h800572003.countdown.CountDown;
import io.github.h800572003.exception.ApBusinessExecpetion;
import io.github.h800572003.exception.CancelExecpetion;
import io.github.h800572003.scheduling.SpringSchedulingManager;

public class AsynDynamicRouter implements IMultDynamicRouter<IMessage>, AutoCloseable {

	private ExecutorService executor;

	private CountDown countDown;

	public AsynDynamicRouter(ExecutorService executor) {
		super();
		this.executor = executor;
		this.countDown = new CountDown();
	}

	public AsynDynamicRouter(String name, int size) {
		this(Executors.newScheduledThreadPool(size, new CustomizableThreadFactory(name)));
	}

	@SuppressWarnings("rawtypes")
	private Map<Class<? extends IMessage>, IChannel> map = Maps.newConcurrentMap();

	@Override
	public void registerChannel(Class<? extends IMessage> messageType, IChannel<? extends IMessage> channel) {
		map.put(messageType, channel);

	}

	@SuppressWarnings("unchecked")
	@Override
	public <T2 extends IMessage> void dispatch(T2 message) {
		if (this.map.containsKey(message.getClass())) {
			IChannel<T2> iChannel = this.map.get(message.getClass());
			executor.submit(() -> {
				countDown.increase();
				try {
					iChannel.dispatch(message);
				} finally {
					countDown.decrease();
				}

			});
		} else {
			throw new ApBusinessExecpetion("not match channel:{0}", message);
		}
	}

	@Override
	public void close() {
		executor.shutdown();
		try {
			countDown.await();
		} catch (InterruptedException e) {
			// 離開作業
		}
	}

}
