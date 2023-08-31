package io.github.h800572003.scheduling;


import io.github.h800572003.exception.ApBusinessException;
import io.github.h800572003.scheduling.SpringSchedulingManager.ISpringSchedulingProperites;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@Slf4j
@Disabled
class SpringSchedulingManagerTest {

    public static final int SIZE = 2;
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

        try {
            springSchedulingManager.down();
        } catch (Exception e) {
            log.error("after ", e);
        }
    }


    private void setScheduling() {
        Mockito.when(springSchedulingProperties.isExecute()).thenReturn(true);
        Mockito.when(springSchedulingProperties.getCloseTimeout()).thenReturn(180);
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
        try {
            //GIVE
            up();

            //WHEN
            springSchedulingManager.up();
            fail("should exception");
        } catch (ApBusinessException e) {

            //THEN
            assertThat(e).hasMessage("服務已啟動");
        }

    }

    @Test
    @DisplayName("關閉任務後，直接啟動任務")
    void testWhenDownAndStartTask() {

        try {
            //GIVE
            up();

            springSchedulingManager.down(); //當服務結束後

            testTasks.getStatusCache().await(Status.FIN);

            //WHEN
            springSchedulingManager.start(LongTimeLoopTask.class.getSimpleName()); //再度啟動特定任務
            //GIVE
            fail("should exception");
        } catch (ApBusinessException e) {
            //THEN
            assertThat(e).hasMessageContaining("已關機不提供作業");
        }
    }

    @Test
    @DisplayName("關閉任務後[特定]任務，再度啟動[特定]，執行完[特定任務]業進度達100%")
    void test_cancel_special_then_start() {

        //GIVE
        up();


        final String taskCode = LongTimeLoopTask.class.getSimpleName();//特定任務

        springSchedulingManager.cancel(taskCode);//中斷任務

        log.info("first 1 waiting");
        testTasks.getStatusCache().await(Status.FIN);

        //WHEN
        springSchedulingManager.start(taskCode); //再度啟動特定任務
        //GIVE
        log.info("first 2 waiting");
        testTasks.getTask(LongTimeLoopTask.class).awaitSelf();


        LongTimeLoopTask task = testTasks.getTask(LongTimeLoopTask.class);
        log.info("task:{}", task.isDoneOk());

        showMessage();
        assertThat(task.isDone()).isTrue();
    }


    @DisplayName("當任務執行中當服務中斷後，進行重整資訊")
    @Test
    void testFreshWhenCancel() {
        String taskCode = LongTimeLoopTask.class.getSimpleName();//特定任務
        up();


        springSchedulingManager.cancel(taskCode);//中斷任務

        testTasks.getStatusCache().await(Status.FIN);

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
        }


    }

    @DisplayName("當任務執行中啟動服務，重整排程資訊")
    @Test
    void testOnce() {
        springSchedulingManager.propertiesChange();

        this.springSchedulingManager//
                .runOnce(LongTimeLoopTask.class.getSimpleName());

        this.springSchedulingManager//
                .runOnce(LongTimeLoopTask.class.getSimpleName());

        LongTimeLoopTask task = testTasks.getTask(LongTimeLoopTask.class);
        task.awaitSelf();
        task.awaitSelf();


        assertThat(testTasks.getTask(LongTimeLoopTask.class).isDoneOk()).isTrue();
    }


    private void showMessage() {
        springSchedulingManager.getContext().getAll()
                .forEach(i -> log.info("code:{},progress:{},status:{} ", i.getCode(), i.getProgress(), i.getStatus()));
    }

    private void up() {
        springSchedulingManager.propertiesChange();
        springSchedulingManager.startAll();
    }


}
