package io.github.h800572003.ibatis;

import org.apache.ibatis.session.SqlSessionFactory;

public class BatisServiceFactory {

	private final SqlSessionFactory sqlSessionFactory;

	public BatisServiceFactory(SqlSessionFactory sqlSessionFactory) {
		super();
		this.sqlSessionFactory = sqlSessionFactory;
	}

	/**
	 * 建立batch helper
	 * 
	 * @param <T> 處理物件泛型
	 * @param pClass 處理物件
	 * @return
	 */
	public <T> IBatisHelplerService<T> createBatchHelper(Class<T> pClass) {
		return new MyBatisBatchHelperService<T>(sqlSessionFactory);

	}
}
