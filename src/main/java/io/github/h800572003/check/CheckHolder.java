package io.github.h800572003.check;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CheckHolder {

	protected List<CheckStep> functions = new ArrayList<>();
	protected Consumer<List<CheckResult>> handle;// 客製化
	protected Consumer<List<CheckResult>> commonHandle;// 通用

	public CheckHolder(CheckRolesBuilder<?> builder) {
		this.functions = builder.functions;
		this.handle = builder.check;
	}

	public Consumer<List<CheckResult>> getHandle() {
		return this.handle;
	}

	public List<CheckStep> getFunctions() {
		return this.functions;
	}

	public Consumer<List<CheckResult>> getCheck() {
		if (handle != null) {
			return this.handle;
		}
		return this.commonHandle;

	}

	public CheckResults getCheckResults(Object dto) {
		Consumer<List<CheckResult>> mergerHandler = this.getHandle();
		final CheckResults checkResult = new CheckResults(mergerHandler);
		for (final CheckStep holder : this.functions) {
			final CheckResult result = holder.check(dto);
			checkResult.add(result);
			if (result.isError() && holder.isBreak()) {
				break;
			}
		}
		return checkResult;
	}

}
