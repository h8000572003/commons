package io.github.h800572003.properties;

import java.util.List;

/**
 * 代碼
 * 
 * @author andy tsai
 *
 */
public interface IPropertiesService {

	/**
	 * 加入代碼
	 * 
	 * @param code
	 * @param name
	 * @param holder
	 */
	void addCode(String code, String name, IPropertieServiceHolder holder);

	/**
	 * 取得所有代碼
	 * 
	 * @return
	 */
	List<Code> getCategory();

	interface PropertiesUpdateListener {
		void update(String category, IProperties properties);
	}

	/**
	 * 代碼異動監聽
	 * 
	 * @param propertiesUpdateListener
	 */
	void addListener(PropertiesUpdateListener propertiesUpdateListener);

	/**
	 * 取得多筆
	 * 
	 * @param category
	 *            類別
	 * @return
	 */
	public List<IProperties> getPropertie(String category);

	/**
	 * 
	 * @param category
	 *            類別
	 * @param key
	 * @return
	 */
	public IProperties getValue(String category, String key);

	/**
	 * 更新
	 * 
	 * @param category
	 * @param properties
	 */
	public void saveOrUpdate(String category, IProperties properties);

	/**
	 * 清除該category
	 * 
	 * @param category
	 */
	public void clear(String category);

	/**
	 * 全部清除
	 */
	public void clear();

	/**
	 * 刪除該category下之code
	 * 
	 * @param category
	 * @param code
	 */
	public void delete(String category, String code);

	/**
	 * 清除該category
	 * 
	 * @param category
	 */
	public void delete(String category);

	/**
	 * 更新所有代碼檔案
	 */
	void update();
}
