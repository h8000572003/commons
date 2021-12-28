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
import java.util.stream.Stream;

import com.google.common.collect.Lists;

import io.github.h800572003.exception.ApBusinessException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DirMonitor implements IDirMonitor {

	private WatchService service;
	private final IBus eventBus;
	private final Path path;
	private volatile boolean start = true;
	List<WatchEvent.Kind<?>> eventsList = Lists.newArrayList();

	public DirMonitor(IBus eventBus, String targe) {
		this.eventBus = eventBus;
		this.path = Paths.get(targe);

	}

	@Override
	public void startMonitor(WatchEvent.Kind<?>... events) throws IOException {
		if (this.service != null) {
			throw new ApBusinessException("服務已啟動");
		}
		addEventList(events);
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {

				log.info("Shutdown");
				this.service.close();
			} catch (final IOException e) {
				log.error(e.getMessage(), e);
			}
		}));

		this.service = FileSystems.getDefault().newWatchService();
		this.path.register(this.service, this.eventsList.toArray(new WatchEvent.Kind<?>[this.eventsList.size()]));
		while (!Thread.currentThread().isInterrupted()) {

			try {
				final WatchKey take = this.service.take();
				final List<WatchEvent<?>> pollEvents = take.pollEvents();

				pollEvents.forEach(e -> {
					final Path path = (Path) e.context();
					final Kind<?> kind = e.kind();
					final Path resolve = this.path.resolve(path);
					this.eventBus.post(new FileChangeEvent(resolve, kind));
				});
				if (!take.reset()) {
					log.info("exit watch server");
				}
			} catch (final InterruptedException e1) {
				this.start = false;
				log.info("中斷作業");
			}

		}
		log.info("close..");

	}

	private void addEventList(WatchEvent.Kind<?>... events) {
		if (events.length == 0) {
			this.eventsList.add(StandardWatchEventKinds.ENTRY_CREATE);
			this.eventsList.add(StandardWatchEventKinds.ENTRY_DELETE);
			this.eventsList.add(StandardWatchEventKinds.ENTRY_MODIFY);
			this.eventsList.add(StandardWatchEventKinds.OVERFLOW);
		} else {
			Stream.of(events).forEach(this.eventsList::add);
		}
	}

	@Override
	public void close() throws IOException {
		this.start = false;
	}

}
