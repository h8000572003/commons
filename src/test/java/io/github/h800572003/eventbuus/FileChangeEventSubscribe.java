package io.github.h800572003.eventbuus;

import org.apache.commons.lang3.builder.ToStringBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileChangeEventSubscribe {
	@Subscribe
	public void handle(FileChangeEvent fileChangeEvent) {
		log.info("get FileChangeEvent:{}", ToStringBuilder.reflectionToString(fileChangeEvent));
	}

}
