package io.github.h800572003.scheduling;

/**
 * 任務KEY
 * 
 * @author 6407
 *
 */
public interface IScheduingKey {
	/**
	 * 作業代碼
	 * 
	 * @return
	 */
	public String getCode();

	Class<? extends IScheduingTask> getPClass();

	String getName();

	/**
	 * 是否啟用
	 * 
	 * @return
	 */
	boolean isActived();

}
