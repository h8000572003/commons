package io.github.h800572003.eventbuus;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.nio.file.WatchService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class EventBusTest {

	private SampleSubscribe sampleSubscribe = Mockito.spy(new SampleSubscribe());
	private SampleSubscribe2 sampleSubscribe2 = Mockito.spy(new SampleSubscribe2());

	@Test
	void testSampleEvent() {

		SampleEvent createDoorEvent = new SampleEvent();// 範例事件

		IBus bus = new EventBus("myBus");

		bus.register(sampleSubscribe);
		bus.register(sampleSubscribe2);

		bus.post(createDoorEvent);

		// Mockito.verify(sampleSubscribe,
		// Mockito.times(1)).handle(createDoorEvent);
		// Mockito.verify(sampleSubscribe2,
		// Mockito.times(1)).handle(createDoorEvent);
	}

}
