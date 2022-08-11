package io.github.h800572003.check;

import java.util.function.Function;

/**
 * 規則
 * 
 * @author Andy
 *
 */
public class CheckStep {
	Function<Object, CheckResult> function;
	boolean isBreak;

	public CheckStep(Function<Object, CheckResult> function, boolean isBreak) {
		super();
		this.function = function;
		this.isBreak = isBreak;
	}

	CheckResult check(Object dto) {
		return this.function.apply(dto);
	}

	public boolean isBreak() {
		return this.isBreak;
	}

}
