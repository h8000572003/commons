package io.github.h800572003.check;

public interface ICheckService {

	/**
	 * 檢查
	 * 
	 * @param dto
	 * @return
	 */
	CheckResultsContext check(Object dto);

	void handleError(Object dto);
	/**
	 * 加入
	 * 
	 * @param checkRolesBuilder
	 */
	void add(CheckRolesBuilder<?> checkRolesBuilder);

}
