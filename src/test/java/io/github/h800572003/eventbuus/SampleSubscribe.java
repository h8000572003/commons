package io.github.h800572003.eventbuus;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SampleSubscribe {

	@Subscribe
	public void handle(SampleEvent createDoorEvent) {
		log.info("get createDoorEvent");
	}
}
