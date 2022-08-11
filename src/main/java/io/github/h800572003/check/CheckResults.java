package io.github.h800572003.check;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import io.github.h800572003.exception.ApBusinessException;

public class CheckResults implements CheckResultsContext {
	private final List<CheckResult> checkStatuses = Lists.newArrayList();
	private final Consumer<CheckResultsContext> handle;
	private final Object src;

	public CheckResults(Consumer<CheckResultsContext> consumer, Object src) {
		super();
		this.handle = consumer;
		this.src = src;
	}

	@Override
	public void isOk(Consumer<List<CheckResult>> consumer) {
		final List<CheckResult> okList = this.checkStatuses.stream()//
				.filter(CheckResult::isOk)//
				.collect(Collectors.toList());//
		consumer.accept(this.isAllOk() ? okList : Lists.newArrayList());
	}

	protected void add(CheckResult checkStatus) {
		this.checkStatuses.add(checkStatus);
	}

	/**
	 * 當異常時中斷
	 * 
	 * @param <T>
	 * @param functoin
	 */
	@Override
	public <T extends RuntimeException> void ifError(Function<CheckResult, T> functoin) {
		final Optional<CheckResult> findFirst = this.checkStatuses.stream()//
				.filter(CheckResult::isError)//
				.findFirst();//
		if (findFirst.isPresent()) {
			throw functoin.apply(findFirst.get());
		}
	}

	/**
	 * 是否全部ok
	 * 
	 * @return
	 */
	@Override
	public boolean isAllOk() {
		Long okCount = checkStatuses.stream()///
				.filter(CheckResult::isOk)//
				.collect(Collectors.counting());//
		return okCount == checkStatuses.size();
	}

	@Override
	public boolean isAllError() {
		Long errorCount = checkStatuses.stream()//
				.filter(CheckResult::isError)//
				.collect(Collectors.counting());//
		return errorCount == checkStatuses.size();
	}

	/**
	 * 當錯誤時，
	 * 
	 * @param consumer
	 *            異常清單
	 */
	@Override
	public void isError(Consumer<List<CheckResult>> consumer) {
		consumer.accept(this.getErrors());
	}

	/**
	 * 取得異常清單
	 * 
	 * @return 異常清單
	 */
	@Override
	public List<CheckResult> getErrors() {
		return this.checkStatuses.stream()//
				.filter(CheckResult::isError)//
				.collect(Collectors.toList());// ;
	}

	/**
	 * 取得取得正常清單
	 * 
	 * @return 正常清單
	 */
	@Override
	public List<CheckResult> getOks() {
		return this.checkStatuses.stream()//
				.filter(CheckResult::isOk)//
				.collect(Collectors.toList());// ;
	}

	/**
	 * 驗證處理
	 */
	@Override
	public void handle() {
		if (this.handle == null) {
			throw new ApBusinessException("無定義handle");
		}
		this.handle.accept(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getSource(Class<T> pClass) {
		return src != null ? (T) src : null;
	}
}
