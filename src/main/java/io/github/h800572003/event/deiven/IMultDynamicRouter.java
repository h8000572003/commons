package io.github.h800572003.event.deiven;

public interface IMultDynamicRouter<E extends IMessage> extends IDynamicRouter<E> {

	void close();
}
