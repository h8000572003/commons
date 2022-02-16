package io.github.h800572003.ibatis;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.google.common.collect.Lists;

public interface IBatisHelplerService<T> {
	@FunctionalInterface
	interface BatchRunnable<T> {
		public void update(SqlSession openSession, T data);
	}
	

	/**
	 * 
	 * @param batchSize
	 * @param datas
	 * @param runnable
	 * @param isErrorBreak
	 * @return
	 */
	BatchResult<T> batchExecute(int batchSize, List<T> datas, BatchRunnable<T> runnable, boolean isErrorBreak);

	public class BatchResult<T> {
		List<BatchDoneGroup<T>> doneGroups = Lists.newArrayList();
		List<BatchErrorGroup<T>> batchErrorGroups = Lists.newArrayList();

		/**
		 * 成功群組
		 * 
		 * @return
		 */
		public List<BatchDoneGroup<T>> getDoneGroups() {
			return doneGroups;
		}

		/**
		 * 錯誤群組
		 * 
		 * @return
		 */
		public List<BatchErrorGroup<T>> getBatchErrorGroups() {
			return batchErrorGroups;
		}

	}

	public class BatchDoneGroup<T> {
		List<T> doneList = Lists.newArrayList();

		public BatchDoneGroup(List<T> doneList) {
			super();
			this.doneList = doneList;
		}

	}

	public class BatchErrorGroup<T> {
		Throwable throwable;
		List<T> src;

		public BatchErrorGroup(Throwable throwable, List<T> src) {
			super();
			this.throwable = throwable;
			this.src = src;
		}

	}

	/**
	 * batch 資料所有都進去，發生錯誤保存異常
	 *
	 * @param batchSize
	 * @param datas
	 * @param runnable
	 */
	default BatchResult<T> batchExecute(int batchSize, List<T> datas, BatchRunnable<T> runnable) {
		return this.batchExecute(batchSize, datas, runnable, false);
	}
}
