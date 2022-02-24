package io.github.h800572003.check;

import java.util.function.Function;

/**
 * 規則
 * 
 * @author Andy
 *
 */
public class CheckHolder {
	Function<Object, CheckResult> function;
	boolean isBreak;

	public CheckHolder(Function<Object, CheckResult> function, boolean isBreak) {
		super();
		this.function = function;
		this.isBreak = isBreak;
	}

	CheckResult check(Object dto, String defCode) {
		return function.apply(dto);
	}

	public boolean isBreak() {
		return isBreak;
	}

}
