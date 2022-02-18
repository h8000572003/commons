package io.github.h800572003.ibatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.ibatis.session.SqlSession;

import com.google.common.collect.Lists;

public interface IBatisHelplerService<T> {

	public class BatchContext<T> {
		private SqlSession openSession;
		private T data;
		private Map<String, Object> cacheMap = new HashMap<String, Object>();

		public BatchContext(SqlSession openSession, T data) {
			super();
			this.openSession = openSession;
			this.data = data;
		}

		public SqlSession getOpenSession() {
			return openSession;
		}

		public T getData() {
			return data;
		}

		public void put(String key, Object value) {
			this.cacheMap.put(key, value);
		}

		public Optional<Object> getValue(String key) {
			boolean containsKey = cacheMap.containsKey(key);
			if (containsKey) {
				return Optional.empty();
			}
			Object object = cacheMap.get(key);
			return Optional.ofNullable(object);
		}
	}

	/**
	 * 
	 * @param batchSize
	 * @param datas
	 * @param runnable
	 * @param isErrorBreak
	 * @return
	 */
	BatchResult<T> batchExecute(int batchSize, List<T> datas, Consumer<BatchContext<T>> runnable, boolean isErrorBreak);

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
			doneList.forEach(i -> this.doneList.add(i));
		}

	}

	public class BatchErrorGroup<T> {
		private Throwable throwable;
		private List<T> src = Lists.newArrayList();

		public BatchErrorGroup(Throwable throwable, List<T> src) {
			super();
			this.throwable = throwable;
			src.forEach(i -> this.src.add(i));

		}

		public Throwable getThrowable() {
			return throwable;
		}

		public List<T> getSrc() {
			return src;
		}

	}

	/**
	 * batch 資料所有都進去，發生錯誤保存異常
	 *
	 * @param batchSize
	 * @param datas
	 * @param runnable
	 */
	default BatchResult<T> batchExecute(int batchSize, List<T> datas, Consumer<BatchContext<T>> runnable) {
		return this.batchExecute(batchSize, datas, runnable, false);
	}
}
