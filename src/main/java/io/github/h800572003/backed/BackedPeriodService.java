package io.github.h800572003.backed;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class BackedPeriodService implements BackendContext, IBackendService {

	private boolean isUp = false;
	protected String name;
	protected long closeTimeout = 0;
	private BackendRunnable execute;
	private String memo;
	private ScheduledExecutorService newSingleThreadExecutor;
	private Future<?> future;
	private final long period;
	private String[] args;
	private final List<ShutdownHookEvent> shutdownHookEvents = new ArrayList<>();

	public BackedPeriodService(String name, long period) {
		this(name, period, 30);
	}

	public BackedPeriodService(String name, long period, int closeTimeout) {
		if (closeTimeout <= 0) {
			throw new BackendRuntimeException("關閉等待時間需大於0");
		}
		if (period <= 0) {
			throw new BackendRuntimeException("週期需大於0");
		}

		this.closeTimeout = closeTimeout;
		this.name = name;
		this.period = period;
		Runtime.getRuntime().addShutdownHook(new Thread("stopCommand") {
			@Override
			public void run() {
				BackedPeriodService.this.shutdownHookEvents.forEach(i -> i.shutdown());
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
		this.newSingleThreadExecutor = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {

			@Override
			public Thread newThread(Runnable r) {
				return new Thread(r, name);
			}
		});
		this.execute = execute;
		synchronized (this) {
			this.isUp = true;
		}
		this.future = this.newSingleThreadExecutor.scheduleWithFixedDelay(this::execute, 2, this.period,
				TimeUnit.SECONDS);

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