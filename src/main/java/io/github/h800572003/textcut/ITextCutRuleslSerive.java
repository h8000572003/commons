package io.github.h800572003.textcut;

import java.util.List;
import java.util.function.Predicate;

/**
 * 文字解析規則服務
 * 
 * @author 6407
 *
 * @param <T>
 */
public interface ITextCutRuleslSerive<T> {

	/**
	 * 加入規則
	 * 
	 * @param line
	 * @param config
	 */
	void addRule(Predicate<String> line, TextCutRoleConfig<T> config);

	/**
	 * 轉換
	 * 
	 * @param lines
	 * @return
	 */
	List<T> to(List<String> lines);

}