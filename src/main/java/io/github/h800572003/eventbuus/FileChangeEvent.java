package io.github.h800572003.eventbuus;

import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;

public class FileChangeEvent {

	private final Path path;
	private final WatchEvent.Kind<?> kinds;
	public FileChangeEvent(Path path, Kind<?> kinds) {
		super();
		this.path = path;
		this.kinds = kinds;
	}
	public Path getPath() {
		return this.path;
	}
	public WatchEvent.Kind<?> getKinds() {
		return this.kinds;
	}

}
