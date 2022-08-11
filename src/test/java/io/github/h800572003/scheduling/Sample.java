package io.github.h800572003.scheduling;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import io.github.h800572003.exception.CancelExecpetion;
import io.github.h800572003.scheduling.IScheduingTask;
import io.github.h800572003.scheduling.IScheduingTaskContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Sample implements IScheduingTask {

	final static AtomicInteger atomicInteger = new AtomicInteger(0);

	@Override
	public void execute(IScheduingTaskContext scheduingTaskContext) {

		try {
			atomicInteger.incrementAndGet();
			for (int i = 0; i < 10; i++) {
				try {
					scheduingTaskContext.updateMessage("INDEX:" + i);
					scheduingTaskContext.checkUp();
					scheduingTaskContext.setProgress(0);
					doSomeThing();

					scheduingTaskContext.setProgress(100);

				} catch (CancelExecpetion e) {
					log.info("中斷程式碼清單", e);
					break;
				}
			}
		} finally {
			atomicInteger.decrementAndGet();
		}

		log.info("end job");

	}

	private void doSomeThing() {

		try {
			TimeUnit.SECONDS.sleep(10);
		} catch (InterruptedException e) {
//			e.printStackTrace();
		}

	}

	static IScheduingCron scheduingCron = new IScheduingCron() {

		@Override
		public boolean isActived() {
			return true;
		}

		@Override
		public Class<? extends IScheduingTask> getPClass() {
			return Sample.class;
		}

		@Override
		public String getName() {
			return Sample.class.getSimpleName();
		}

		@Override
		public String getCode() {
			return Sample.class.getSimpleName();
		}

		@Override
		public String getCon() {
			return String.valueOf(3000l);
		}
	};
}
