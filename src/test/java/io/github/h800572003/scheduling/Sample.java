package io.github.h800572003.scheduling;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
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
					log.info("do task...{}", i + 1);
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
		}finally {
			atomicInteger.decrementAndGet();
		}
	
	
		log.info("end job");

	}

	private void doSomeThing(int index) {

		try {
//			log.info("index " + index + " start");
			for (int i = 0; i < 100; i++) {
				double nextDouble = nextDouble();

				// System.out.println("i:" + i + " ,value:" + nextDouble);
				// log.info("random:{}", nextDouble);
			}
		} finally {
//			log.info("index " + index + " done");
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

}
