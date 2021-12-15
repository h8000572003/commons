package io.github.h800572003.event.deiven;

public interface IChannel<E extends IMessage> {

	void dispatch(E message);

}