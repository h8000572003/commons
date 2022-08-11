package io.github.h800572003.check;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class CheckRolesBuilder<T> {

	protected List<CheckStep> functions = new ArrayList<>();
	protected Class<T> checkMainClasss;
	protected Consumer<CheckResultsContext> check;
	protected Consumer<CheckResultsContext> commonHandler;

	public CheckRolesBuilder(Class<T> checkMain) {
		this.checkMainClasss = checkMain;
	}

	/**
	 * 異常繼續檢查
	 * 
	 * @param function
	 * @return
	 */
	public CheckRolesBuilder<T> continueNext(Function<T, CheckResult> function) {
		this.add(function, false);
		return this;
	}

	public CheckRolesBuilder<T> next(Function<T, CheckResult> function, boolean isBreak) {
		this.add(function, isBreak);
		return this;
	}

	/**
	 * 有異常即中斷檢查
	 * 
	 * @param function
	 * @return
	 */
	public CheckRolesBuilder<T> breakNext(Function<T, CheckResult> function) {
		this.add(function, true);
		return this;
	}

	@SuppressWarnings({ "unchecked" })
	private CheckRolesBuilder<T> add(Function<T, CheckResult> function, boolean isBreak) {
		this.functions.add(new CheckStep((Function<Object, CheckResult>) function, isBreak));
		return this;
	}

	protected CheckHolder createCheckRegister(Consumer<CheckResultsContext> commonHandler) {
		this.commonHandler = commonHandler;
		return new CheckHolder(this);
	}

	public void setErrorHandle(Consumer<CheckResultsContext> check) {
		this.check = check;
	}

}
