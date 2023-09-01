package io.github.h800572003.scheduling;

import io.github.h800572003.exception.ApBusinessException;
import io.github.h800572003.scheduling.SpringSchedulingManager.SpringSchedulingHook.BalankSpringSchedulingHook;
import io.github.h800572003.utils.HostNameUtls;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class SpringSchedulingManager implements ISchedulingManager {

    final Map<String, ISchedulingItemContext> map = new ConcurrentHashMap<>();
    final List<ISchedulingItemContext> list = Collections.synchronizedList(new ArrayList<>());

    private final ISchedulingRepository repository;
    private final ApplicationContext applicationContext;
    private final IScheduingMonitor myScheduingMonitors;

    private ScheduledTaskRegistrar taskRegistrar;

    private int extractThreadSize = 2;

    private SpringSchedulingHook springSchedulingHook = new BalankSpringSchedulingHook();
    private final ISpringSchedulingProperites springSchedulingProperites;

    class SchedulingContext implements ISchedulingContext {

        @Override
        public boolean isExecuter() {
            return springSchedulingProperites.isExecute();
        }

        @Override
        public String getHostName() {
            return HostNameUtls.getHostName().toUpperCase();
        }

        @Override
        public List<ISchedulingItemContext> getAll() {
            final List<ISchedulingItemContext> tasks = SpringSchedulingManager.this.getTasks();
            tasks.sort((s1, s2) -> s1.getCode().compareTo(s2.getCode()));
            return tasks;
        }

        @Override
        public boolean isStart() {
            return SpringSchedulingManager.this.taskRegistrar != null;
        }

        @Override
        public int getRunningCount() {
            return SpringSchedulingManager.this.getTasks().stream().mapToInt(i -> {
                boolean equals = i.getStatus().equals(SchedulingStatusCodes.RUNNNIG.name());
                return equals ? 1 : 0;
            }).reduce(0, Integer::sum);
        }

        @Override
        public ISpringSchedulingProperites getProperites() {
            return springSchedulingProperites;
        }
    }

    public interface ISpringSchedulingProperites {
        boolean isExecute();

        /**
         * down 時間
         *
         * @return
         */
        default int getCloseTimeout() {
            return 60 * 30;
        }

        /**
         *
         * @return
         */
        default int getShutdownCloseTimeout() {
            return getCloseTimeout();
        }

        /**
         * 延遲啟動
         *
         * @return
         */
        default int getDelayStart() {
            return 10;
        }
    }

    public interface SpringSchedulingHook {
        void downHook();

        void upHook();

        public static class BalankSpringSchedulingHook implements SpringSchedulingHook {

            @Override
            public void downHook() {
                // 不執行額外行為
            }

            @Override
            public void upHook() {
                // 不執行額外行為
            }

        }
    }

    private ThreadPoolTaskScheduler scheduler;
    private boolean lock = false;
    private Byte[] lockKey = new Byte[]{};
    private CustomizableThreadFactory customizableThreadFactory = new CustomizableThreadFactory("my-task-scheduler") {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        public Thread newThread(Runnable runnable) {
            Thread thread = super.newThread(runnable);
            SpringSchedulingManager.this.threads.add(thread);

            return thread;
        }

        ;
    };

    private List<Thread> threads = new ArrayList<>();

    public void add(IScheduingCron code) {
        if (this.map.containsKey(code.getCode())) {
            throw new ApBusinessException("任務代碼重複{0}", code.getCode());
        }
        log.info("add task:{} name:{} cron:({})", code.getCode(), code.getName(), code.getCon());
        this.add(code, new SchedulingCronContextHolderDTO(code, scheduler, applicationContext.getBean(code.getPClass()),
                myScheduingMonitors, repository, getContext()));
    }

    protected void add(IScheduingCron code, ISchedulingItemContext schedulingItemContext) {
        this.map.put(code.getCode(), schedulingItemContext);
    }

    public void add(IScheduingDelay code) {
        if (this.map.containsKey(code.getCode())) {
            throw new ApBusinessException("任務代碼重複{0}", code.getCode());
        }
        log.info("add task:{} name:{} cron:({})", code.getCode(), code.getName(), code.getCron());
        this.add(code, new SchedulingDelayContextHolderDTO(code, scheduler,
                applicationContext.getBean(code.getPClass()), myScheduingMonitors, repository, getContext()));
    }

    protected void add(IScheduingDelay code, ISchedulingItemContext schedulingItemContext) {
        this.map.put(code.getCode(), schedulingItemContext);
    }

    public void propertiesChange() {
        init();
    }

    private ThreadPoolTaskScheduler initScheduler(int size) {
        final ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler() {
            private static final long serialVersionUID = -1L;

            @Override
            public void destroy() {
                this.getScheduledThreadPoolExecutor().setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
                super.destroy();
            }
        };
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(30);
        scheduler.setPoolSize(size);
        scheduler.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        scheduler.setThreadFactory(customizableThreadFactory);

        scheduler.initialize();
        return scheduler;
    }

    @Override
    public final void refresh(String code) {
        busy();
        init(code).refresh();

    }

    @Override
    public final void start(String code) {
        busy();
        init(code).start();

    }

    @Override
    public final void runOnce(String code) {
        busy();
        init(code).runOnce();

    }

    @Override
    public final void cancel(String code) {
        busy();
        init(code).cancel();
    }

    @Override
    public final void cancelAll() {
        busy();
        this.map.values().forEach(item -> item.cancel());
    }

    @Override
    public final void startAll() {
        busy();
        this.map.values().forEach(item -> item.start());
    }

    public List<ISchedulingItemContext> getTasks() {
        return this.list;
    }

    @Override
    public final void down() {
        busy();
        synchronized (this) {
            log.info("down start..");
            try {
                this.lock = true;
                if (this.taskRegistrar == null) {
                    log.info("服務已關閉");
                    throw new ApBusinessException("服務已關閉");
                }

                this.map.values().forEach(item -> item.cancel());// 通知關閉
                this.map.values().forEach(item -> {
                    try {
                        item.destroy();
                    } catch (final ApBusinessException e) {
                        log.error("強制關閉忽略警告 error:", e);
                    }

                });//
                this.scheduler.setAwaitTerminationSeconds(springSchedulingProperites.getCloseTimeout());
                this.scheduler.destroy();
                this.scheduler = null;
                this.taskRegistrar.destroy();
                this.taskRegistrar = null;
                this.springSchedulingHook.downHook();
            } finally {
                this.lock = false;
                log.info("down end..");
            }

        }

    }

    @Override
    public void shutdown() {
        busy();
        synchronized (this) {
            log.info("down start..");
            try {
                this.lock = true;
                if (this.taskRegistrar == null) {
                    log.info("服務已關閉");
                    throw new ApBusinessException("服務已關閉");
                }
                this.scheduler.setAwaitTerminationSeconds(springSchedulingProperites.getShutdownCloseTimeout());
                this.scheduler.shutdown();
                this.map.values().forEach(item -> item.cancel());// 通知關閉
                this.map.values().forEach(item -> {
                    try {
                        item.destroy();
                    } catch (final ApBusinessException e) {
                        log.error("強制關閉忽略警告 error:", e);
                    }
                });//
                this.scheduler = null;
                this.taskRegistrar.destroy();
                this.taskRegistrar = null;
                this.springSchedulingHook.downHook();
            } finally {
                this.lock = false;
                log.info("down end..");
            }

        }

    }

    private void init() {
        this.map.clear();
        this.list.clear();
        final List<IScheduingDelay> scheduingDelays = this.repository.getDelayTask();
        final List<IScheduingCron> scheduingCrons = this.repository.getCcheduingCronsTask();

        this.taskRegistrar = new ScheduledTaskRegistrar();
        final int size = scheduingDelays.size() + scheduingCrons.size() + this.extractThreadSize;
        log.info(" Scheduling execute thread size:{}", size);
        final ThreadPoolTaskScheduler scheduler = initScheduler(size + 2);
        this.scheduler = scheduler;
        this.taskRegistrar.setTaskScheduler(scheduler);

        scheduingDelays.forEach(this::add);
        scheduingCrons.forEach(this::add);

        this.list.addAll(this.map.values());
    }

    @Override
    public final void up() {
        busy();
        synchronized (this) {
            log.info("up start..");
            try {
                if (this.taskRegistrar != null) {
                    log.info("服務已啟動");
                    throw new ApBusinessException("服務已啟動");
                }
                long count = this.threads.stream().filter(Thread::isAlive).count();
                if (count > 0) {
                    throw new ApBusinessException("任務尚未完全停止，請稍後");
                }
                this.taskRegistrar = new ScheduledTaskRegistrar();
                this.init();
                springSchedulingHook.upHook();
            } finally {
                log.info("up end..");
            }
        }
        this.startAll();

    }


    private void busy() {
        synchronized (lockKey) {
            if (this.lock) {
                throw new ApBusinessException("服務忙碌中請稍後操作");
            }
        }
    }

    private ISchedulingItemContext init(String code) {
        final ISchedulingItemContext orDefault = this.map.getOrDefault(code, null);
        if (orDefault == null) {
            throw new ApBusinessException("任務不存在{0}", code);
        }
        return orDefault;
    }

    public void setSpringSchedulingHook(SpringSchedulingHook springSchedulingHook) {
        this.springSchedulingHook = springSchedulingHook;
    }

    @Override
    public ISchedulingContext getContext() {
        return new SchedulingContext();
    }

}
