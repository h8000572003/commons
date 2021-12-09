package io.github.h800572003.scheduling;

import java.util.concurrent.TimeUnit;

import io.github.h800572003.exception.CancelExecpetion;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class Sample2 implements IScheduingTask {

	@Override
	public void execute(IScheduingTaskContext context) {
		try {
			while (true) {
				context.checkUp();
				context.setProgress(0);
				log.info("Sample run start");
				try {
					TimeUnit.SECONDS.sleep(10);
				} catch (InterruptedException e) {
					// 忽略
				}
				context.setProgress(100);
				log.info("Sample run end");

			}
		} catch (CancelExecpetion e) {
			log.info("離開作業");
		}

	}

	static IScheduingCron scheduingCron = new IScheduingCron() {

		@Override
		public boolean isActived() {
			return true;
		}

		@Override
		public Class<? extends IScheduingTask> getPClass() {
			return Sample2.class;
		}

		@Override
		public String getName() {
			return Sample2.class.getSimpleName();
		}

		@Override
		public String getCode() {
			return Sample2.class.getSimpleName();
		}

		@Override
		public String getCon() {
			return String.valueOf(3000l);
		}
	};
}
