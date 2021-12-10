package io.github.h800572003.status;

import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.Maps;

import io.github.h800572003.exception.ApBusinessExecpetion;

public class TypeService<I, O> {

	private Map<String, Function<I, O>> map = Maps.newConcurrentMap();
	private NotFoundKeyPolicy<I, O> notFondPolicy = new ExceptionPolicy<I, O>();

	public TypeService() {
		this(new ExceptionPolicy<I, O>());
	}

	public TypeService(NotFoundKeyPolicy<I, O> notFondPolicy) {
		super();
		this.notFondPolicy = notFondPolicy;
	}

	public static <I, O> TypeService<I, O> createService(NotFoundKeyPolicy<I, O> notFondPolicy) {
		return new TypeService<I, O>(notFondPolicy);
	}

	public static <I, O> TypeService<I, O> createService() {
		return new TypeService<I, O>();
	}

	public interface NotFoundKeyPolicy<I, O> {
		Function<I, O> none(StatusKey typeServiceKey);
	}

	public static class ExceptionPolicy<I, O> implements NotFoundKeyPolicy<I, O> {

		@Override
		public Function<I, O> none(StatusKey typeServiceKey) {
			throw new ApBusinessExecpetion("未定義該狀態:{0}", typeServiceKey.toStatus());
		}

	}

	public static class MissPolicy<I, O> implements NotFoundKeyPolicy<I, O> {

		@Override
		public Function<I, O> none(StatusKey typeServiceKey) {
			return new Function<I, O>() {

				@Override
				public O apply(I t) {
					return null;
				}
			};
			// 忽略
		}

	}

	public interface StatusKey {
		String toStatus();
	}

	public void register(StatusKey typeServiceKey, Function<I, O> function) {
		this.map.put(typeServiceKey.toStatus(), function);
	}

	public O action(StatusKey typeServiceKey, I input) {
		Function<I, O> orDefault = this.map.getOrDefault(typeServiceKey.toStatus(), null);
		if (orDefault == null) {
			orDefault = notFondPolicy.none(typeServiceKey);
		}
		return orDefault.apply(input);
	}

}
