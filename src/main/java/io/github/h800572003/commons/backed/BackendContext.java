package io.github.h800572003.commons.backed;

public interface BackendContext {
	/**
	 * 檢查
	 * 
	 * @throws BackendCancelException
	 */
	void checkUp() throws BackendCancelException;

	void updateMemo(String memo);

	String[] getArgs();

	/**
	 * 訂閱關閉事件
	 * 
	 * @param shutdownHookEvent
	 */
	void registerShutdownHook(ShutdownHookEvent shutdownHookEvent);

	@FunctionalInterface
	static interface ShutdownHookEvent {
		void shutdown();
	}
}
