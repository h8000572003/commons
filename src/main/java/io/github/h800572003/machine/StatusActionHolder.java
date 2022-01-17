package io.github.h800572003.machine;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import com.google.common.collect.Maps;

/**
 * 
 * @author andy tsai
 *
 * @param <T>
 */
class StatusActionHolder<T extends IStatus, R> implements IStatusActionHolder<T, R> {
	private Map<String, Function<T, R>> map = Maps.newConcurrentMap();
	private NotFoundKeyPolicy<T, R> notFoundKeyPolicy;

	/**
	 * 
	 * @param notFoundKeyPolicy
	 *            策略
	 */
	StatusActionHolder(NotFoundKeyPolicy<T, R> notFoundKeyPolicy) {
		super();
		this.notFoundKeyPolicy = notFoundKeyPolicy;
	}

	/**
	 * 找不到策略
	 * 
	 * @author andy tsai
	 *
	 * @param <T>
	 * @param <R>
	 */
	public interface NotFoundKeyPolicy<T, R> {
		Function<T, R> none(StatusAction statusAction);
	}

	@Override
	public void register(StatusAction acion, Function<T, R> consumer) {
		Objects.requireNonNull(acion);
		Objects.requireNonNull(consumer);
		synchronized (StatusActionHolder.class) {
			this.map.put(acion.toAction(), consumer);
		}

	}

	@Override
	public Function<T, R> getAction(StatusAction statusAction) {
		Objects.requireNonNull(statusAction);
		if (this.map.containsKey(statusAction.toAction())) {
			return this.map.get(statusAction.toAction());
		} else {
			return this.notFoundKeyPolicy.none(statusAction);
		}
	}
}
