package io.github.h800572003.eventbuus;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;

import com.google.common.collect.Lists;

import io.github.h800572003.exception.ApBusinessExecpetion;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DirMonitor {

	private WatchService service;
	private final IBus eventBus;
	private Path path;
	private volatile boolean start = false;

	public DirMonitor(IBus eventBus, String targe, String... morePath) {
		super();
		this.eventBus = eventBus;
		this.start = start;
		this.path = Paths.get(targe, morePath);
	}

	public void startMonitor() throws IOException {
		this.startMonitor(Lists.newArrayList());
	}

	public void startMonitor(List<WatchEvent.Kind<?>> events) throws IOException {
		if (events.isEmpty()) {
			events.add(StandardWatchEventKinds.ENTRY_CREATE);
			events.add(StandardWatchEventKinds.ENTRY_DELETE);
			events.add(StandardWatchEventKinds.ENTRY_MODIFY);
			events.add(StandardWatchEventKinds.OVERFLOW);
		}
		if (this.service != null) {
			throw new ApBusinessExecpetion("服務已啟動");
		}
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {

				log.info("Shutdown");
				this.service.close();
			} catch (IOException e) {
				log.error(e.getMessage());
			}
		}));

		this.service = FileSystems.getDefault().newWatchService();
		this.path.register(service, events.toArray(new WatchEvent.Kind<?>[events.size()]));

		this.start = true;
		while (start) {

			try {
				WatchKey take = service.take();
				List<WatchEvent<?>> pollEvents = take.pollEvents();

				pollEvents.forEach(e -> {
					Path path = (Path) e.context();
					Kind<?> kind = e.kind();
					Path resolve = this.path.resolve(path);
					eventBus.post(new FileChangeEvent(resolve, kind));
				});
			} catch (InterruptedException e1) {
				start = false;
				log.info("中斷作業");
			}

		}
		log.info("close..");

	}

	public void close() throws IOException {
		this.start = false;
	}

}
