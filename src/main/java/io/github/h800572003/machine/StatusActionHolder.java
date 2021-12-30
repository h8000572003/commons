package io.github.h800572003.machine;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import com.google.common.collect.Maps;

import io.github.h800572003.exception.ApBusinessException;

/**
 * 
 * @author 6407
 *
 * @param <T>
 */
class StatusActionHolder<T extends IStatus, R> implements IStatusActionHolder<T, R> {
	private Map<String, Function<T, R>> map = Maps.newConcurrentMap();
	private NotFoundKeyPolicy<T, R> notFoundKeyPolicy;

	StatusActionHolder(NotFoundKeyPolicy<T, R> notFoundKeyPolicy) {
		super();
		this.notFoundKeyPolicy = notFoundKeyPolicy;
	}

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
		final Function<T, R> function = this.map.get(statusAction.toAction());
		if (function == null) {
			notFoundKeyPolicy.none(statusAction);
			// throw new ApBusinessException("action " + statusAction + " not
			// register");
		} else {

		}
		return function;
	}
}
