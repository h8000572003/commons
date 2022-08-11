package io.github.h800572003.check;

public interface IValidationService {
	public <T> boolean execute(T src, ValidationStrategy<T> validationStrategy);
}
