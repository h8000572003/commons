package io.github.h800572003.event.deiven;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

class AsynDynamicRouterTest {

	@Test
	void test() {

		final ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(3);
		final AsynDynamicRouter dynamicRouter = new AsynDynamicRouter(newFixedThreadPool);

		final SampleInputChannel sampleChannel = new SampleInputChannel(dynamicRouter);
		final SampleOutputChannel sampleOutputChannel = new SampleOutputChannel();

		dynamicRouter.registerChannel(SampleInputMessage.class, sampleChannel);
		dynamicRouter.registerChannel(SampleOutputMessage.class, sampleOutputChannel);

		final SampleInputMessage sampleInputMessage = new SampleInputMessage(1, 3);

		for (int i = 0; i < 1000; i++) {
			dynamicRouter.dispatch(sampleInputMessage);
		}

		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}

		dynamicRouter.close();
	}

}
