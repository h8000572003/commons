package io.github.h800572003.scheduling;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;

/**
 * 關閉逾時中斷策略
 * <p>
 * 1.逾時後中斷所有排程執行緒 2.等待寬限時間 3.仍未停止則回報殘留任務與執行緒(可覆寫 {@link #onResidual})
 *
 * @author andy tsai
 */
@Slf4j
public class InterruptSchedulingShutdownPolicy implements ISchedulingShutdownPolicy {

	/**
	 * 預設中斷後寬限時間(秒)
	 */
	public static final long DEFAULT_GRACE_SECONDS = 10;

	private final long graceSeconds;

	public InterruptSchedulingShutdownPolicy() {
		this(DEFAULT_GRACE_SECONDS);
	}

	/**
	 * @param graceSeconds 中斷後寬限時間(秒)
	 */
	public InterruptSchedulingShutdownPolicy(long graceSeconds) {
		if (graceSeconds < 0) {
			throw new IllegalArgumentException("graceSeconds must not be negative:" + graceSeconds);
		}
		this.graceSeconds = graceSeconds;
	}

	@Override
	public void onTimeout(ISchedulingShutdownContext context) {
		log.warn("排程關閉逾時{}秒，中斷執行中任務:{}", context.getCloseTimeout(), codes(context.getRunningTasks()));
		context.interrupt();
		boolean terminated;
		try {
			terminated = context.awaitTermination(this.graceSeconds, TimeUnit.SECONDS);
		} catch (final InterruptedException e) {
			Thread.currentThread().interrupt();
			terminated = context.isTerminated();
		}
		if (!terminated) {
			onResidual(context);
		}
	}

	/**
	 * 中斷並等待寬限時間後仍未停止，預設記錄殘留任務與執行緒堆疊，可覆寫通知監控
	 *
	 * @param context 逾時資訊
	 */
	protected void onResidual(ISchedulingShutdownContext context) {
		log.error("排程中斷{}秒後仍未停止，殘留任務:{}", this.graceSeconds, codes(context.getRunningTasks()));
		for (final Thread thread : context.getAliveThreads()) {
			log.error("殘留執行緒:{} state:{}\n{}", thread.getName(), thread.getState(), stackTrace(thread));
		}
	}

	public long getGraceSeconds() {
		return this.graceSeconds;
	}

	private static List<String> codes(List<ISchedulingItemContext> items) {
		return items.stream().map(ISchedulingItemContext::getCode).collect(Collectors.toList());
	}

	private static String stackTrace(Thread thread) {
		return Stream.of(thread.getStackTrace()).map(element -> "\tat " + element)
				.collect(Collectors.joining("\n"));
	}
}
