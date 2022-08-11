package io.github.h800572003.check;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class ValidationStrategy<T> {
	private List<CheckStep> functions = new ArrayList<>();
	private Consumer<CheckResultsContext> cumstomerHandler;

	public ValidationStrategy(Builder<T> builder) {
		functions.addAll(builder.functions);
		this.cumstomerHandler = builder.cumstomerHandler;
	}

	public static class Builder<T> {
		private List<CheckStep> functions = new ArrayList<>();
		private Consumer<CheckResultsContext> cumstomerHandler;

		@SuppressWarnings("unchecked")
		public Builder<T> check(Function<T, CheckResult> function, boolean isBreak) {
			this.functions.add(new CheckStep((Function<Object, CheckResult>) function, isBreak));
			return this;
		}

		/**
		 * 異常中斷
		 * 
		 * @param function
		 * @return
		 */
		public Builder<T> checkBreak(Function<T, CheckResult> function) {
			this.check(function, true);
			return this;
		}

		/**
		 * 異常繼續
		 * 
		 * @param function
		 * @return
		 */
		public Builder<T> checkContinue(Function<T, CheckResult> function) {
			this.check(function, false);
			return this;
		}

		public Builder<T> setHandler(Consumer<CheckResultsContext> commonHandler) {
			this.cumstomerHandler = commonHandler;
			return this;
		}

		public ValidationStrategy<T> build() {
			return new ValidationStrategy<>(this);
		}
	}

	protected boolean execeute(T src, Consumer<CheckResultsContext> commonHandler) {
		final CheckResults checkResult = new CheckResults(cumstomerHandler == null ? commonHandler : cumstomerHandler,
				src);
		for (final CheckStep holder : this.functions) {
			final CheckResult result = holder.check(src);
			checkResult.add(result);
			if (result.isError() && holder.isBreak()) {
				break;
			}
		}
		checkResult.handle();
		return checkResult.isAllOk();
	}

}
