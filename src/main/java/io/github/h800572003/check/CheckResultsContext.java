package io.github.h800572003.check;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 驗證結果上下文
 * @author andy
 *
 */
public interface CheckResultsContext {

	/**
	 * 資料來源
	 * 
	 * @param <T>
	 * @return
	 */
	<T> T getSource(Class<T> pClass);

	/**
	 * 驗證處理，呼叫handle
	 * 
	 */
	void handle();

	/**
	 * 取得取得正常清單
	 * 
	 * @return 正常清單
	 */

	/**
	 * 取得異常清單
	 * 
	 * @return 異常清單
	 */
	List<CheckResult> getErrors();

	/**
	 * 當錯誤時，
	 * 
	 * @param consumer
	 *            異常清單
	 */
	void isError(Consumer<List<CheckResult>> consumer);

	/**
	 * 是否全部錯誤
	 * 
	 * @return
	 */
	boolean isAllError();

	/**
	 * 是否全部ok
	 * 
	 * @return
	 */
	boolean isAllOk();

	/**
	 * 是OK時
	 * 
	 * @param consumer
	 */
	void isOk(Consumer<List<CheckResult>> consumer);

	/**
	 * 取得取得正常清單
	 * 
	 * @return 正常清單
	 */
	List<CheckResult> getOks();

	/**
	 * 當異常時中斷
	 * 
	 * @param <T>
	 * @param functoin
	 */
	<T extends RuntimeException> void ifError(Function<CheckResult, T> functoin);
}
