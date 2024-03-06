package io.github.h800572003.event.deiven;

public interface IDynamicRouter<E extends IMessage> {

    default void registerChannel(Class<E> messageType, IChannel<E> channel) {
        registerChannelByName(messageType.getName(), channel);
    }


    void registerChannelByName(String message, IChannel<E> channel);

    <T2 extends E> void dispatch(T2 message);
}
