package io.github.h800572003.event.deiven;

public class Message implements IMessage {

	@Override
	public Class<? extends IMessage> getType() {
		return getClass();
	}
}
