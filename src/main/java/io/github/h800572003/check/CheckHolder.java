package io.github.h800572003.check;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CheckHolder {

	protected List<CheckStep> functions = new ArrayList<>();
	protected Consumer<CheckResultsContext> handle;// 客製化
	protected Consumer<CheckResultsContext> commonHandle;// 通用

	public CheckHolder(CheckRolesBuilder<?> builder) {
		this.functions = builder.functions;
		this.handle = builder.check;
		this.commonHandle = builder.commonHandler;
	}

	public Consumer<CheckResultsContext> getHandle() {
		if (this.handle != null) {
			return this.handle;
		} else {
			return this.commonHandle;
		}
	}

	public List<CheckStep> getFunctions() {
		return this.functions;
	}

	public Consumer<CheckResultsContext> getCheck() {
		if (handle != null) {
			return this.handle;
		}
		return this.commonHandle;

	}

	public CheckResultsContext getCheckResults(Object dto) {
		Consumer<CheckResultsContext> mergerHandler = this.getHandle();
		final CheckResults checkResult = new CheckResults(mergerHandler, dto);
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
