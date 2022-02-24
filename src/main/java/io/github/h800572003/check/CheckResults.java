package io.github.h800572003.check;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

public class CheckResults {
	private final List<CheckResult> checkStatuses = Lists.newArrayList();

	public void isOk(Consumer<List<CheckResult>> consumer) {
		final List<CheckResult> okList = this.checkStatuses.stream()//
				.filter(CheckResult::isOk)//
				.collect(Collectors.toList());//
		consumer.accept(this.isOk()?okList:Lists.newArrayList());
	}

	protected void add(CheckResult checkStatus) {
		this.checkStatuses.add(checkStatus);
	}

	public boolean isOk() {
		final boolean isError = !this.checkStatuses.stream()//
				.filter(CheckResult::isOk)//
				.findAny().isPresent();//
		return !isError;
	}

	public boolean isError() {
		return !this.isOk();
	}

	/**
	 * 當錯誤時，
	 * 
	 * @param consumer
	 *            異常清單
	 */
	public void isError(Consumer<List<CheckResult>> consumer) {
		final List<CheckResult> errors = this.getErrors();
		consumer.accept(this.isError() ? errors : Lists.newArrayList());
	}

	/**
	 * 取得異常清單
	 * 
	 * @return 異常清單
	 */
	public List<CheckResult> getErrors() {
		final List<CheckResult> errors = this.checkStatuses.stream()//
				.filter(CheckResult::isError)//
				.collect(Collectors.toList());//
		return errors;
	}
}
