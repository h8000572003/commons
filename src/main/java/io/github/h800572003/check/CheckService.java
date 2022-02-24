package io.github.h800572003.check;

import java.util.List;
import java.util.Map;

import org.springframework.util.CollectionUtils;

import com.google.common.collect.Maps;

import io.github.h800572003.exception.ApBusinessException;

public class CheckService implements ICheckService {

	private final Map<Class<?>, List<CheckHolder>> checkHolderMap = Maps.newConcurrentMap();
	private String defaultErrorCode = "XXX";

	@Override
	public CheckResults check(Object dto) {
		final List<CheckHolder> checkHolders = this.checkHolderMap.get(dto.getClass());
		if (CollectionUtils.isEmpty(checkHolders)) {
			this.notCheck(dto);
		}
		final CheckResults checkResult = new CheckResults();
		for (final CheckHolder holder : checkHolders) {
			final CheckResult check = holder.check(dto, defaultErrorCode);
			checkResult.add(check);
			if (check.isError() && holder.isBreak()) {
				break;
			}
		}
		return checkResult;
	}

	protected CheckResults notCheck(Object dto) {
		throw new ApBusinessException("資料無提供驗證規則:{0}", dto.getClass());
	}

	@Override
	public void add(CheckRolesBuilder<?> checkRolesBuilder) {
		this.checkHolderMap.put(checkRolesBuilder.checkMainClasss, checkRolesBuilder.functions);

	}

	public void setDefaultErrorCode(String defaultErrorCode) {
		this.defaultErrorCode = defaultErrorCode;
	}

}
