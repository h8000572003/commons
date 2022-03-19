package io.github.h800572003.check;

import java.util.function.Consumer;

import lombok.extern.slf4j.Slf4j;

/**
 * 驗證
 * 
 * @author 6407
 *
 */
@Slf4j
public class ValidationService implements IValidationService {
	protected static final Consumer<CheckResultsContext> NO_WOKR_HANDLER = resultsContext -> {
		log.info("CheckResultsContext {}", resultsContext);
	};
	protected final Consumer<CheckResultsContext> commonHandler;

	public ValidationService(Consumer<CheckResultsContext> commonHandler) {
		this.commonHandler = commonHandler;
	}

	public ValidationService() {
		this(NO_WOKR_HANDLER);
	}

	@Override
	public <T> boolean execute(T src, ValidationStrategy<T> validationStrategy) {
		return validationStrategy.execeute(src, commonHandler);
	}

}
