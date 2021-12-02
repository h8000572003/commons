package com.github.h800572003.libs.backed;

public interface IBackendService {
	void start(BackendRunnable execute, String... args);

	void close();
}
