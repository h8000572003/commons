package io.github.h800572003.scheduling;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import io.github.h800572003.exception.ApBusinessException;
import io.github.h800572003.scheduling.SpringSchedulingManager.ISpringSchedulingProperites;
import io.github.h800572003.scheduling.SpringSchedulingManager.SpringSchedulingHook;
import io.github.h800572003.utils.HostNameUtls;

/**
 * SpringSchedulingManager BDD 測試
 * <p>
 * 以 Given / When / Then 描述每個 method 的行為。
 *
 * @author andy tsai
 */
@DisplayName("SpringSchedulingManager 排程管理")
@Timeout(value = 30, unit = TimeUnit.SECONDS)
class SpringSchedulingManagerBddTest {

	/** 一年一次，測試期間不會被觸發 */
	static final String NEVER = "0 0 0 1 1 ?";
	/** 數字表示 fixed delay(ms)，搭配 delayStart=0 會立即執行一次 */
	static final String IMMEDIATELY = "3600000";

	final ISchedulingRepository repository = mock(ISchedulingRepository.class);
	final ApplicationContext applicationContext = mock(ApplicationContext.class);
	final IScheduingMonitor monitor = mock(IScheduingMonitor.class);
	final SpringSchedulingHook hook = mock(SpringSchedulingHook.class);
	final FakeProperites properites = new FakeProperites();
	final FakeConfigRepository configRepository = new FakeConfigRepository();

	final TaskA taskA = new TaskA();
	final TaskB taskB = new TaskB();
	final TaskC taskC = new TaskC();

	final List<IScheduingCron> crons = new ArrayList<>();
	final List<IScheduingDelay> delays = new ArrayList<>();

	SpringSchedulingManager manager;

	@BeforeEach
	void setUp() {
		given(applicationContext.getBean(TaskA.class)).willReturn(taskA);
		given(applicationContext.getBean(TaskB.class)).willReturn(taskB);
		given(applicationContext.getBean(TaskC.class)).willReturn(taskC);
		given(repository.getCcheduingCronsTask()).willReturn(crons);
		given(repository.getDelayTask()).willReturn(delays);

		manager = new SpringSchedulingManager(repository, applicationContext, monitor, properites, configRepository);
		manager.setSpringSchedulingHook(hook);
	}

	@AfterEach
	void tearDown() {
		taskA.release();
		taskB.release();
		taskC.release();
		properites.closeTimeout = 1;
		if (manager.getContext().isStart()) {
			try {
				manager.down();
			} catch (RuntimeException e) {
				// 非執行者等情境 down 會失敗，直接關閉內部排程器
				destroyScheduler(manager);
			}
		}
	}

	// ========================================================================
	// add
	// ========================================================================
	@Nested
	@DisplayName("add(IScheduingCron) / add(IScheduingDelay) 新增任務")
	class Add {

		@Test
		@DisplayName("Given 已初始化 When 新增已存在代碼的 cron 任務 Then 拋出任務代碼重複")
		void givenExistsCode_whenAddCron_thenDuplicate() {
			crons.add(new FakeCron("A", TaskA.class, NEVER));
			manager.propertiesChange();

			assertThatThrownBy(() -> manager.add(new FakeCron("A", TaskB.class, NEVER)))
					.isInstanceOf(ApBusinessException.class).hasMessage("任務代碼重複A");
		}

		@Test
		@DisplayName("Given 已初始化 When 新增已存在代碼的 delay 任務 Then 拋出任務代碼重複")
		void givenExistsCode_whenAddDelay_thenDuplicate() {
			delays.add(new FakeDelay("A", TaskA.class, NEVER));
			manager.propertiesChange();

			assertThatThrownBy(() -> manager.add(new FakeDelay("A", TaskB.class, NEVER)))
					.isInstanceOf(ApBusinessException.class).hasMessage("任務代碼重複A");
		}

		@Test
		@DisplayName("Given 已初始化 When 新增 cron 任務 Then 可透過代碼操作該任務")
		void givenInit_whenAddCron_thenCanOperateByCode() {
			manager.propertiesChange();

			manager.add(new FakeCron("B", TaskB.class, NEVER));

			assertThatCode(() -> manager.start("B")).doesNotThrowAnyException();
			assertThatThrownBy(() -> manager.start("B")).hasMessageContaining("已啟動，請先關閉");
		}

		@Test
		@DisplayName("Given 已初始化 When 動態新增 cron 與 delay 任務 Then getTasks/getAll 可看到新任務")
		void givenInit_whenAdd_thenVisibleInTasks() {
			crons.add(new FakeCron("A", TaskA.class, NEVER));
			manager.propertiesChange();

			manager.add(new FakeCron("B", TaskB.class, NEVER));
			manager.add(new FakeDelay("C", TaskC.class, NEVER));

			assertThat(codes(manager.getContext().getAll())).containsExactly("A", "B", "C");
			assertThat(codes(manager.getTasks())).containsExactlyInAnyOrder("A", "B", "C");
		}

		@Test
		@DisplayName("Given 尚未初始化排程器 When 新增 cron 任務 Then 拋出排程服務尚未初始化")
		void givenNoScheduler_whenAddCron_thenNotInit() {
			assertThatThrownBy(() -> manager.add(new FakeCron("A", TaskA.class, NEVER)))
					.isInstanceOf(ApBusinessException.class).hasMessage("排程服務尚未初始化，無法新增任務A");
		}

		@Test
		@DisplayName("Given 尚未初始化排程器 When 新增 delay 任務 Then 拋出排程服務尚未初始化，且任務不會被加入")
		void givenNoScheduler_whenAddDelay_thenNotInit() {
			assertThatThrownBy(() -> manager.add(new FakeDelay("A", TaskA.class, NEVER)))
					.isInstanceOf(ApBusinessException.class).hasMessage("排程服務尚未初始化，無法新增任務A");

			assertThatThrownBy(() -> manager.start("A")).hasMessage("任務不存在A");
		}

		@Test
		@DisplayName("Given 1 個 cron 與 1 個 delay When 初始化 Then 兩者都經過 configRepository.newItem 包裝")
		void givenCronAndDelay_whenInit_thenAllWrappedByNewItem() {
			crons.add(new FakeCron("A", TaskA.class, NEVER));
			delays.add(new FakeDelay("B", TaskB.class, NEVER));

			manager.propertiesChange();

			assertThat(configRepository.newItemCodes).containsExactlyInAnyOrder("A", "B");
		}
	}

	// ========================================================================
	// propertiesChange
	// ========================================================================
	@Nested
	@DisplayName("propertiesChange() 參數變更")
	class PropertiesChange {

		@Test
		@DisplayName("Given repository 有 cron 與 delay 任務 When propertiesChange Then 載入全部任務且狀態為 IDLE、不會自動啟動")
		void givenTasks_whenPropertiesChange_thenLoadedButIdle() {
			crons.add(new FakeCron("A", TaskA.class, IMMEDIATELY));
			delays.add(new FakeDelay("B", TaskB.class, IMMEDIATELY));

			manager.propertiesChange();

			assertThat(codes(manager.getContext().getAll())).containsExactly("A", "B");
			assertThat(manager.getContext().getAll()).extracting(ISchedulingItemContext::getStatus)
					.containsOnly(SchedulingStatusCodes.IDLE.name());
			assertThat(manager.getContext().isStart()).isTrue();
			assertThat(taskA.executions.get()).isZero();
			assertThat(taskB.executions.get()).isZero();
		}

		@Test
		@DisplayName("Given 已初始化 When repository 資料異動後再次 propertiesChange Then 以新資料重建任務清單")
		void givenInit_whenRepositoryChangedAndPropertiesChange_thenRebuild() {
			crons.add(new FakeCron("A", TaskA.class, NEVER));
			manager.propertiesChange();

			crons.clear();
			crons.add(new FakeCron("B", TaskB.class, NEVER));
			manager.propertiesChange();

			assertThat(codes(manager.getContext().getAll())).containsExactly("B");
			assertThatThrownBy(() -> manager.start("A")).hasMessage("任務不存在A");
		}

		@Test
		@DisplayName("Given delay 與 cron 代碼重複 When propertiesChange Then 拋出重複例外，且不會建立任何任務")
		void givenDuplicateCode_whenPropertiesChange_thenNothingCreated() {
			delays.add(new FakeDelay("A", TaskA.class, NEVER));
			crons.add(new FakeCron("A", TaskB.class, NEVER));

			assertThatThrownBy(() -> manager.propertiesChange()).isInstanceOf(ApBusinessException.class)
					.hasMessage("任務代碼重複A");

			assertThat(manager.getContext().getAll()).isEmpty();
			assertThat(manager.getContext().isStart()).isFalse();
			assertThatThrownBy(() -> manager.start("A")).hasMessage("任務不存在A");
		}

		@Test
		@DisplayName("Given 已初始化 When repository 出現重複代碼並 propertiesChange Then 拋出重複例外，保留原任務清單")
		void givenInit_whenDuplicateAndPropertiesChange_thenKeepOrigin() {
			crons.add(new FakeCron("A", TaskA.class, NEVER));
			manager.propertiesChange();
			delays.add(new FakeDelay("A", TaskB.class, NEVER));

			assertThatThrownBy(() -> manager.propertiesChange()).hasMessage("任務代碼重複A");

			assertThat(codes(manager.getContext().getAll())).containsExactly("A");
			assertThat(manager.getContext().isStart()).isTrue();
			assertThatCode(() -> manager.start("A")).doesNotThrowAnyException();
		}

		@Test
		@DisplayName("Given 取得任務 bean 失敗 When propertiesChange Then 拋出例外，排程器回收且不留下部分任務")
		void givenGetBeanFail_whenPropertiesChange_thenRollback() {
			crons.add(new FakeCron("A", TaskA.class, NEVER));
			crons.add(new FakeCron("B", TaskB.class, NEVER));
			given(applicationContext.getBean(TaskB.class)).willThrow(new IllegalStateException("no bean"));

			assertThatThrownBy(() -> manager.propertiesChange()).hasMessage("no bean");

			assertThat(manager.getContext().getAll()).isEmpty();
			assertThat(manager.getContext().isStart()).isFalse();
		}

		@Test
		@DisplayName("Given 任務已排程 When propertiesChange Then 拋出尚有任務啟動中，原任務持續運作且可被關閉")
		void givenStarted_whenPropertiesChange_thenRejected() {
			crons.add(new FakeCron("A", TaskA.class, "50"));
			manager.propertiesChange();
			manager.start("A");
			awaitUntil(() -> taskA.executions.get() >= 1);

			assertThatThrownBy(() -> manager.propertiesChange()).isInstanceOf(ApBusinessException.class)
					.hasMessage("尚有1個任務啟動中，請先關閉後再變更參數");

			int executions = taskA.executions.get();
			awaitUntil(() -> taskA.executions.get() > executions);
			manager.cancel("A");
			awaitUntil(() -> SchedulingStatusCodes.DEAD.name().equals(item("A").getStatus()));
		}

		@Test
		@DisplayName("Given 任務 runOnce 執行中 When propertiesChange Then 拋出尚有任務啟動中")
		void givenRunningOnce_whenPropertiesChange_thenRejected() throws InterruptedException {
			crons.add(new FakeCron("A", TaskA.class, NEVER));
			manager.propertiesChange();
			taskA.blockUntilReleased();
			manager.runOnce("A");
			taskA.awaitStarted();

			assertThatThrownBy(() -> manager.propertiesChange()).hasMessage("尚有1個任務啟動中，請先關閉後再變更參數");
		}

		@Test
		@DisplayName("Given 任務已全部關閉 When propertiesChange Then 關閉舊排程器並以新排程器重建")
		void givenCanceled_whenPropertiesChange_thenReleaseOldScheduler() throws Exception {
			crons.add(new FakeCron("A", TaskA.class, NEVER));
			manager.propertiesChange();
			manager.start("A");
			manager.cancel("A");
			ThreadPoolTaskScheduler oldScheduler = getScheduler(manager);

			manager.propertiesChange();

			assertThat(oldScheduler.getScheduledThreadPoolExecutor().isShutdown()).isTrue();
			assertThat(getScheduler(manager)).isNotSameAs(oldScheduler);
			assertThat(item("A").getStatus()).isEqualTo(SchedulingStatusCodes.IDLE.name());
		}

		@Test
		@DisplayName("Given 非執行者主機 When propertiesChange Then 仍可載入任務供查詢")
		void givenNotExecuter_whenPropertiesChange_thenLoaded() {
			properites.execute = false;
			crons.add(new FakeCron("A", TaskA.class, NEVER));

			manager.propertiesChange();

			assertThat(codes(manager.getContext().getAll())).containsExactly("A");
		}
	}

	// ========================================================================
	// refresh
	// ========================================================================
	@Nested
	@DisplayName("refresh(code) 更新任務資訊")
	class Refresh {

		@Test
		@DisplayName("Given cron 任務未啟動 When refresh Then 從 repository 重新讀取名稱與 cron")
		void givenCronNotStarted_whenRefresh_thenReload() {
			crons.add(new FakeCron("A", TaskA.class, NEVER));
			manager.propertiesChange();
			FakeCron updated = new FakeCron("A", TaskA.class, "0 0 0 2 2 ?");
			updated.name = "A-新名稱";
			updated.actived = false;
			given(repository.getCcheduingCronsTask("A")).willReturn(updated);

			manager.refresh("A");

			ISchedulingItemContext item = item("A");
			assertThat(item.getName()).isEqualTo("A-新名稱");
			assertThat(item.getCron()).isEqualTo("0 0 0 2 2 ?");
			assertThat(item.isActived()).isFalse();
		}

		@Test
		@DisplayName("Given delay 任務未啟動 When refresh Then 從 repository.getCcheduingDelayTask 重新讀取")
		void givenDelayNotStarted_whenRefresh_thenReload() {
			delays.add(new FakeDelay("A", TaskA.class, NEVER));
			manager.propertiesChange();
			FakeDelay updated = new FakeDelay("A", TaskA.class, "5000");
			updated.name = "A-新名稱";
			given(repository.getCcheduingDelayTask("A")).willReturn(updated);

			manager.refresh("A");

			assertThat(item("A").getName()).isEqualTo("A-新名稱");
			assertThat(item("A").getCron()).isEqualTo("5000");
			then(repository).should(never()).getCcheduingCronsTask("A");
		}

		@Test
		@DisplayName("Given 任務已啟動 When refresh Then 拋出已啟動請先關閉")
		void givenStarted_whenRefresh_thenError() {
			crons.add(new FakeCron("A", TaskA.class, NEVER));
			manager.up();

			assertThatThrownBy(() -> manager.refresh("A")).isInstanceOf(ApBusinessException.class)
					.hasMessage("A已啟動，請先關閉");
		}

		@Test
		@DisplayName("Given 代碼不存在 When refresh Then 拋出任務不存在")
		void givenNotExists_whenRefresh_thenError() {
			manager.propertiesChange();

			assertThatThrownBy(() -> manager.refresh("X")).isInstanceOf(ApBusinessException.class)
					.hasMessage("任務不存在X");
		}
	}

	// ========================================================================
	// start
	// ========================================================================
	@Nested
	@DisplayName("start(code) 個別啟動")
	class Start {

		@Test
		@DisplayName("Given 啟用中的 cron 任務 When start Then 狀態為 RUNNABLE 且有下次執行秒數")
		void givenActive_whenStart_thenRunnable() {
			crons.add(new FakeCron("A", TaskA.class, NEVER));
			manager.propertiesChange();
			assertThat(item("A").getNextSec()).isEmpty();

			manager.start("A");

			assertThat(item("A").getStatus()).isEqualTo(SchedulingStatusCodes.RUNNABLE.name());
			assertThat(item("A").getNextSec()).isNotEmpty();
			assertThat(taskA.executions.get()).isZero();
		}

		@Test
		@DisplayName("Given 數字型週期且 delayStart=0 When start Then 立即執行並通知 monitor 開始與結束")
		void givenNumericCron_whenStart_thenExecuteImmediately() {
			crons.add(new FakeCron("A", TaskA.class, IMMEDIATELY));
			manager.propertiesChange();

			manager.start("A");

			ISchedulingItemContext item = item("A");
			then(monitor).should(timeout(3000)).updateEnd(item);
			then(monitor).should().updateStart(item);
			assertThat(taskA.executions.get()).isEqualTo(1);
			assertThat(item.getStatus()).isEqualTo(SchedulingStatusCodes.RUNNABLE.name());
			assertThat(item.getStartTime()).isNotEmpty();
			assertThat(item.getEndTime()).isNotEmpty();
		}

		@Test
		@DisplayName("Given delay 任務 When start Then 狀態為 RUNNABLE")
		void givenDelay_whenStart_thenRunnable() {
			delays.add(new FakeDelay("A", TaskA.class, NEVER));
			manager.propertiesChange();

			manager.start("A");

			assertThat(item("A").getStatus()).isEqualTo(SchedulingStatusCodes.RUNNABLE.name());
		}

		@Test
		@DisplayName("Given 任務已啟動 When 再次 start Then 拋出已啟動請先關閉")
		void givenStarted_whenStartAgain_thenError() {
			crons.add(new FakeCron("A", TaskA.class, NEVER));
			manager.propertiesChange();
			manager.start("A");

			assertThatThrownBy(() -> manager.start("A")).isInstanceOf(ApBusinessException.class)
					.hasMessage("A已啟動，請先關閉");
		}

		@Test
		@DisplayName("Given 未啟用的任務 When start Then 不排程，狀態維持 IDLE")
		void givenInactive_whenStart_thenIdle() {
			FakeCron cron = new FakeCron("A", TaskA.class, IMMEDIATELY);
			cron.actived = false;
			crons.add(cron);
			manager.propertiesChange();

			manager.start("A");

			assertThat(item("A").getStatus()).isEqualTo(SchedulingStatusCodes.IDLE.name());
			assertThat(item("A").getNextSec()).isEmpty();
		}

		@Test
		@DisplayName("Given 非執行者主機 When start Then 拋出非執行者無權限操作")
		void givenNotExecuter_whenStart_thenError() {
			properites.execute = false;
			crons.add(new FakeCron("A", TaskA.class, NEVER));
			manager.propertiesChange();

			assertThatThrownBy(() -> manager.start("A")).isInstanceOf(ApBusinessException.class)
					.hasMessageContaining("非執行者").hasMessageContaining("無權限操作");
		}

		@Test
		@DisplayName("Given 代碼不存在 When start Then 拋出任務不存在")
		void givenNotExists_whenStart_thenError() {
			manager.propertiesChange();

			assertThatThrownBy(() -> manager.start("X")).hasMessage("任務不存在X");
		}

		@Test
		@DisplayName("Given 服務已 down（任務仍保留在 map） When start Then 拋出已關機不提供作業新增")
		void givenDown_whenStart_thenRejected() {
			crons.add(new FakeCron("A", TaskA.class, NEVER));
			manager.up();
			manager.down();

			assertThatThrownBy(() -> manager.start("A")).isInstanceOf(ApBusinessException.class)
					.hasMessage("已關機不提供作業新增A");
		}

		@Test
		@DisplayName("Given 任務已 cancel When 再次 start Then 可重新啟動為 RUNNABLE")
		void givenCanceled_whenStart_thenRunnableAgain() {
			crons.add(new FakeCron("A", TaskA.class, NEVER));
			manager.propertiesChange();
			manager.start("A");
			manager.cancel("A");

			manager.start("A");

			assertThat(item("A").getStatus()).isEqualTo(SchedulingStatusCodes.RUNNABLE.name());
		}
	}

	// ========================================================================
	// runOnce
	// ========================================================================
	@Nested
	@DisplayName("runOnce(code) 執行一次")
	class RunOnce {

		@Test
		@DisplayName("Given 任務閒置 When runOnce Then 立即執行一次，結束後狀態為 IDLE")
		void givenIdle_whenRunOnce_thenExecuteOnce() {
			crons.add(new FakeCron("A", TaskA.class, NEVER));
			manager.propertiesChange();

			manager.runOnce("A");

			ISchedulingItemContext item = item("A");
			then(monitor).should(timeout(3000)).updateEnd(item);
			then(monitor).should().updateStart(item);
			assertThat(taskA.executions.get()).isEqualTo(1);
			assertThat(item.getStatus()).isEqualTo(SchedulingStatusCodes.IDLE.name());
		}

		@Test
		@DisplayName("Given 任務已排程 When runOnce Then 立即執行一次，結束後狀態維持 RUNNABLE")
		void givenScheduled_whenRunOnce_thenRunnable() {
			crons.add(new FakeCron("A", TaskA.class, NEVER));
			manager.propertiesChange();
			manager.start("A");

			manager.runOnce("A");

			then(monitor).should(timeout(3000)).updateEnd(item("A"));
			assertThat(item("A").getStatus()).isEqualTo(SchedulingStatusCodes.RUNNABLE.name());
		}

		@Test
		@DisplayName("Given 一次性執行中且未開啟重複點擊取消 When 再次 runOnce Then 拋出服務已啟動不執行一次執行")
		void givenRunningOnce_whenRunOnceAgain_thenError() throws InterruptedException {
			configRepository.doubleClickCancel = false;
			crons.add(new FakeCron("A", TaskA.class, NEVER));
			manager.propertiesChange();
			taskA.blockUntilReleased();
			manager.runOnce("A");
			taskA.awaitStarted();

			assertThatThrownBy(() -> manager.runOnce("A")).isInstanceOf(ApBusinessException.class)
					.hasMessage("服務已啟動，不執行一次執行");
		}

		@Test
		@DisplayName("Given 一次性執行中且開啟重複點擊取消(中斷) When 再次 runOnce Then 中斷執行中的任務")
		void givenRunningOnceAndDoubleClickCancel_whenRunOnceAgain_thenInterrupt() throws InterruptedException {
			configRepository.doubleClickCancel = true;
			configRepository.cancel = true;
			crons.add(new FakeCron("A", TaskA.class, NEVER));
			manager.propertiesChange();
			taskA.blockUntilReleased();
			manager.runOnce("A");
			taskA.awaitStarted();

			manager.runOnce("A");

			awaitUntil(() -> taskA.interrupted);
			assertThat(taskA.executions.get()).isEqualTo(1);
		}

		@Test
		@DisplayName("Given 任務執行拋出例外 When runOnce Then 通知 monitor.updateError 並將錯誤寫入訊息")
		void givenTaskFail_whenRunOnce_thenUpdateError() {
			crons.add(new FakeCron("A", TaskA.class, NEVER));
			manager.propertiesChange();
			taskA.failure = new IllegalStateException("boom");

			manager.runOnce("A");

			ISchedulingItemContext item = item("A");
			then(monitor).should(timeout(3000)).updateEnd(item);
			then(monitor).should().updateError(item, taskA.failure);
			assertThat(item.getMessage()).contains("boom");
			assertThat(item.getStatus()).isEqualTo(SchedulingStatusCodes.IDLE.name());
		}

		@Test
		@DisplayName("Given 代碼不存在 When runOnce Then 拋出任務不存在")
		void givenNotExists_whenRunOnce_thenError() {
			manager.propertiesChange();

			assertThatThrownBy(() -> manager.runOnce("X")).hasMessage("任務不存在X");
		}

		@Test
		@DisplayName("Given 非執行者主機 When runOnce Then 拋出非執行者無權限操作且不執行")
		void givenNotExecuter_whenRunOnce_thenError() {
			properites.execute = false;
			crons.add(new FakeCron("A", TaskA.class, NEVER));
			manager.propertiesChange();

			assertThatThrownBy(() -> manager.runOnce("A")).isInstanceOf(ApBusinessException.class)
					.hasMessageContaining("非執行者").hasMessageContaining("無權限操作");

			then(monitor).should(never()).updateStart(any());
			assertThat(taskA.executions.get()).isZero();
		}
	}

	// ========================================================================
	// cancel
	// ========================================================================
	@Nested
	@DisplayName("cancel(code) 個別關閉")
	class Cancel {

		@Test
		@DisplayName("Given 任務已排程但未執行 When cancel Then 狀態為 DEAD 並通知 monitor.updateCancel")
		void givenScheduled_whenCancel_thenDead() {
			crons.add(new FakeCron("A", TaskA.class, NEVER));
			manager.propertiesChange();
			manager.start("A");

			manager.cancel("A");

			ISchedulingItemContext item = item("A");
			assertThat(item.getStatus()).isEqualTo(SchedulingStatusCodes.DEAD.name());
			assertThat(item.getNextSec()).isEmpty();
			then(monitor).should().updateCancel(item);
		}

		@Test
		@DisplayName("Given 任務未啟動 When cancel Then 不拋例外、狀態維持 IDLE、不通知 updateCancel")
		void givenNotStarted_whenCancel_thenNothing() {
			crons.add(new FakeCron("A", TaskA.class, NEVER));
			manager.propertiesChange();

			assertThatCode(() -> manager.cancel("A")).doesNotThrowAnyException();

			assertThat(item("A").getStatus()).isEqualTo(SchedulingStatusCodes.IDLE.name());
			then(monitor).should(never()).updateCancel(any());
		}

		@Test
		@DisplayName("Given 任務執行中且不中斷 When cancel Then 任務感知已中止，執行完畢後狀態為 DEAD")
		void givenRunning_whenCancel_thenDeadAfterFinish() throws InterruptedException {
			configRepository.cancel = false;
			crons.add(new FakeCron("A", TaskA.class, IMMEDIATELY));
			manager.propertiesChange();
			taskA.blockUntilReleased();
			manager.start("A");
			taskA.awaitStarted();

			manager.cancel("A");

			ISchedulingItemContext item = item("A");
			assertThat(item.getStatus()).isEqualTo(SchedulingStatusCodes.RUNNNIG.name());
			then(monitor).should(never()).updateCancel(any());

			taskA.release();

			then(monitor).should(timeout(3000)).updateEnd(item);
			assertThat(taskA.interrupted).isFalse();
			assertThat(taskA.runningAfterWait).isFalse();
			assertThat(item.getStatus()).isEqualTo(SchedulingStatusCodes.DEAD.name());
			then(monitor).should().updateCancel(item);
		}

		@Test
		@DisplayName("Given 任務執行中且設定中斷 When cancel Then 中斷執行緒")
		void givenRunningAndInterrupt_whenCancel_thenInterrupted() throws InterruptedException {
			configRepository.cancel = true;
			crons.add(new FakeCron("A", TaskA.class, IMMEDIATELY));
			manager.propertiesChange();
			taskA.blockUntilReleased();
			manager.start("A");
			taskA.awaitStarted();

			manager.cancel("A");

			then(monitor).should(timeout(3000)).updateEnd(item("A"));
			assertThat(taskA.interrupted).isTrue();
			assertThat(item("A").getStatus()).isEqualTo(SchedulingStatusCodes.DEAD.name());
		}

		@Test
		@DisplayName("Given 非執行者主機 When cancel Then 拋出非執行者無權限操作")
		void givenNotExecuter_whenCancel_thenError() {
			properites.execute = false;
			crons.add(new FakeCron("A", TaskA.class, NEVER));
			manager.propertiesChange();

			assertThatThrownBy(() -> manager.cancel("A")).hasMessageContaining("非執行者");
		}

		@Test
		@DisplayName("Given 代碼不存在 When cancel Then 拋出任務不存在")
		void givenNotExists_whenCancel_thenError() {
			manager.propertiesChange();

			assertThatThrownBy(() -> manager.cancel("X")).hasMessage("任務不存在X");
		}
	}

	// ========================================================================
	// cancelAll / startAll
	// ========================================================================
	@Nested
	@DisplayName("cancelAll() / startAll() 全部關閉與啟動")
	class All {

		@Test
		@DisplayName("Given 2 個啟用任務與 1 個未啟用任務 When startAll Then 啟用者 RUNNABLE、未啟用者 IDLE")
		void givenMixed_whenStartAll_thenOnlyActiveRunnable() {
			crons.add(new FakeCron("A", TaskA.class, NEVER));
			delays.add(new FakeDelay("B", TaskB.class, NEVER));
			FakeCron inactive = new FakeCron("C", TaskC.class, NEVER);
			inactive.actived = false;
			crons.add(inactive);
			manager.propertiesChange();

			manager.startAll();

			assertThat(item("A").getStatus()).isEqualTo(SchedulingStatusCodes.RUNNABLE.name());
			assertThat(item("B").getStatus()).isEqualTo(SchedulingStatusCodes.RUNNABLE.name());
			assertThat(item("C").getStatus()).isEqualTo(SchedulingStatusCodes.IDLE.name());
		}

		@Test
		@DisplayName("Given 已有任務啟動 When startAll Then 拋出已啟動請先關閉")
		void givenOneStarted_whenStartAll_thenError() {
			crons.add(new FakeCron("A", TaskA.class, NEVER));
			crons.add(new FakeCron("B", TaskB.class, NEVER));
			manager.propertiesChange();
			manager.start("A");

			assertThatThrownBy(() -> manager.startAll()).hasMessageContaining("已啟動，請先關閉");
		}

		@Test
		@DisplayName("Given 全部任務已啟動 When cancelAll Then 全部狀態為 DEAD")
		void givenAllStarted_whenCancelAll_thenAllDead() {
			crons.add(new FakeCron("A", TaskA.class, NEVER));
			delays.add(new FakeDelay("B", TaskB.class, NEVER));
			manager.propertiesChange();
			manager.startAll();

			manager.cancelAll();

			assertThat(manager.getContext().getAll()).extracting(ISchedulingItemContext::getStatus)
					.containsOnly(SchedulingStatusCodes.DEAD.name());
			then(monitor).should(times(2)).updateCancel(any());
		}

		@Test
		@DisplayName("Given 非執行者主機 When startAll / cancelAll Then 拋出非執行者無權限操作")
		void givenNotExecuter_whenAll_thenError() {
			properites.execute = false;
			crons.add(new FakeCron("A", TaskA.class, NEVER));
			manager.propertiesChange();

			assertThatThrownBy(() -> manager.startAll()).hasMessageContaining("非執行者");
			assertThatThrownBy(() -> manager.cancelAll()).hasMessageContaining("非執行者");
		}

		@Test
		@DisplayName("Given 沒有任何任務 When startAll / cancelAll Then 不拋例外")
		void givenEmpty_whenAll_thenNothing() {
			manager.propertiesChange();

			assertThatCode(() -> {
				manager.startAll();
				manager.cancelAll();
			}).doesNotThrowAnyException();
		}
	}

	// ========================================================================
	// getTasks / getContext
	// ========================================================================
	@Nested
	@DisplayName("getTasks() / getContext() 取得資訊")
	class Context {

		@Test
		@DisplayName("Given 任務代碼為 C、A、B When getAll Then 依代碼排序")
		void givenUnsorted_whenGetAll_thenSortedByCode() {
			crons.add(new FakeCron("C", TaskC.class, NEVER));
			crons.add(new FakeCron("A", TaskA.class, NEVER));
			delays.add(new FakeDelay("B", TaskB.class, NEVER));
			manager.propertiesChange();

			assertThat(codes(manager.getContext().getAll())).containsExactly("A", "B", "C");
		}

		@Test
		@DisplayName("Given 已初始化 When 修改 getAll / getTasks 回傳的清單 Then 不影響管理器內部任務")
		void givenInit_whenModifyTasks_thenInternalUnchanged() {
			crons.add(new FakeCron("A", TaskA.class, NEVER));
			manager.propertiesChange();

			assertThat(manager.getContext().getAll()).isNotSameAs(manager.getTasks());
			manager.getContext().getAll().clear();
			manager.getTasks().clear();

			assertThat(codes(manager.getTasks())).containsExactly("A");
			assertThat(codes(manager.getContext().getAll())).containsExactly("A");
		}

		@Test
		@DisplayName("Given 未初始化 When getTasks Then 回傳空清單")
		void givenNotInit_whenGetTasks_thenEmpty() {
			assertThat(manager.getTasks()).isEmpty();
			assertThat(manager.getContext().getAll()).isEmpty();
		}

		@Test
		@DisplayName("Given 屬性 isExecute When getContext Then isExecuter 與屬性一致，getProperites 為同一物件")
		void givenProperites_whenGetContext_thenReflect() {
			properites.execute = true;
			assertThat(manager.getContext().isExecuter()).isTrue();

			properites.execute = false;
			assertThat(manager.getContext().isExecuter()).isFalse();
			assertThat(manager.getContext().getProperites()).isSameAs(properites);
		}

		@Test
		@DisplayName("When getHostName Then 回傳大寫主機名稱")
		void whenGetHostName_thenUpperCase() {
			assertThat(manager.getContext().getHostName()).isEqualTo(HostNameUtls.getHostName().toUpperCase());
		}

		@Test
		@DisplayName("Given 生命週期變化 When isStart Then 未初始化 false、up 後 true、down 後 false")
		void givenLifecycle_whenIsStart_thenReflect() {
			assertThat(manager.getContext().isStart()).isFalse();

			manager.up();
			assertThat(manager.getContext().isStart()).isTrue();

			manager.down();
			assertThat(manager.getContext().isStart()).isFalse();
		}

		@Test
		@DisplayName("Given 1 個任務執行中、1 個閒置 When getRunningCount Then 回傳 1")
		void givenOneRunning_whenGetRunningCount_thenOne() throws InterruptedException {
			crons.add(new FakeCron("A", TaskA.class, NEVER));
			crons.add(new FakeCron("B", TaskB.class, NEVER));
			manager.propertiesChange();
			assertThat(manager.getContext().getRunningCount()).isZero();
			taskA.blockUntilReleased();

			manager.runOnce("A");
			taskA.awaitStarted();

			assertThat(manager.getContext().getRunningCount()).isEqualTo(1);
		}

		@Test
		@DisplayName("Given 執行者 / 非執行者 When ifExecuter / ifNotExecuter / ifExecuterNotMessage Then 依身分執行或拋例外")
		void givenRole_whenIfExecuter_thenRunOrThrow() {
			AtomicInteger counter = new AtomicInteger();
			Runnable runnable = counter::incrementAndGet;

			properites.execute = true;
			manager.getContext().ifExecuter(runnable);
			manager.getContext().ifExecuterNotMessage(runnable);
			assertThatThrownBy(() -> manager.getContext().ifNotExecuter(runnable)).hasMessageContaining("不提供該操作");
			assertThat(counter.get()).isEqualTo(2);

			properites.execute = false;
			manager.getContext().ifNotExecuter(runnable);
			manager.getContext().ifExecuterNotMessage(runnable);
			assertThatThrownBy(() -> manager.getContext().ifExecuter(runnable)).hasMessageContaining("無權限操作");
			assertThat(counter.get()).isEqualTo(3);
		}
	}

	// ========================================================================
	// up
	// ========================================================================
	@Nested
	@DisplayName("up() 排程服務啟動")
	class Up {

		@Test
		@DisplayName("Given 服務未啟動 When up Then 載入任務、呼叫 upHook、啟用任務為 RUNNABLE")
		void givenNotStarted_whenUp_thenStarted() {
			crons.add(new FakeCron("A", TaskA.class, NEVER));
			FakeDelay inactive = new FakeDelay("B", TaskB.class, NEVER);
			inactive.actived = false;
			delays.add(inactive);

			manager.up();

			assertThat(manager.getContext().isStart()).isTrue();
			assertThat(item("A").getStatus()).isEqualTo(SchedulingStatusCodes.RUNNABLE.name());
			assertThat(item("B").getStatus()).isEqualTo(SchedulingStatusCodes.IDLE.name());
			then(hook).should().upHook();
		}

		@Test
		@DisplayName("Given 服務已啟動 When 再次 up Then 拋出服務已啟動且不再呼叫 upHook")
		void givenStarted_whenUp_thenError() {
			manager.up();

			assertThatThrownBy(() -> manager.up()).isInstanceOf(ApBusinessException.class).hasMessage("服務已啟動");
			then(hook).should(times(1)).upHook();
		}

		@Test
		@DisplayName("Given 只呼叫過 propertiesChange When up Then 視為已啟動並拋出服務已啟動")
		void givenPropertiesChanged_whenUp_thenError() {
			manager.propertiesChange();

			assertThatThrownBy(() -> manager.up()).hasMessage("服務已啟動");
		}

		@Test
		@DisplayName("Given 服務 up 後 down When 再次 up Then 重新從 repository 載入並啟動")
		void givenDown_whenUp_thenReload() {
			crons.add(new FakeCron("A", TaskA.class, NEVER));
			manager.up();
			manager.down();
			crons.add(new FakeCron("B", TaskB.class, NEVER));

			manager.up();

			assertThat(codes(manager.getContext().getAll())).containsExactly("A", "B");
			assertThat(item("B").getStatus()).isEqualTo(SchedulingStatusCodes.RUNNABLE.name());
			then(repository).should(times(2)).getCcheduingCronsTask();
			then(hook).should(times(2)).upHook();
		}

		@Test
		@DisplayName("Given 任務曾執行過 When down 後立即 up（重複 200 次） Then 不會誤判任務尚未完全停止")
		void givenExecuted_whenDownThenUpImmediately_thenNoRace() {
			crons.add(new FakeCron("A", TaskA.class, IMMEDIATELY));

			for (int i = 1; i <= 200; i++) {
				manager.up();
				int expected = i;
				awaitUntil(() -> taskA.executions.get() >= expected);
				manager.down();
			}

			assertThatCode(() -> manager.up()).doesNotThrowAnyException();
		}

		@Test
		@DisplayName("Given 非執行者主機 When up Then 拋出非執行者，服務維持未啟動且不呼叫 upHook")
		void givenNotExecuter_whenUp_thenRejected() {
			properites.execute = false;
			crons.add(new FakeCron("A", TaskA.class, NEVER));

			assertThatThrownBy(() -> manager.up()).isInstanceOf(ApBusinessException.class)
					.hasMessageContaining("非執行者").hasMessageContaining("無權限操作");

			assertThat(manager.getContext().isStart()).isFalse();
			assertThat(manager.getTasks()).isEmpty();
			then(hook).should(never()).upHook();
		}

		@Test
		@DisplayName("Given delay 與 cron 代碼重複 When up Then 拋出重複例外，服務維持未啟動")
		void givenDuplicateCode_whenUp_thenRejected() {
			delays.add(new FakeDelay("A", TaskA.class, NEVER));
			crons.add(new FakeCron("A", TaskB.class, NEVER));

			assertThatThrownBy(() -> manager.up()).hasMessage("任務代碼重複A");

			assertThat(manager.getContext().isStart()).isFalse();
			then(hook).should(never()).upHook();
		}

		@Test
		@DisplayName("Given 未設定 hook（預設空白 hook） When up / down Then 不拋例外")
		void givenDefaultHook_whenUpDown_thenNoError() {
			SpringSchedulingManager target = new SpringSchedulingManager(repository, applicationContext, monitor,
					properites, configRepository);

			assertThatCode(() -> {
				target.up();
				target.down();
			}).doesNotThrowAnyException();
		}
	}

	// ========================================================================
	// down
	// ========================================================================
	@Nested
	@DisplayName("down() 排程服務關閉")
	class Down {

		@Test
		@DisplayName("Given 服務未啟動 When down Then 拋出服務已關閉")
		void givenNotStarted_whenDown_thenError() {
			assertThatThrownBy(() -> manager.down()).isInstanceOf(ApBusinessException.class).hasMessage("服務已關閉");
			then(hook).should(never()).downHook();
		}

		@Test
		@DisplayName("Given 服務已啟動 When down Then 取消並回收所有任務、呼叫 downHook、isStart 為 false")
		void givenStarted_whenDown_thenDestroyAll() {
			crons.add(new FakeCron("A", TaskA.class, NEVER));
			delays.add(new FakeDelay("B", TaskB.class, NEVER));
			manager.up();

			manager.down();

			assertThat(manager.getContext().isStart()).isFalse();
			assertThat(manager.getContext().getAll()).extracting(ISchedulingItemContext::getStatus)
					.containsOnly(SchedulingStatusCodes.DEAD.name());
			then(monitor).should().destroy(item("A"));
			then(monitor).should().destroy(item("B"));
			then(hook).should().downHook();
		}

		@Test
		@DisplayName("Given 服務已 down When 再次 down Then 拋出服務已關閉且 downHook 只呼叫一次")
		void givenDown_whenDownAgain_thenError() {
			manager.up();
			manager.down();

			assertThatThrownBy(() -> manager.down()).hasMessage("服務已關閉");
			then(hook).should(times(1)).downHook();
		}

		@Test
		@DisplayName("Given monitor.destroy 拋出業務例外 When down Then 忽略例外並完成關閉")
		void givenDestroyError_whenDown_thenIgnore() {
			crons.add(new FakeCron("A", TaskA.class, NEVER));
			manager.up();
			willThrow(new ApBusinessException("destroy error")).given(monitor).destroy(any());

			assertThatCode(() -> manager.down()).doesNotThrowAnyException();

			assertThat(manager.getContext().isStart()).isFalse();
			then(hook).should().downHook();
		}

		@Test
		@DisplayName("Given 任務執行中 When down 進行中 Then 其他操作拋出服務忙碌中，任務結束後 down 完成")
		void givenRunning_whenDownInProgress_thenBusy() throws Exception {
			configRepository.cancel = false;
			properites.closeTimeout = 10;
			crons.add(new FakeCron("A", TaskA.class, IMMEDIATELY));
			taskA.blockUntilReleased();
			manager.up();
			taskA.awaitStarted();

			ExecutorService executor = Executors.newSingleThreadExecutor();
			try {
				Future<?> down = executor.submit(() -> manager.down());
				then(monitor).should(timeout(3000)).destroy(any());

				assertThatThrownBy(() -> manager.start("A")).isInstanceOf(ApBusinessException.class)
						.hasMessage("服務忙碌中請稍後操作");
				assertThatThrownBy(() -> manager.up()).hasMessage("服務忙碌中請稍後操作");
				assertThatThrownBy(() -> manager.propertiesChange()).hasMessage("服務忙碌中請稍後操作");
				assertThat(down.isDone()).isFalse();

				taskA.release();
				down.get(5, TimeUnit.SECONDS);
			} finally {
				executor.shutdownNow();
			}

			assertThat(manager.getContext().isStart()).isFalse();
			assertThat(taskA.runningAfterWait).isFalse();
		}

		@Test
		@DisplayName("Given 未設定關閉策略且任務執行超過 closeTimeout When down Then 逾時後返回、不中斷任務，up 拋出任務尚未完全停止，任務結束後可再 up")
		void givenRunningOverTimeout_whenDown_thenUpRejectedUntilStopped() throws InterruptedException {
			configRepository.cancel = false;
			properites.closeTimeout = 1;
			crons.add(new FakeCron("A", TaskA.class, IMMEDIATELY));
			taskA.blockUntilReleased();
			manager.up();
			taskA.awaitStarted();

			manager.down();

			assertThat(taskA.interrupted).isFalse();
			assertThat(manager.getContext().isStart()).isFalse();
			assertThatThrownBy(() -> manager.up()).isInstanceOf(ApBusinessException.class)
					.hasMessage("任務尚未完全停止，請稍後");

			taskA.release();
			upWhenThreadsStopped();
			assertThat(manager.getContext().isStart()).isTrue();
		}

		@Test
		@DisplayName("Given 非執行者主機已初始化 When down Then 忽略任務取消的權限例外，完成關閉並呼叫 downHook")
		void givenNotExecuter_whenDown_thenReleased() throws Exception {
			properites.execute = false;
			crons.add(new FakeCron("A", TaskA.class, NEVER));
			manager.propertiesChange();
			ThreadPoolTaskScheduler scheduler = getScheduler(manager);

			assertThatCode(() -> manager.down()).doesNotThrowAnyException();

			assertThat(manager.getContext().isStart()).isFalse();
			assertThat(scheduler.getScheduledThreadPoolExecutor().isShutdown()).isTrue();
			then(hook).should().downHook();
		}
	}

	// ========================================================================
	// setShutdownPolicy
	// ========================================================================
	@Nested
	@DisplayName("setShutdownPolicy() 關閉逾時處理策略")
	class ShutdownPolicy {

		final List<String> residualCodes = Collections.synchronizedList(new ArrayList<>());
		final List<Thread> residualThreads = Collections.synchronizedList(new ArrayList<>());

		/** 記錄殘留資訊的中斷策略 */
		final InterruptSchedulingShutdownPolicy recordingPolicy = new InterruptSchedulingShutdownPolicy(1) {
			@Override
			protected void onResidual(ISchedulingShutdownContext context) {
				super.onResidual(context);
				residualCodes.addAll(codes(context.getRunningTasks()));
				residualThreads.addAll(context.getAliveThreads());
			}
		};

		@BeforeEach
		void givenBlockingTaskOverTimeout() {
			configRepository.cancel = false;
			properites.closeTimeout = 1;
			crons.add(new FakeCron("A", TaskA.class, IMMEDIATELY));
			crons.add(new FakeCron("B", TaskB.class, NEVER));
			taskA.blockUntilReleased();
		}

		@Test
		@DisplayName("Given 中斷策略且任務回應中斷 When down 逾時 Then 中斷任務，down 完成後可立即 up")
		void givenInterruptPolicy_whenDownTimeout_thenInterrupted() throws InterruptedException {
			manager.setShutdownPolicy(recordingPolicy);
			manager.up();
			taskA.awaitStarted();

			manager.down();

			assertThat(taskA.interrupted).isTrue();
			assertThat(residualCodes).isEmpty();
			assertThat(item("A").getStatus()).isEqualTo(SchedulingStatusCodes.DEAD.name());
			then(hook).should().downHook();
			assertThatCode(() -> manager.up()).doesNotThrowAnyException();
		}

		@Test
		@DisplayName("Given 中斷策略且任務不理會中斷 When down 逾時 Then 回報殘留任務與執行緒，up 拋出任務尚未完全停止，任務結束後可再 up")
		void givenInterruptPolicyAndIgnoreInterrupt_whenDownTimeout_thenResidual() throws InterruptedException {
			taskA.ignoreInterrupt = true;
			manager.setShutdownPolicy(recordingPolicy);
			manager.up();
			taskA.awaitStarted();

			manager.down();

			assertThat(taskA.interrupted).isTrue();
			assertThat(residualCodes).containsExactly("A");
			assertThat(residualThreads).isNotEmpty().allMatch(Thread::isAlive);
			assertThat(manager.getContext().isStart()).isFalse();
			then(hook).should().downHook();
			assertThatThrownBy(() -> manager.up()).hasMessage("任務尚未完全停止，請稍後");

			taskA.release();
			upWhenThreadsStopped();
			assertThat(manager.getContext().isStart()).isTrue();
		}

		@Test
		@DisplayName("Given 自訂策略 When down 逾時 Then 傳入 closeTimeout 與執行中任務")
		void givenCustomPolicy_whenDownTimeout_thenContextProvided() throws InterruptedException {
			ISchedulingShutdownPolicy policy = mock(ISchedulingShutdownPolicy.class);
			List<String> running = new ArrayList<>();
			AtomicInteger closeTimeout = new AtomicInteger();
			willAnswer(invocation -> {
				ISchedulingShutdownContext context = invocation.getArgument(0);
				closeTimeout.set(context.getCloseTimeout());
				running.addAll(codes(context.getRunningTasks()));
				return null;
			}).given(policy).onTimeout(any());
			manager.setShutdownPolicy(policy);
			manager.up();
			taskA.awaitStarted();

			manager.down();

			then(policy).should(times(1)).onTimeout(any());
			assertThat(closeTimeout.get()).isEqualTo(1);
			assertThat(running).containsExactly("A");
			assertThat(taskA.interrupted).isFalse();
		}

		@Test
		@DisplayName("Given 任務在 closeTimeout 內結束 When down Then 不呼叫關閉策略")
		void givenFinishInTime_whenDown_thenPolicyNotCalled() throws InterruptedException {
			ISchedulingShutdownPolicy policy = mock(ISchedulingShutdownPolicy.class);
			manager.setShutdownPolicy(policy);
			manager.up();
			taskA.awaitStarted();
			taskA.release();

			manager.down();

			then(policy).should(never()).onTimeout(any());
		}

		@Test
		@DisplayName("Given 關閉策略拋出例外 When down 逾時 Then 忽略例外並完成關閉")
		void givenPolicyError_whenDownTimeout_thenIgnore() throws InterruptedException {
			ISchedulingShutdownPolicy policy = mock(ISchedulingShutdownPolicy.class);
			willThrow(new IllegalStateException("policy error")).given(policy).onTimeout(any());
			manager.setShutdownPolicy(policy);
			manager.up();
			taskA.awaitStarted();

			assertThatCode(() -> manager.down()).doesNotThrowAnyException();

			assertThat(manager.getContext().isStart()).isFalse();
			then(hook).should().downHook();
		}

		@Test
		@DisplayName("When setShutdownPolicy(null) Then 拋出 NullPointerException")
		void whenSetNull_thenNpe() {
			assertThatThrownBy(() -> manager.setShutdownPolicy(null)).isInstanceOf(NullPointerException.class);
		}

		@Test
		@DisplayName("Given 寬限時間為負數 When 建立 InterruptSchedulingShutdownPolicy Then 拋出 IllegalArgumentException，預設寬限 10 秒")
		void givenNegativeGrace_whenCreate_thenError() {
			assertThatThrownBy(() -> new InterruptSchedulingShutdownPolicy(-1))
					.isInstanceOf(IllegalArgumentException.class);
			assertThat(new InterruptSchedulingShutdownPolicy().getGraceSeconds()).isEqualTo(10);
		}
	}

	// ========================================================================
	// SchedulingManagers
	// ========================================================================
	@Nested
	@DisplayName("SchedulingManagers.createSchedulingManager 建立")
	class Factory {

		@Test
		@DisplayName("Given 必要參數為 null When create Then 拋出 NullPointerException")
		void givenNullArgs_whenCreate_thenNpe() {
			assertThatThrownBy(() -> SchedulingManagers.createSchedulingManager(null, applicationContext, monitor,
					properites, configRepository)).isInstanceOf(NullPointerException.class);
			assertThatThrownBy(() -> SchedulingManagers.createSchedulingManager(repository, null, monitor,
					properites, configRepository)).isInstanceOf(NullPointerException.class);
			assertThatThrownBy(() -> SchedulingManagers.createSchedulingManager(repository, applicationContext, null,
					properites, configRepository)).isInstanceOf(NullPointerException.class);
			assertThatThrownBy(() -> SchedulingManagers.createSchedulingManager(repository, applicationContext,
					monitor, null, configRepository)).isInstanceOf(NullPointerException.class);
			assertThatThrownBy(() -> SchedulingManagers.createSchedulingManager(repository, applicationContext,
					monitor, properites, null)).isInstanceOf(NullPointerException.class);
		}

		@Test
		@DisplayName("Given 參數齊全 When create Then 建立 SpringSchedulingManager")
		void givenArgs_whenCreate_thenCreated() {
			assertThat(SchedulingManagers.createSchedulingManager(repository, applicationContext, monitor,
					properites, configRepository)).isInstanceOf(SpringSchedulingManager.class);
		}
	}

	// ========================================================================
	// helper
	// ========================================================================

	ISchedulingItemContext item(String code) {
		return manager.getTasks().stream().filter(i -> i.getCode().equals(code)).findFirst()
				.orElseThrow(() -> new AssertionError("找不到任務 " + code));
	}

	static List<String> codes(List<ISchedulingItemContext> items) {
		return items.stream().map(ISchedulingItemContext::getCode).collect(Collectors.toList());
	}

	/**
	 * down 逾時返回時任務仍在執行，任務結束後 worker thread 才會陸續結束，故重試 up 至成功
	 */
	void upWhenThreadsStopped() {
		awaitUntil(() -> {
			try {
				manager.up();
				return true;
			} catch (ApBusinessException e) {
				if (!"任務尚未完全停止，請稍後".equals(e.getMessage())) {
					throw e;
				}
				return false;
			}
		});
	}

	static void awaitUntil(BooleanSupplier condition) {
		long deadline = System.currentTimeMillis() + 5000;
		while (!condition.getAsBoolean()) {
			if (System.currentTimeMillis() > deadline) {
				throw new AssertionError("等待條件逾時");
			}
			try {
				TimeUnit.MILLISECONDS.sleep(20);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new AssertionError(e);
			}
		}
	}

	static ThreadPoolTaskScheduler getScheduler(SpringSchedulingManager target) throws Exception {
		Field field = SpringSchedulingManager.class.getDeclaredField("scheduler");
		field.setAccessible(true);
		return (ThreadPoolTaskScheduler) field.get(target);
	}

	static void destroyScheduler(SpringSchedulingManager target) {
		try {
			ThreadPoolTaskScheduler scheduler = getScheduler(target);
			if (scheduler != null) {
				scheduler.setAwaitTerminationSeconds(1);
				scheduler.destroy();
			}
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	static class FakeProperites implements ISpringSchedulingProperites {
		volatile boolean execute = true;
		volatile int closeTimeout = 5;

		@Override
		public boolean isExecute() {
			return execute;
		}

		@Override
		public int getCloseTimeout() {
			return closeTimeout;
		}

		@Override
		public int getDelayStart() {
			return 0;
		}
	}

	static class FakeConfigRepository implements ISchedulingConfigRepository {
		volatile boolean cancel = false;
		volatile boolean doubleClickCancel = false;
		final List<String> newItemCodes = Collections.synchronizedList(new ArrayList<>());

		@Override
		public boolean getCancel(AbstractSchedulingCronContextHolder abstractSchedulingCronContextHolder) {
			return cancel;
		}

		@Override
		public ISchedulingItemContext newItem(ISchedulingItemContext schedulingItemContext) {
			newItemCodes.add(schedulingItemContext.getCode());
			return schedulingItemContext;
		}

		@Override
		public boolean isDoubleClickOneRunningCancel() {
			return doubleClickCancel;
		}
	}

	static class FakeCron implements IScheduingCron {
		final String code;
		final Class<? extends IScheduingTask> pClass;
		final String cron;
		String name;
		boolean actived = true;

		FakeCron(String code, Class<? extends IScheduingTask> pClass, String cron) {
			this.code = code;
			this.pClass = pClass;
			this.cron = cron;
			this.name = code;
		}

		@Override
		public String getCode() {
			return code;
		}

		@Override
		public Class<? extends IScheduingTask> getPClass() {
			return pClass;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public boolean isActived() {
			return actived;
		}

		@Override
		public String getCon() {
			return cron;
		}
	}

	static class FakeDelay implements IScheduingDelay {
		final String code;
		final Class<? extends IScheduingTask> pClass;
		final String cron;
		String name;
		boolean actived = true;

		FakeDelay(String code, Class<? extends IScheduingTask> pClass, String cron) {
			this.code = code;
			this.pClass = pClass;
			this.cron = cron;
			this.name = code;
		}

		@Override
		public String getCode() {
			return code;
		}

		@Override
		public Class<? extends IScheduingTask> getPClass() {
			return pClass;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public boolean isActived() {
			return actived;
		}

		@Override
		public String getCron() {
			return cron;
		}
	}

	/**
	 * 可控制的測試任務：記錄執行次數，可選擇阻塞至 release、拋出例外
	 */
	static class RecordingTask implements IScheduingTask {
		final AtomicInteger executions = new AtomicInteger();
		final CountDownLatch started = new CountDownLatch(1);
		volatile CountDownLatch release;
		volatile RuntimeException failure;
		volatile boolean interrupted;
		/** 模擬不理會中斷的任務，被中斷後繼續等待 release */
		volatile boolean ignoreInterrupt;
		volatile boolean runningAfterWait = true;

		@Override
		public void execute(IScheduingTaskContext context) {
			executions.incrementAndGet();
			started.countDown();
			CountDownLatch latch = release;
			if (latch != null) {
				while (true) {
					try {
						latch.await(20, TimeUnit.SECONDS);
						break;
					} catch (InterruptedException e) {
						interrupted = true;
						if (!ignoreInterrupt) {
							break;
						}
					}
				}
				runningAfterWait = context.isRunning();
			}
			if (failure != null) {
				throw failure;
			}
		}

		void blockUntilReleased() {
			release = new CountDownLatch(1);
		}

		void release() {
			CountDownLatch latch = release;
			if (latch != null) {
				latch.countDown();
			}
		}

		void awaitStarted() throws InterruptedException {
			assertThat(started.await(5, TimeUnit.SECONDS)).as("任務未在時間內開始").isTrue();
		}
	}

	static class TaskA extends RecordingTask {
	}

	static class TaskB extends RecordingTask {
	}

	static class TaskC extends RecordingTask {
	}
}
