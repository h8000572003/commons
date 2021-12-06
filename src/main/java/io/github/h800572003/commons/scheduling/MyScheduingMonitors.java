package io.github.h800572003.commons.scheduling;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * 被觀察者
 * 
 * @author 6407
 *
 */
@Slf4j
public class MyScheduingMonitors implements IScheduingMonitor {

	protected List<IScheduingMonitor> scheduingMonitors = new ArrayList<>();

	/**
	 * 註冊
	 * 
	 * @param scheduingMonitor
	 */
	public void register(IScheduingMonitor scheduingMonitor) {
		log.info("register type:{}", scheduingMonitor.getClass().getName());
		this.scheduingMonitors.add(scheduingMonitor);
	}

	@Override
	public void updateStart(ISchedulingItemContext context) {
		this.scheduingMonitors.forEach(item -> item.updateStart(context));

	}

	@Override
	public void updateEnd(ISchedulingItemContext context) {
		this.scheduingMonitors.forEach(item -> item.updateEnd(context));

	}

	@Override
	public void updateError(ISchedulingItemContext context, Throwable throwable) {
		this.scheduingMonitors.forEach(item -> item.updateError(context, throwable));

	}

	@Override
	public void updateCancel(ISchedulingItemContext context) {
		this.scheduingMonitors.forEach(item -> item.updateCancel(context));

	}

	@Override
	public void destroy(ISchedulingItemContext context) {
		this.scheduingMonitors.forEach(item -> item.destroy(context));

	}

}
