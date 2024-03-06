package io.github.h800572003.event.deiven;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

class DynamicRouterTest {

    @RepeatedTest(10000)
    void testCall() {

        new TestingDynamicRouter()
                .give(SampleInputMessage.class)//
                .give(SampleOutputChannel.class)//
                .test(new SampleInputMessage(2, 3))//
                .thenAssertCount(SampleInputMessage.class, 1)
                .thenAssertCount(SampleOutputChannel.class, 0);


    }

    @RepeatedTest(10)
    void testCallWithoutConfig() {
        new TestingDynamicRouter()
                .test(new SampleInputMessage(2, 3))
                .thenThrow((Consumer<Exception>) throwable -> throwable.getMessage().contains("Unregister"));
    }

    @RepeatedTest(10)
    void test_set_default() {
        //give this default IChannel
        AtomicInteger counter = new AtomicInteger(0);
        IChannel<IMessage> messageIChannel = new IChannel<IMessage>() {
            @Override
            public void dispatch(IMessage message) {
                counter.incrementAndGet();
            }
        };
        new TestingDynamicRouter()
                .giveDefaultMessage(messageIChannel)
                .test(new SampleInputMessage(2, 3));
        Assertions.assertEquals(1, counter.get());
    }
}
