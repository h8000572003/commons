package io.github.h800572003.log;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class Slf4jUpdateLogLeavelServiceTest {

	public static void main(String[] args) {
		Slf4jUpdateLogLeavelService slf4jUpdateLogLeavelService = new Slf4jUpdateLogLeavelService();
		slf4jUpdateLogLeavelService.registerLogback();
		slf4jUpdateLogLeavelService.change("root", "TRACE");

		log.info("INFO");
		log.debug("DEBUG");
		log.trace("TRACE");
		log.error("ERROR");
		slf4jUpdateLogLeavelService.change("root", "ERROR");

		log.info("2_INFO");
		log.debug("2_DEBUG");
		log.trace("2_TRACE");
		log.error("2_ERROR");
	}

}
