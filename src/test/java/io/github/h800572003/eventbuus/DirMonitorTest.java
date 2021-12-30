package io.github.h800572003.eventbuus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import io.github.chungtsai.cmd.TestCmdService;
import io.github.chungtsai.cmd.TestCmdService.CmdRunnable;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Disabled
class DirMonitorTest {

	FileChangeEventSubscribe fileChangeEventSubscribe = new FileChangeEventSubscribe();

	@Test
	void test() throws InterruptedException {
		IBus bus = new EventBus("myBus");
		bus.register(fileChangeEventSubscribe);
		IDirMonitor dirMonitor = new DirMonitor(bus, "/PIMCT/PEDIFIL/RISK");

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

		TestCmdService testCmdService = TestCmdService.create();

		testCmdService.addCacheRepeat(new CmdRunnable("writeA", () -> {
			try {
				writeFile();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}), 1);
		testCmdService.startJoin();

	}

	public void writeFile() throws InterruptedException {
		for (int i = 0; i < 100; i++) {
			File file = new File("/PIMCT/PEDIFIL/RISK/" + i + ".log");
			String name = new Date() + ":" + Thread.currentThread().getName() + "\n";
			try (FileOutputStream is = new FileOutputStream(file, true)) {
				// try (FileChannel channel = is.getChannel()) {
				// try (FileLock lock = channel.lock()) {
				log.info("write");
				TimeUnit.SECONDS.sleep(10);
				is.write(name.getBytes());
				// }
				// }

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
