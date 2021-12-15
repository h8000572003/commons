package io.github.h800572003.eventbuus;

import java.io.IOException;
import java.nio.file.WatchEvent;

public interface IDirMonitor {

	void startMonitor(WatchEvent.Kind<?>... events) throws IOException;

	void close() throws IOException;

}