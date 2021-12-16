package io.github.h800572003.event.deiven;

import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SampleOutputChannel implements IChannel<SampleOutputMessage> {

	@Override
	public void dispatch(SampleOutputMessage message) {
		log.info("get message:{}", message.getResult());

		try {
			TimeUnit.SECONDS.sleep(5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		log.info("end message:{}", message.getResult());
	}

}
