package io.github.h800572003.scheduling;


import io.github.h800572003.exception.ApBusinessException;
import io.github.h800572003.scheduling.SpringSchedulingManager.ISpringSchedulingProperites;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@Slf4j
@Disabled
@IndicativeSentencesGeneration
class SpringSchedulingManagerTest {

    public static final int SIZE = 2;
    public static final int CLOSE_TIMEOUT = 10;


    private final ISchedulingRepository schedulingRepository = Mockito.mock(ISchedulingRepository.class);
    private final ApplicationContext applicationContext = Mockito.mock(ApplicationContext.class);
    private final MyScheduingMonitors myScheduingMonitors = Mockito.spy(new MyScheduingMonitors());
    private final ISpringSchedulingProperites springSchedulingProperties = Mockito.mock(ISpringSchedulingProperites.class);

    private ISchedulingManager springSchedulingManager;


    private final TestTasks testTasks = new TestTasks(SIZE);

    @BeforeEach
    public void before() {
        springSchedulingManager = SchedulingManagers.createSchedulingManager(schedulingRepository, applicationContext, myScheduingMonitors, springSchedulingProperties);
        setScheduling();
        setTaskConfig();
    }

    @AfterEach
    public void after() {

        log.info("after .......");
        try {
            springSchedulingManager.down();
        } catch (Exception e) {
            log.error("after ", e);
        }
    }


    private void setScheduling() {
        Mockito.when(springSchedulingProperties.isExecute()).thenReturn(true);
        Mockito.when(springSchedulingProperties.getCloseTimeout()).thenReturn(CLOSE_TIMEOUT);
        Mockito.when(springSchedulingProperties.getShutdownCloseTimeout()).thenReturn(CLOSE_TIMEOUT);
        Mockito.when(springSchedulingProperties.getDelayStart()).thenReturn(0);
    }

    private void setTaskConfig() {


        Mockito.when(schedulingRepository.getCcheduingCronsTask()).thenReturn(new ArrayList<>(testTasks.getTasks()));

        Mockito.when(applicationContext.getBean(LongTimeLoopTask.class)).thenReturn(testTasks.getTask(LongTimeLoopTask.class));
        Mockito.when(applicationContext.getBean(LongTimeTask.class)).thenReturn(testTasks.getTask(LongTimeTask.class));
    }


    /**
     * 服務停止
     * 1.等待時間
     * 2.執行停機
     * 3.確認作業發生中斷，
     */
    @Test
    @DisplayName("服務停止")
    void testDown() {
        //GIVE
        up();

        //wait 等到功能都開始執行
        testTasks.getStatusCache().await(Status.RUNNING);


        //WHEN
        springSchedulingManager.down();

        //wait all ok
        testTasks.getStatusCache().await(Status.FIN);
        showMessage();

        //THEN
        assertThat(testTasks.getTasks()).filteredOn(BaseLatchTask::isDone).hasSize(SIZE);

        log.info("end");
    }


    @Test
    @DisplayName("測試重複啟動")
    void testUp() {


        Assertions.assertThrows(ApBusinessException.class, () -> {
            up();
            //WHEN
            springSchedulingManager.up();
        }).getMessage().contains("服務已啟動");


    }


    @Test
    @DisplayName("關閉任務後，直接啟動任務")
    void testWhenDownAndStartTask() {


        Assertions.assertThrows(ApBusinessException.class, () -> {
            up();

            springSchedulingManager.down(); //當服務結束後

            testTasks.getStatusCache().await(Status.FIN);

            //WHEN
            springSchedulingManager.start(LongTimeLoopTask.class.getSimpleName()); //再度啟動特定任務
        }).getMessage().contains("已關機不提供作業");


    }

    //    @Test
    @DisplayName("關閉任務後[特定]任務，再度啟動[特定]，執行完[特定任務]業進度達100%")
    void test_cancel_special_then_start() {


        final LongTimeLoopTask task = testTasks.getTask(LongTimeLoopTask.class);
        task.refreshLatchSize(1);
        //GIVE
        up();


        final String taskCode = LongTimeLoopTask.class.getSimpleName();//特定任務

        task.awaitSelfLatch();
        //WHEN
        springSchedulingManager.cancel(taskCode);//中斷任務
        task.refreshLatchSize(1);

        springSchedulingManager.start(taskCode); //再度啟動特定任務
        task.awaitSelfLatch();


        showMessage();

        log.info("task:{}", task.isDoneOk());


        assertThat(task.isDone()).isTrue();
        assertThat(task.isDoneOk()).isTrue();
        assertThat(task.getOkTimes()).isEqualTo(2);


    }


    @DisplayName("當任務執行中當服務中斷後，進行重整資訊")
    @Test
    void testFreshWhenCancel() {

        springSchedulingManager.propertiesChange();


        final String taskCode = LongTimeLoopTask.class.getSimpleName();//特定任務


        springSchedulingManager.refresh(taskCode);

        Mockito.verify(schedulingRepository, Mockito.times(1)).getCcheduingCronsTask(taskCode);

    }

    @DisplayName("當任務執行中啟動服務，重整排程資訊")
    @Test
    void testFreshWhenUp() {
        //give
        //1.啟動服務
        up();

        try {

            //when 重新整理該任務資料
            this.springSchedulingManager//
                    .refresh(LongTimeLoopTask.class.getSimpleName());

            fail("should exception");
        } catch (ApBusinessException e) {

            //then
            assertThat(e).hasMessageContaining("請先關閉");
        } catch (Exception e) {
            fail("not including exception");
        }


    }


    @DisplayName("服務啟動，呼叫執行一次")
    @Test
    void testOnce() {

        //give
        springSchedulingManager.propertiesChange();

        final int doneTimes = 4;   //設定完成次數
        final LongTimeLoopTask task = testTasks.getTask(LongTimeLoopTask.class);
        task.refreshLatchSize(doneTimes);//

        this.springSchedulingManager.startAll();

        final int updateTimes = 1;//服務啟動執行次數
        IntStream.range(0, doneTimes - updateTimes)
                .forEach(this::once);
        task.awaitSelfLatch();


        assertThat(testTasks.getTask(LongTimeLoopTask.class).isDoneOk()).isTrue();
    }

    @DisplayName("服務啟動後呼叫shutdown，任務時間於等待時間內完成")
    @Test
    @Timeout(unit = TimeUnit.SECONDS, value = 2)
    void testShutdown() {

        LongTimeLoopTask task = testTasks.getTask(LongTimeLoopTask.class);
        task.setMilliSeconds(CLOSE_TIMEOUT / 100 * 1000 < 0 ? 1 : CLOSE_TIMEOUT / 100 * 1000);
        this.up();
        testTasks.getStatusCache().await(Status.RUNNING);
        springSchedulingManager.shutdown();

        log.info("Shutdown");
        springSchedulingManager.getContext().getAll().forEach(i -> {
            log.info("Code:{},StatusL:{},Progress:{}", i.getCode(), i.getStatus(), i.getProgress());
        });

        Assertions.assertFalse(springSchedulingManager.getContext().isStart());
        Assertions.assertEquals(0, springSchedulingManager.getContext().getRunningCount());


        Assertions.assertAll(
                "all ok done",
                () -> Assertions.assertTrue(testTasks.getTask(LongTimeLoopTask.class).isDoneOk()),
                () -> Assertions.assertTrue(testTasks.getTask(LongTimeTask.class).isDoneOk())
        );
        springSchedulingManager.getContext().getAll().forEach(i -> {
            Assertions.assertEquals(i.getStatus(), SchedulingStatusCodes.DEAD.name());
        });

    }

    @DisplayName("服務啟動後呼叫shutdown，任務項目執行時間，超過等待時間，超過時間呼叫中斷服務")
    @Test
    void testShutdownWhenWaitOverWaitTime() {

        LongTimeLoopTask task = testTasks.getTask(LongTimeLoopTask.class);
        task.setMilliSeconds(CLOSE_TIMEOUT / 2 * 1000);

        this.up();
        testTasks.getStatusCache().await(Status.RUNNING);
        springSchedulingManager.shutdown();

        log.info("Shutdown");
        springSchedulingManager.getContext().getAll().forEach(i -> {
            log.info("Code:{},StatusL:{},Progress:{}", i.getCode(), i.getStatus(), i.getProgress());
        });

        Assertions.assertFalse(springSchedulingManager.getContext().isStart());
        Assertions.assertEquals(0, springSchedulingManager.getContext().getRunningCount());


        Assertions.assertAll(
                "all ok done",
                () -> Assertions.assertTrue(testTasks.getTask(LongTimeLoopTask.class).isDone()),
                () -> Assertions.assertTrue(testTasks.getTask(LongTimeTask.class).isDoneOk())
        );
        springSchedulingManager.getContext().getAll().forEach(i -> {
            Assertions.assertEquals(i.getStatus(), SchedulingStatusCodes.DEAD.name());
        });


    }


    private void showMessage() {
        springSchedulingManager.getContext().getAll()
                .forEach(i -> log.info("code:{},progress:{},status:{} ", i.getCode(), i.getProgress(), i.getStatus()));
    }

    private void up() {
        springSchedulingManager.propertiesChange();
        springSchedulingManager.startAll();
    }


    private void once(int i) {
        log.info("runOnce time :{}", i);
        this.springSchedulingManager.runOnce(LongTimeLoopTask.class.getSimpleName());
    }
}
