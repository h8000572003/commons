package io.github.h800572003.commons.scheduling;

/**
 * 排程服務監督程式
 * 
 * @author 6407
 *
 */
public interface IScheduingMonitor {
	

	public void updateStart(ISchedulingItemContext context);

	public void updateEnd(ISchedulingItemContext context);

	public void updateError(ISchedulingItemContext context, Throwable throwable);

	public void updateCancel(ISchedulingItemContext context);

	public void destroy(ISchedulingItemContext context);
}
