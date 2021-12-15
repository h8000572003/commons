package io.github.h800572003.event.deiven;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SampleInputChannel implements IChannel<SampleInputMessage> {

	private final IDynamicRouter<IMessage> routler;

	public SampleInputChannel(IDynamicRouter<IMessage> routler) {
		super();
		this.routler = routler;
	}

	@Override
	public void dispatch(SampleInputMessage message) {
		final int value1 = message.getValue1();
		final int value2 = message.getValue2();
		final int totoal = value1 + value2;
		log.info("add message: {}+{}:", message.getValue1(), message.getValue2(), totoal);

		final SampleOutputMessage sampleOutputMessage = new SampleOutputMessage(value1 + value2);
		this.routler.dispatch(sampleOutputMessage);

	}

}
