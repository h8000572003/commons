package io.github.h800572003.batch;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import io.github.h800572003.exception.ApBusinessExecpetion;
import io.github.h800572003.exception.CancelExecpetion;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BactchTaskHolder implements IBactchTaskHolder, IBactchTaskExecuterContext {

	protected IBactchTaskExecuter bactchTaskExecuter;

	private ScheduledExecutorService newSingleThreadExecutor;
	private ScheduledFuture<?> future;
	private boolean isCallDown = false;
	private IBactchTaskConfig config;
	private boolean isOn = false;

	public BactchTaskHolder(IBactchTaskConfig config, IBactchTaskExecuter bactchTaskExecuter) {
		this.config = config;
		this.bactchTaskExecuter = bactchTaskExecuter;
	}

	@Override
	public void on() {
		this.start();
	}

	private synchronized void start() {
		if (newSingleThreadExecutor != null) {

		} else {
			newSingleThreadExecutor = Executors
					.newSingleThreadScheduledExecutor(r -> new Thread(r, "BACTCH_" + config.getCode()));
			log.info("start code{}", config.getCode());
			this.future = this.newSingleThreadExecutor.scheduleWithFixedDelay(this::execute, getDelay(),
					config.getPeriod(), TimeUnit.SECONDS);
			this.isOn = true;
		}

	}

	@Override
	public synchronized void off() {
		if (this.future != null) {
			if (!isCallDown) {
				this.isCallDown = true;
				this.newSingleThreadExecutor.shutdown();
				this.future.cancel(true);
				try {
					log.info("down code{}", config.getCode());
					// try {
					this.newSingleThreadExecutor.shutdown();
					this.future.cancel(true);
					try {
						if (!this.newSingleThreadExecutor.awaitTermination(config.getCloseTimeout(),
								TimeUnit.SECONDS)) {
							List<Runnable> shutdownNow = this.newSingleThreadExecutor.shutdownNow();
							log.info("shutdownNow size:{}", shutdownNow.size());
						}
					} catch (InterruptedException e) {
						this.newSingleThreadExecutor.shutdownNow();
					}

				} finally {
					this.newSingleThreadExecutor = null;
					this.future = null;
					this.isCallDown = false;
					this.isOn = false;
					log.info("down code{} end", config.getCode());
				}
			} else {
				throw new ApBusinessExecpetion("batch " + config.getCode() + "停止中");
			}

		} else {
			throw new ApBusinessExecpetion("batch " + config.getCode() + "未執行");
		}

	}

	public void execute() {
		try {
			this.bactchTaskExecuter.execute(this);
		} catch (Exception e) {
			log.info("發生異常", e);
		}

	}

	@Override
	public void checkUp() throws CancelExecpetion {
		if (this.isCallDown) {
			throw new CancelExecpetion("程式中斷");
		}

	}

	@Override
	public boolean isOn() {
		return isOn;
	}

	@Override
	public String getCode() {
		return config.getCode();
	}

	@Override
	public String getName() {
		return config.getName();
	}

	@Override
	public long getPeriod() {
		return config.getPeriod();
	}

	@Override
	public long getCloseTimeout() {
		return config.getCloseTimeout();
	}

	@Override
	public String getNextSec() {
		if (this.future == null) {
			return "";

		}
		return this.future.getDelay(TimeUnit.SECONDS) + "";
	}

	@Override
	public long getDelay() {
		return config.getDelay();
	}

}
