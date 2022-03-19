package io.github.h800572003.check;

import java.util.Map;
import java.util.function.Consumer;

import com.google.common.collect.Maps;

import io.github.h800572003.exception.ApBusinessException;

public class CheckService implements ICheckService {

	private final Map<Class<?>, CheckHolder> checkHolderMap = Maps.newConcurrentMap();
	private final Consumer<CheckResultsContext> commonHandler;// 通用處理

	public CheckService(Consumer<CheckResultsContext> commonHandler) {
		super();
		this.commonHandler = commonHandler;
	}

	@Override
	public CheckResultsContext check(Object dto) {
		final CheckHolder checkHolder = this.checkHolderMap.get(dto.getClass());
		if (checkHolder == null) {
			throw new ApBusinessException("資料無提供驗證規則:{0}", dto.getClass());
		}
		return checkHolder.getCheckResults(dto);

	}

	@Override
	public void add(CheckRolesBuilder<?> checkRolesBuilder) {
		this.checkHolderMap.put(checkRolesBuilder.checkMainClasss,
				checkRolesBuilder.createCheckRegister(this.commonHandler));

		for (Class<?> pClass : checkRolesBuilder.checkMainClasss.getInterfaces()) {
			this.checkHolderMap.put(pClass, checkRolesBuilder.createCheckRegister(this.commonHandler));
		}

	}

	@Override
	public void handleError(Object dto) {
		this.check(dto).handle();
	}

}
