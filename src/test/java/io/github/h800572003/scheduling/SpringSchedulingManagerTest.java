package io.github.h800572003.scheduling;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;

import io.github.h800572003.exception.ApBusinessExecpetion;
import io.github.h800572003.exception.CancelExecpetion;
import io.github.h800572003.scheduling.SpringSchedulingManager.ISpringSchedulingProperites;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Disabled
class SpringSchedulingManagerTest {

	private final ISchedulingRepository schedulingRepository = Mockito.mock(ISchedulingRepository.class);
	private final ApplicationContext applicationContext = Mockito.mock(ApplicationContext.class);
	private final MyScheduingMonitors myScheduingMonitors = Mockito.spy(new MyScheduingMonitors());
	private final ISpringSchedulingProperites springSchedulingProperites = Mockito
			.mock(ISpringSchedulingProperites.class);

	ISchedulingManager springSchedulingManager = SchedulingManagers.createSchedulingManager(schedulingRepository,
			applicationContext, myScheduingMonitors, springSchedulingProperites);

	Sample sample = new Sample();
	Sample2 sample2 = new Sample2();
	Sample3 sample3 = new Sample3();
	private int closeTimeout = 60 * 3;

	SpringSchedulingManagerTest() {

		Mockito.when(springSchedulingProperites.isExecute()).thenReturn(true);
		this.setCloseTimeout(closeTimeout);
		Mockito.when(springSchedulingProperites.getDelayStart()).thenReturn(0);

		List<IScheduingCron> list = Lists.newArrayList();
		list.add(Sample.scheduingCron);
		list.add(Sample2.scheduingCron);
		list.add(Sample3.scheduingCron);

		Mockito.when(schedulingRepository.getCcheduingCronsTask()).thenReturn(list);

		Mockito.when(applicationContext.getBean(Sample.class)).thenReturn(sample);
		Mockito.when(applicationContext.getBean(Sample2.class)).thenReturn(sample2);
		Mockito.when(applicationContext.getBean(Sample3.class)).thenReturn(sample3);
	}

	@BeforeEach
	public void before() {
		runCmd("ON", this::up, 1);
	}

	/**
	 * 設置中斷時間
	 * 
	 * @param timeout
	 */
	private void setCloseTimeout(int timeout) {
		Mockito.when(springSchedulingProperites.getCloseTimeout()).thenReturn(timeout);
	}

	/**
	 * 測試 (停止有等待) 1.啟動 2.等1秒 3.停機
	 * 
	 */
	@Test
	void testDown() {

		List<CmdRunnable> cmdRunnables = new ArrayList<SpringSchedulingManagerTest.CmdRunnable>();
		cmdRunnables.add(new CmdRunnable("DOWN_THREAD", () -> springSchedulingManager.down(), 0, 0));
		runCmd(cmdRunnables);

		showMssage();
		assertThat(springSchedulingManager.getContext().getAll()).filteredOn(i -> i.getProgress() != 100).hasSize(0);

		log.info("end");
	}
	@Test
	void testWatch() {

		ISchedulingContext context = springSchedulingManager.getContext();
		showMssage();
		assertThat(springSchedulingManager.getContext().getAll()).filteredOn(i -> i.getProgress() != 100).hasSize(0);

		log.info("end");
	}

	/**
	 * 停止作業堵塞1秒，然後啟動，發生Exception
	 */
	@Test
	void testDowOverAndCallWhenRunnning() {

		ErrorIsOkUncaughtExceptionHandler errorIsOkUncaughtExceptionHandler = new ErrorIsOkUncaughtExceptionHandler();

		this.setCloseTimeout(1);

		List<CmdRunnable> cmdRunnables = new ArrayList<SpringSchedulingManagerTest.CmdRunnable>();
		cmdRunnables.add(new CmdRunnable("DOWN_THREAD", () -> springSchedulingManager.down(), 0, 0));

		CmdRunnable cmdRunnable = new CmdRunnable("UP_THREAD", () -> springSchedulingManager.up(), 0, 2);
		cmdRunnable.uncaughtExceptionHandler = errorIsOkUncaughtExceptionHandler;
		cmdRunnables.add(cmdRunnable);
		runCmd(cmdRunnables);

		showMssage();
		assertThat(springSchedulingManager.getContext().getAll()).filteredOn(i -> i.getProgress() != 100)
				.hasSizeGreaterThan(0);
		assertThat(errorIsOkUncaughtExceptionHandler.messages).contains("任務尚未完全停止，請稍後");
		log.info("end");
	}

	private void showMssage() {
		springSchedulingManager.getContext().getAll().stream().forEach(i -> System.out
				.printf("code:%s,progress:%d,status:%s \n", i.getCode(), i.getProgress(), i.getStatus()));
	}

	private void up() {
		springSchedulingManager.propertiesChange();
		springSchedulingManager.startAll();
	}

	/**
	 * 測試 cacnel 不會堵塞，因此至少sample1狀態為100
	 * 
	 * 
	 *
	 */
	@Test
	// @Timeout(unit = TimeUnit.SECONDS, value = 20)
	void testStartIntrupt() {

		List<CmdRunnable> cmdRunnables = new ArrayList<SpringSchedulingManagerTest.CmdRunnable>();
		cmdRunnables.add(new CmdRunnable("CNACEL_1", () -> springSchedulingManager.cancelAll(), 1, 0));
		cmdRunnables.add(new CmdRunnable("CNACEL_2", () -> springSchedulingManager.cancelAll(), 1, 0));

		runCmd(cmdRunnables);
		showMssage();

		Optional<ISchedulingItemContext> findAny = springSchedulingManager.getContext().getAll().stream()
				.filter(i -> i.getCode().equals(Sample.class.getSimpleName())).findAny();

		assertThat(findAny.get().getProgress()).isEqualTo(100);

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
	 * 重複呼叫啟動，檢查兩次中斷紀錄 1.啟動服務 2.連續啟動同樣服務 3.兩個都會發生錯誤 4.取得兩次錯誤紀錄
	 */
	@Test
	// @Timeout(unit = TimeUnit.SECONDS, value = 15)
	void testStartStart() {

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
	// @Timeout(unit = TimeUnit.SECONDS, value = 15)
	void testOnRresh() {

		assertThatExceptionOfType(ApBusinessExecpetion.class).isThrownBy(() -> {
			this.springSchedulingManager.refresh(Sample.class.getSimpleName());
		}).withMessageContaining("請先關閉");
		// assertThat(springSchedulingManager.getContext().getRunningCount()).isEqualTo(2);
		log.info("end");
	}

	/**
	 * 排程 + runOnce同時呼叫，計數表示僅同一功能進行執行
	 */
	@Test
	// @Timeout(unit = TimeUnit.SECONDS, value = 15)
	void testRunOnce() {

		this.runCmd("RUNONCE_1", () -> springSchedulingManager.runOnce(Sample.class.getSimpleName()), 5);
		this.runCmd("RUNONCE_2", () -> springSchedulingManager.runOnce(Sample.class.getSimpleName()));

		this.showMssage();
		assertThat(Sample.atomicInteger.get()).isEqualTo(1);

		log.info("end");
	}

	class CmdRunnable {
		String cmd;
		Runnable runnable;
		UncaughtExceptionHandler uncaughtExceptionHandler;
		int wait = 0;

		public CmdRunnable(String cmd, Runnable runnable, int wait, int delayStart) {
			super();
			this.cmd = cmd;
			this.runnable = new DelayRunnable(runnable, delayStart);
			this.wait = wait;
		}

		class DelayRunnable implements Runnable {

			private Runnable runnable;
			private int delayStart = 0;

			public DelayRunnable(Runnable runnable, int delayStart) {
				super();
				this.runnable = runnable;
				this.delayStart = delayStart;
			}

			@Override
			public void run() {
				if (delayStart > 0) {
					log.info("delay start...{}s", delayStart);
					try {
						TimeUnit.SECONDS.sleep(delayStart);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}
				runnable.run();
			}

		}

		public CmdRunnable(String cmd, Runnable runnable) {
			this(cmd, runnable, 0, 0);
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

	void runCmd(CmdRunnable cmd) {
		this.runCmd(Arrays.asList(cmd));
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
				log.info("#### 呼叫" + cmdRunnable.cmd + " ####");
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
		this.runCmd(Lists.list(new CmdRunnable(cmd, runnable, wait, 0)));
	}

	void runCmd(String cmd, Runnable runnable, int wait, int delay) {
		this.runCmd(Lists.list(new CmdRunnable(cmd, runnable, wait, delay)));
	}

	void runCmd(String cmd, Runnable runnable) {
		this.runCmd(Lists.list(new CmdRunnable(cmd, runnable)));
	}

}
