package io.github.h800572003.machine;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import com.google.common.collect.Maps;

import io.github.h800572003.exception.ApBusinessException;
import io.github.h800572003.machine.StatusActionHolder.NotFoundKeyPolicy;

public class StatusMachine<T extends IStatus, R> {
	private Map<String, StatusActionHolder<T, R>> statusMap = Maps.newConcurrentMap();
	private NotFoundKeyPolicy<T, R> notFoundKeyPolicy;

	public static class ExceptionPolicy<T, R> implements NotFoundKeyPolicy<T, R> {

		@Override
		public Function<T, R> none(StatusAction statusAction) {
			throw new ApBusinessException("action " + statusAction + " not register");
		}

	}

	public StatusMachine(NotFoundKeyPolicy<T, R> notFoundKeyPolicy) {
		this.notFoundKeyPolicy = Objects.requireNonNull(notFoundKeyPolicy);
	}

	public StatusMachine() {
		this(new ExceptionPolicy<T, R>());
	}

	public IStatusActionHolder<T, R> getStatusAction(IStatus status) {
		synchronized (StatusMachine.class) {
			return statusMap.compute(status.toStatus(), (k, v) -> {
				return v == null ? new StatusActionHolder<T, R>(this.notFoundKeyPolicy) : v;
			});
		}

	}

	public R run(StatusAction action, T src) {
		IStatusActionHolder<T, R> statusAction = this.getStatusAction(src);
		Function<T, R> function = statusAction.getAction(action);
		return function.apply(src);
	}
}
