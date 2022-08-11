package io.github.h800572003.ibatis;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import io.github.h800572003.exception.ApBusinessException;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

/**
 * Bacth Service
 * 
 * @author Andy
 *
 * @param <T>
 *            修改資料
 * 
 */
public class MyBatisBatchHelperService<T> implements IBatisHelplerService<T> {

	public final SqlSessionFactory sqlSessionFactory;

	public MyBatisBatchHelperService(SqlSessionFactory sqlSessionFactory) {
		super();
		this.sqlSessionFactory = sqlSessionFactory;
	}

	@Override
	public BatchResult<T> batchExecute(int batchSize, List<T> datas, Consumer<BatchContext<T>> runnable,
			boolean isErrorBreak) {
		final BatchResult<T> context = new BatchResult<>();
		try (final SqlSession openSession = this.sqlSessionFactory.openSession(ExecutorType.BATCH, false)) {
			final List<T> executes = new ArrayList<>();
			for (T data : datas) {
				BatchContext<T> batchContext = new BatchContext<>(openSession, data);
				runnable.accept(batchContext);
				executes.add(data);
				if (executes.size() == batchSize) {
					this.commit(executes, openSession, isErrorBreak, context);
				}
			}
			this.commit(executes, openSession, isErrorBreak, context);
		}
		return context;

	}

	private void commit(final List<T> executes, final SqlSession openSession, boolean isErrorBreak,
			BatchResult<T> context) {
		try {
			openSession.commit();
			context.doneGroups.add(new BatchDoneGroup<>(executes));
		} catch (Exception e) {
			context.batchErrorGroups.add(new BatchErrorGroup<>(e, executes));
			openSession.rollback();
			if (isErrorBreak) {
				throw new ApBusinessException("batch error SqlSessionException", e);
			}
		} finally {
			openSession.clearCache();
			executes.clear();
		}
	}

}
