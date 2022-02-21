package io.github.h800572003.check;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

public class CheckResult {
	private List<CheckRule> checkStatuses = Lists.newArrayList();

	public boolean isOk() {
		boolean isOk = !checkStatuses.stream().filter(CheckRule::isError).findAny().isPresent();
		return isOk;
	}

	public void isOk(Consumer<List<CheckRule>> consumer) {
		List<CheckRule> errors = checkStatuses.stream().filter(CheckRule::isOk).collect(Collectors.toList());
		consumer.accept(errors);
	}

	public void add(CheckRule checkStatus) {
		checkStatuses.add(checkStatus);
	}

	public boolean isError() {
		return !this.isOk();
	}

	public void isError(Consumer<List<CheckRule>> consumer) {
		List<CheckRule> errors = checkStatuses.stream().filter(CheckRule::isError).collect(Collectors.toList());
		consumer.accept(errors);
	}
}
