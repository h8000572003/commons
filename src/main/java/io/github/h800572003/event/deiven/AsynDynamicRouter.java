package io.github.h800572003.event.deiven;

import java.util.Map;
import java.util.concurrent.ExecutorService;

import com.google.common.collect.Maps;

import io.github.h800572003.exception.ApBusinessExecpetion;

public class AsynDynamicRouter implements IDynamicRouter<IMessage> {

	private ExecutorService executor;

	public AsynDynamicRouter(ExecutorService executor) {
		super();
		this.executor = executor;
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
			executor.submit(() -> iChannel.dispatch(message));
		} else {
			throw new ApBusinessExecpetion("not match channel:{0}", message);
		}
	}

	public void close() {
		executor.shutdown();
	}

}
