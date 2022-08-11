package io.github.h800572003.backed;

public interface IBackendService {
	void start(BackendRunnable execute, String... args);

	void close();
}
