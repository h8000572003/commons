package io.github.h800572003.scheduling;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;

import io.github.h800572003.exception.ApBusinessExecpetion;
import io.github.h800572003.exception.CancelExecpetion;
import io.github.h800572003.scheduling.SpringSchedulingManager.SpringSchedulingProperites;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Disabled
class SpringSchedulingManagerTest {

	private final ISchedulingRepository schedulingRepository = Mockito.mock(ISchedulingRepository.class);
	private final ApplicationContext applicationContext = Mockito.mock(ApplicationContext.class);
	private final MyScheduingMonitors myScheduingMonitors = Mockito.spy(new MyScheduingMonitors());
	private final SpringSchedulingProperites springSchedulingProperites = Mockito
			.mock(SpringSchedulingProperites.class);

	SpringSchedulingManager springSchedulingManager = new SpringSchedulingManager(schedulingRepository,
			applicationContext, myScheduingMonitors, springSchedulingProperites);

	Sample sample = new Sample();
	Sample2 sample2 = new Sample2();

	SpringSchedulingManagerTest() {
		Mockito.when(springSchedulingProperites.isExecute()).thenReturn(true);
		Mockito.when(springSchedulingProperites.getCloseTimeout()).thenReturn(30);

		IScheduingCron scheduingCron = new IScheduingCron() {

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
		IScheduingCron scheduingCron2 = new IScheduingCron() {

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
		List<IScheduingCron> list = Lists.newArrayList();
		list.add(scheduingCron);
		list.add(scheduingCron2);

		Mockito.when(schedulingRepository.getCcheduingCronsTask()).thenReturn(list);

		Mockito.when(applicationContext.getBean(Sample.class)).thenReturn(sample);
		Mockito.when(applicationContext.getBean(Sample2.class)).thenReturn(sample2);
	}

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

	}

	/**
	 * 測試 1.啟動 2.等12秒 3.停機
	 * 
	 */
	@Test
	void testDown() {
		springSchedulingManager.propertiesChange();
		
		runCmd("ON", ()->springSchedulingManager.startAll(), 12);

		this.runCmd("DOWN_THREAD", () -> springSchedulingManager.down(), 5);
		// this.runCmd("UP_THREAD", () -> springSchedulingManager.up());
		assertThat(springSchedulingManager.getTasks()).filteredOn(i -> i.getProgress() != 100).hasSize(0);
		log.info("end");
	}

	/**
	 * 測試 1.啟動 2.等12秒 3.反覆操作(cancel、start)
	 *
	 */
	@Test
//	@Timeout(unit = TimeUnit.SECONDS, value = 20)
	void testStartIntrupt() {
		springSchedulingManager.propertiesChange();
		runCmd("ON", ()->springSchedulingManager.startAll(), 12);
		List<CmdRunnable> cmdRunnables = new ArrayList<SpringSchedulingManagerTest.CmdRunnable>();
		cmdRunnables.add(new CmdRunnable("CNACEL_1", () -> springSchedulingManager.cancelAll(),4));
		cmdRunnables.add(new CmdRunnable("CNACEL_2", () -> springSchedulingManager.cancelAll(),4));

		runCmd(cmdRunnables);
		assertThat(springSchedulingManager.getContext().getRunningCount()).isEqualTo(0);
		log.info("end");
	}

	class ErrorIsOkUncaughtExceptionHandler implements UncaughtExceptionHandler {
		boolean withException = false;

		List<String> messages = new ArrayList<String>();

		@Override
		public void uncaughtException(Thread t, Throwable e) {
			messages.add(e.getMessage());
			log.info("get execption thread:{}", t, e);
			withException = true;

		}

	}

	/**
	 * 1.啟動服務 2.連續啟動同樣服務 3.兩個都會發生錯誤 4.取得兩次錯誤紀錄
	 */
	@Test
//	@Timeout(unit = TimeUnit.SECONDS, value = 15)
	void testStartStart() {
		springSchedulingManager.propertiesChange();
		runCmd("ON", ()->springSchedulingManager.startAll(), 12);

		ErrorIsOkUncaughtExceptionHandler errorIsOkUncaughtExceptionHandler = new ErrorIsOkUncaughtExceptionHandler();

		List<CmdRunnable> cmdRunnables = new ArrayList<SpringSchedulingManagerTest.CmdRunnable>();
		CmdRunnable cmdRunnable1 = new CmdRunnable("START_1",
				() -> springSchedulingManager.start(Sample.class.getSimpleName()));
		cmdRunnable1.uncaughtExceptionHandler = errorIsOkUncaughtExceptionHandler;
		CmdRunnable cmdRunnable2 = new CmdRunnable("START_2",
				() -> springSchedulingManager.start(Sample.class.getSimpleName()));
		cmdRunnable2.uncaughtExceptionHandler = errorIsOkUncaughtExceptionHandler;
		cmdRunnables.add(cmdRunnable1);
		cmdRunnables.add(cmdRunnable2);

		runCmd(cmdRunnables);

		assertThat(errorIsOkUncaughtExceptionHandler.withException).isTrue();
		assertThat(errorIsOkUncaughtExceptionHandler.messages).hasSize(2);
		assertThat(errorIsOkUncaughtExceptionHandler.messages).matches(i -> StringUtils.contains(i.get(0), "已啟動，請先關閉"));
		log.info("end");
	}

	/**
	 * 1.啟動服務 2.重新整理該排成 3.get Exception 請先關閉作業
	 */
	@Test
//	@Timeout(unit = TimeUnit.SECONDS, value = 15)
	void testOnRresh() {
		springSchedulingManager.propertiesChange();
		runCmd("ON", ()->springSchedulingManager.startAll(), 12);
		assertThatExceptionOfType(ApBusinessExecpetion.class).isThrownBy(() -> {
			this.springSchedulingManager.refresh(Sample.class.getSimpleName());
		}).withMessageContaining("請先關閉");
//		assertThat(springSchedulingManager.getContext().getRunningCount()).isEqualTo(2);
		log.info("end");
	}

	/**
	 * 1.啟動服務 2.呼叫中斷 3.檢查執行中作業=0
	 */
	@Test
//	@Timeout(unit = TimeUnit.SECONDS, value = 15)
	void testCancel() {
		springSchedulingManager.propertiesChange();
		runCmd("ON", ()->springSchedulingManager.startAll(), 12);

		this.runCmd("CNACEL_1", () -> springSchedulingManager.cancelAll(), 2);

		while (springSchedulingManager.getContext().getRunningCount() != 0) {
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		log.info("end");

		List<ISchedulingItemContext> tasks = springSchedulingManager.getTasks();
		for (ISchedulingItemContext task : tasks) {
			log.info("task:{}", ToStringBuilder.reflectionToString(task));
		}

		assertThat(springSchedulingManager.getTasks()).filteredOn(i -> i.getProgress() != 100).hasSize(0);
	}

	/**
	 * 排程 + runOnce同時呼叫，計數表示僅同一功能進行執行
	 */
	@Test
	// @Timeout(unit = TimeUnit.SECONDS, value = 15)
	void testRunOnce() {
		springSchedulingManager.propertiesChange();
		springSchedulingManager.startAll();
		log.info("倒數執行");
		try {
			TimeUnit.SECONDS.sleep(12);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		this.runCmd("RUNONCE_1", () -> springSchedulingManager.runOnce(Sample.class.getSimpleName()), 5);
		this.runCmd("RUNONCE_2", () -> springSchedulingManager.runOnce(Sample.class.getSimpleName()));

		assertThat(Sample.atomicInteger.get()).isEqualTo(1);

		log.info("end");
	}

	class CmdRunnable {
		String cmd;
		Runnable runnable;
		UncaughtExceptionHandler uncaughtExceptionHandler;
		int wait = 0;

		public CmdRunnable(String cmd, Runnable runnable, int wait) {
			super();
			this.cmd = cmd;
			this.runnable = runnable;
			this.wait = wait;
		}

		public CmdRunnable(String cmd, Runnable runnable) {
			super();
			this.cmd = cmd;
			this.runnable = runnable;
		}

		public int getWait() {
			return wait;
		}

		void sleep() {
			if (wait > 0) {
				try {
					TimeUnit.SECONDS.sleep(wait);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	void runCmd(List<CmdRunnable> cmds) {

		List<Thread> collect = cmds.stream().map(this::thread).collect(Collectors.toList());
		collect.forEach(Thread::start);
		collect.forEach(t -> {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		Optional<CmdRunnable> findFirst = cmds.stream()
				.sorted(Comparator.comparing(new Function<CmdRunnable, Integer>() {
					@Override
					public Integer apply(CmdRunnable t) {
						return t.getWait();
					}
				})).findFirst();
		CmdRunnable cmdRunnable = findFirst.get();
		cmdRunnable.sleep();
	}

	Thread thread(CmdRunnable cmdRunnable) {
		Runnable warp = new Runnable() {

			@Override
			public void run() {
				log.info("#### 呼叫" + cmdRunnable.cmd);
				cmdRunnable.runnable.run();
			}
		};
		Thread thread = new Thread(warp, cmdRunnable.cmd);
		if (cmdRunnable.uncaughtExceptionHandler != null) {
			thread.setUncaughtExceptionHandler(cmdRunnable.uncaughtExceptionHandler);
		}
		return thread;
	}

	void runCmd(String cmd, Runnable runnable, int wait) {
		this.runCmd(Lists.list(new CmdRunnable(cmd, runnable, wait)));
	}

	void runCmd(String cmd, Runnable runnable) {
		this.runCmd(Lists.list(new CmdRunnable(cmd, runnable)));
	}

}
