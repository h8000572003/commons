package io.github.h800572003.eventbuus;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Disabled
class DirMonitorTest {

	FileChangeEventSubscribe fileChangeEventSubscribe = new FileChangeEventSubscribe();

	@Test
	void test() {
		IBus bus = new EventBus("myBus");
		bus.register(fileChangeEventSubscribe);
		IDirMonitor dirMonitor = new DirMonitor(bus, "/EHU2", "");

		Thread thread = new Thread() {
			public void run() {
				try {
					dirMonitor.startMonitor();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			};
		};
		thread.start();
		log.info("start..");
		try {
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Paths.get("/EHU2", "1212.txt").toFile().delete();
		try (FileWriter writer = new FileWriter(Paths.get("/EHU2", "1212.txt").toFile())) {
			writer.write("ok");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		log.info("create file..");

		try {
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.info("interrupt");
		thread.interrupt();
		try {
			TimeUnit.SECONDS.sleep(30);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
