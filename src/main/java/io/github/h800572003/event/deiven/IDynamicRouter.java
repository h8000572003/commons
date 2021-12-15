package io.github.h800572003.event.deiven;

public interface IDynamicRouter<E extends IMessage> {

	void registerChannel(Class<? extends E> messageType, IChannel<? extends E> channel);

	<T2 extends E> void dispatch(T2 message);
}
