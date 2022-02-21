package io.github.h800572003.check;

public interface ICheckService {

	/**
	 * 檢查
	 * @param dto
	 * @return
	 */
	CheckResult check(Object dto);

	/**
	 * 加入
	 * @param checkRolesBuilder
	 */
	void add(CheckRolesBuilder<?> checkRolesBuilder);

}
