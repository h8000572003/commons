package io.github.h800572003.event.deiven;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class AsynDynamicRouterTest {

	@Test
	void test() {

		try (final AsynDynamicRouter dynamicRouter = new AsynDynamicRouter("Test", 3)) {
			final SampleInputChannel sampleChannel = new SampleInputChannel(dynamicRouter);
			final SampleOutputChannel sampleOutputChannel = new SampleOutputChannel();

			dynamicRouter.registerChannel(SampleInputMessage.class, sampleChannel);
			dynamicRouter.registerChannel(SampleOutputMessage.class, sampleOutputChannel);

			final SampleInputMessage sampleInputMessage = new SampleInputMessage(1, 3);

			for (int i = 0; i < 10; i++) {
				dynamicRouter.dispatch(sampleInputMessage);
			}

			TimeUnit.SECONDS.sleep(2);

		} catch (InterruptedException e1) {
			log.info("timeout", e1);
		}
		log.info("end job");

	}

}
