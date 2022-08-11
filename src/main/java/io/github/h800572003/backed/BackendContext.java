package io.github.h800572003.backed;

public interface BackendContext {
	/**
	 * 檢查
	 * 
	 * @throws BackendCancelException 中斷
	 */
	void checkUp() throws BackendCancelException;

	void updateMemo(String memo);

	String[] getArgs();

	/**
	 * 訂閱關閉事件
	 * 
	 * @param shutdownHookEvent 關閉事件
	 */
	void registerShutdownHook(ShutdownHookEvent shutdownHookEvent);

	@FunctionalInterface
	static interface ShutdownHookEvent {
		void shutdown();
	}
}
