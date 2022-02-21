package io.github.h800572003.check;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class CheckRolesBuilder<T> {

	protected List<CheckHolder> functions = new ArrayList<CheckHolder>();
	protected Class<T> checkMainClasss;

	public CheckRolesBuilder(Class<T> checkMain) {
		this.checkMainClasss = checkMain;
	}

	/**
	 * 異常繼續檢查
	 * 
	 * @param function
	 * @return
	 */
	public CheckRolesBuilder<T> continueNext(Function<T, CheckRule> function) {
		this.add(function, false);
		return this;
	}

	/**
	 * 有異常即中斷檢查
	 * 
	 * @param function
	 * @return
	 */
	public CheckRolesBuilder<T> breakNext(Function<T, CheckRule> function) {
		this.add(function, true);
		return this;
	}

	@SuppressWarnings({ "unchecked" })
	private CheckRolesBuilder<T> add(Function<T, CheckRule> function, boolean isBreak) {
		functions.add(new CheckHolder((Function<Object, CheckRule>) function, isBreak));
		return this;
	}

}
