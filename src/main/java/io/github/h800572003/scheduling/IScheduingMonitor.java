package io.github.h800572003.scheduling;

/**
 * 排程服務監督程式
 * 
 * @author andy tsai
 *
 */
public interface IScheduingMonitor {
	

	public void updateStart(ISchedulingItemContext context);

	public void updateEnd(ISchedulingItemContext context);

	public void updateError(ISchedulingItemContext context, Throwable throwable);

	public void updateCancel(ISchedulingItemContext context);

	public void destroy(ISchedulingItemContext context);
}
