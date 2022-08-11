package io.github.h800572003.type;

import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.Maps;
import io.github.h800572003.exception.ApBusinessException;

public class TypeService<I extends ITypeContext> {

	private Map<TypeKey, Function<I, ?>> map = Maps.newConcurrentMap();
	private NotFoundKeyPolicy<I> notFondPolicy = new ExceptionPolicy<I>();

	public TypeService() {
		this(new ExceptionPolicy<I>());
	}

	public TypeService(NotFoundKeyPolicy<I> notFondPolicy) {
		super();
		this.notFondPolicy = notFondPolicy;
	}

	public static <I extends ITypeContext, O> TypeService<I> createService(NotFoundKeyPolicy<I> notFondPolicy) {
		return new TypeService<I>(notFondPolicy);
	}

	public static <I extends ITypeContext> TypeService<I> createService() {
		return new TypeService<I>();
	}

	public interface NotFoundKeyPolicy<I> {
		Function<I, ?> none(Object typeServiceKey);
	}

	public static class ExceptionPolicy<I> implements NotFoundKeyPolicy<I> {

		@Override
		public Function<I, ?> none(Object typeServiceKey) {
			throw new ApBusinessException("未定義該狀態:");
		}

	}

	public static class MissPolicy<I> implements NotFoundKeyPolicy<I> {

		@Override
		public Function<I, ?> none(Object typeServiceKey) {
			return new Function<I, Object>() {
				@Override
				public Object apply(I t) {
					return null;
				}
			};
			// 忽略
		}

	}

	public void register(String status, Function<I, ?> function) {
		TypeKey typeKey = TypeKey.create(status);
		if (this.map.containsKey(typeKey)) {
			throw new ApBusinessException("已重複註冊{0}", status);
		}
		this.map.put(typeKey, function);
	}

	public Object dispatch(I input) {
		Function<I, ?> orDefault = this.map.getOrDefault(TypeKey.create(input), null);
		if (orDefault == null) {
			orDefault = notFondPolicy.none(input);
		}
		return orDefault.apply(input);
	}

}
