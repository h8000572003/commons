package io.github.h800572003.event.deiven;

public class Message implements IMessage {

    private final String typeKey;

    public Message() {
        this.typeKey = getClass().getName();
    }

    @Override
    public final String getTypeKey() {
        return typeKey;
    }
}
