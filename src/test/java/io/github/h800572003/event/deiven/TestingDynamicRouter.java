package io.github.h800572003.event.deiven;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Slf4j
public class TestingDynamicRouter<T extends IMessage> {

    private final DynamicRouter dynamicRouter = new DynamicRouter();

    private final Map<String, CounterChannel> countMap = new HashMap<>();


    private Exception throwable;

    public TestingDynamicRouter give(Class<T> messagClass) {
        CounterChannel counterChannel = new CounterChannel();
        countMap.put(messagClass.getName(), counterChannel);
        dynamicRouter.registerChannel(messagClass, counterChannel);
        return this;
    }

    public TestingDynamicRouter thenThrow(Consumer<Exception> throwableConsumer) {
        throwableConsumer.accept(throwable);
        return this;
    }

    public TestingDynamicRouter giveDefaultMessage(IChannel<IMessage> messageIChannel) {
        dynamicRouter.setDefaultChannel(messageIChannel);
        return this;
    }

    static class CounterChannel implements IChannel<IMessage> {
        private final AtomicInteger atomicInteger = new AtomicInteger(0);

        @Override
        public void dispatch(IMessage message) {
            atomicInteger.incrementAndGet();
        }
    }

    public TestingDynamicRouter test(T message) {
        try {
            dynamicRouter.dispatch(message);
        } catch (Exception e) {
            throwable = e;
        }
        return this;
    }

    public TestingDynamicRouter thenAssertCount(Class<T> pClass, int i) {
        CounterChannel counterChannel = countMap.get(pClass.getName());
        Assertions.assertEquals(i, counterChannel.atomicInteger.get());
        return this;
    }
}
