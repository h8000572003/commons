package io.github.h800572003.scheduling;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicInteger;

import io.github.h800572003.exception.CancelExecpetion;
import lombok.extern.slf4j.Slf4j;

/**
 * 停止時間長任務
 * 
 * @author 6407
 *
 */
@Slf4j
public class Sample3 implements IScheduingTask {

	@Override
	public void execute(IScheduingTaskContext scheduingTaskContext) {

		try {
			for (int i = 0; i < 10; i++) {
				try {
					scheduingTaskContext.updateMessage("INDEX:" + i);
					scheduingTaskContext.checkUp();
					log.info("do task...{}", i + 1);
					scheduingTaskContext.setProgress(0);
					doSomeThing(i);
					scheduingTaskContext.setProgress(20);
					doSomeThing(i);
					scheduingTaskContext.setProgress(40);

					doSomeThing(i);
					scheduingTaskContext.setProgress(60);

					doSomeThing(i);
					scheduingTaskContext.setProgress(80);

					doSomeThing(i);
					scheduingTaskContext.setProgress(100);

				} catch (CancelExecpetion e) {
					log.info("中斷程式碼清單", e);
					break;
				}
			}
		} finally {
		}

		log.info("end job");

	}

	private void doSomeThing(int index) {

		try {
			// log.info("index " + index + " start");
			for (int i = 0; i < 10000; i++) {
				double nextDouble = nextDouble();

				// System.out.println("i:" + i + " ,value:" + nextDouble);
				// log.info("random:{}", nextDouble);
			}
		} finally {
			// log.info("index " + index + " done");
		}

	}

	private double nextDouble() {
		try {
			SecureRandom instanceStrong = SecureRandom.getInstanceStrong();
			return instanceStrong.nextDouble();
		} catch (NoSuchAlgorithmException e) {
			log.info("演算法不存在不提供隨機數值");
			return 0;
		}
	}

	static IScheduingCron scheduingCron = new IScheduingCron() {

		@Override
		public boolean isActived() {
			return true;
		}

		@Override
		public Class<? extends IScheduingTask> getPClass() {
			return Sample3.class;
		}

		@Override
		public String getName() {
			return Sample3.class.getSimpleName();
		}

		@Override
		public String getCode() {
			return Sample3.class.getSimpleName();
		}

		@Override
		public String getCon() {
			return String.valueOf(3000l);
		}
	};

}
