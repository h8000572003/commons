package io.github.h800572003.event.deiven;

import org.junit.jupiter.api.Test;

class DynamicRouterTest {

	@Test
	void testCall() {
		final DynamicRouter dynamicRouter = new DynamicRouter();

		final SampleInputChannel sampleChannel = new SampleInputChannel(dynamicRouter);
		final SampleOutputChannel sampleOutputChannel = new SampleOutputChannel();

		dynamicRouter.registerChannel(SampleInputMessage.class, sampleChannel);
		dynamicRouter.registerChannel(SampleOutputMessage.class, sampleOutputChannel);

		final SampleInputMessage sampleInputMessage = new SampleInputMessage(2, 3);
		dynamicRouter.dispatch(sampleInputMessage);

	}

}
