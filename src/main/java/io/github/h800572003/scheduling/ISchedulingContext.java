package io.github.h800572003.scheduling;

import java.util.List;

import io.github.h800572003.exception.ApBusinessExecpetion;
import io.github.h800572003.scheduling.SpringSchedulingManager.ISpringSchedulingProperites;

public interface ISchedulingContext {

	List<ISchedulingItemContext> getAll();

	String getHostName();

	/**
	 * 是否執行者
	 * 
	 * @return
	 */
	boolean isExecuter();

	/**
	 * 是否執行中
	 * 
	 * @return
	 */
	boolean isStart();

	default void ifExecuter(Runnable runnable) {
		if (isExecuter()) {
			runnable.run();
		} else {
			throw new ApBusinessExecpetion("非執行者{0}無權限操作", this.getHostName());
		}
	}

	default void ifExecuterNotMessage(Runnable runnable) {
		if (isExecuter()) {
			runnable.run();
		}
	}

	default void ifNotExecuter(Runnable runnable) {
		if (!isExecuter()) {
			runnable.run();
		} else {
			throw new ApBusinessExecpetion("執行者{0}不提供該操作", this.getHostName());
		}
	}

	int getRunningCount();
	
	ISpringSchedulingProperites getProperites();
}
