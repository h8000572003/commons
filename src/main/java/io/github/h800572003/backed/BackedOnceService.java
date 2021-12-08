package io.github.h800572003.backed;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class BackedOnceService implements BackendContext, IBackendService {

	private boolean isUp = false;
	protected String name;
	protected int closeTimeout = 0;
	private BackendRunnable execute;
	private String memo;
	private ExecutorService newSingleThreadExecutor;
	private Future<?> future;
	private final List<ShutdownHookEvent> shutdownHookEvents = new ArrayList<>();
	private String[] args;

	public BackedOnceService(String name) {
		this(name, 30);
	}

	public BackedOnceService(String name, int closeTimeout) {
		if (closeTimeout <= 0) {
			throw new BackendRuntimeException("關閉等待時間需大於0");
		}
		this.newSingleThreadExecutor = Executors.newSingleThreadExecutor();
		this.closeTimeout = closeTimeout;
		this.name = name;
		Runtime.getRuntime().addShutdownHook(new Thread("stopCommand") {
			@Override
			public void run() {
				BackedOnceService.this.shutdownHookEvents.forEach(i -> i.shutdown());
				close();
			}
		});

	}

	public void execute() {
		this.execute.execute(this);

	}

	@Override
	public final void start(BackendRunnable execute, String... args) {

		if (this.execute != null) {
			throw new BackendRuntimeException("服務已啟動");
		}
		this.args = args;
		this.execute = execute;
		synchronized (this) {
			this.isUp = true;
		}
		this.future = this.newSingleThreadExecutor.submit(this::execute);

	}

	@Override
	public final void close() {
		synchronized (this) {
			this.isUp = false;
		}
		if (this.future != null) {
			this.newSingleThreadExecutor.shutdown();
			callBackend();
			try {
				this.future.cancel(true);
			} finally {
				this.newSingleThreadExecutor.shutdownNow();
				this.newSingleThreadExecutor = null;
			}
		}
	}

	private void callBackend() {
		try {
			this.newSingleThreadExecutor.awaitTermination(this.closeTimeout, TimeUnit.SECONDS);
		} catch (final InterruptedException e) {
			// 超時，結束等待
		}
	}

	@Override
	public void updateMemo(String memo) {
		this.memo = memo;
	}

	@Override
	public void checkUp() throws BackendCancelException {
		synchronized (this) {
			if (!this.isUp) {
				throw new BackendCancelException("服務中斷");
			}
		}
	}

	@Override
	public void registerShutdownHook(ShutdownHookEvent shutdownHookEvent) {
		this.shutdownHookEvents.add(shutdownHookEvent);
	}

	@Override
	public String[] getArgs() {
		return args;
	}
}