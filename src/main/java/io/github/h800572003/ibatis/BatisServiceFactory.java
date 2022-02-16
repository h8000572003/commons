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
	 * @param <T>
	 * @param pClass
	 * @return
	 */
	public <T> IBatisHelplerService<T> createBatchHelper(Class<T> pClass) {
		return new MyBatisBatchHelperService<T>(sqlSessionFactory);

	}
}
