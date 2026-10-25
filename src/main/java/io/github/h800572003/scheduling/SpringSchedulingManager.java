package io.github.h800572003.scheduling;

import io.github.h800572003.exception.ApBusinessException;
import io.github.h800572003.scheduling.ISchedulingShutdownPolicy.BlankSchedulingShutdownPolicy;
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
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class SpringSchedulingManager implements ISchedulingManager {

    /**
     * 等待執行緒池 worker thread 結束的時間(ms)
     */
    private static final long THREAD_EXIT_TIMEOUT = TimeUnit.SECONDS.toMillis(1);

    final Map<String, ISchedulingItemContext> map = new ConcurrentHashMap<>();

    private final ISchedulingRepository repository;
    private final ApplicationContext applicationContext;
    private final IScheduingMonitor myScheduingMonitors;

    private ScheduledTaskRegistrar taskRegistrar;

    private int extractThreadSize = 2;


    private SpringSchedulingHook springSchedulingHook = new BalankSpringSchedulingHook();
    private ISchedulingShutdownPolicy shutdownPolicy = new BlankSchedulingShutdownPolicy();
    private final ISpringSchedulingProperites springSchedulingProperites;

    private final ISchedulingConfigRepository configRepository;

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
            tasks.sort(Comparator.comparing(ISchedulingItemContext::getCode));
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
    private volatile boolean lock = false;
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

    private final List<Thread> threads = new CopyOnWriteArrayList<>();

    public void add(IScheduingCron code) {
        checkAddable(code);
        log.info("add task:{} name:{} cron:({})", code.getCode(), code.getName(), code.getCon());
        this.add(code, new SchedulingCronContextHolderDTO(code, scheduler, applicationContext.getBean(code.getPClass()),
                myScheduingMonitors, repository, getContext(), configRepository));
    }

    protected void add(IScheduingCron code, ISchedulingItemContext schedulingItemContext) {
        this.map.put(code.getCode(), this.configRepository.newItem(schedulingItemContext));
    }

    public void add(IScheduingDelay code) {
        checkAddable(code);
        log.info("add task:{} name:{} cron:({})", code.getCode(), code.getName(), code.getCron());
        this.add(code, new SchedulingDelayContextHolderDTO(code, scheduler,
                applicationContext.getBean(code.getPClass()), myScheduingMonitors, repository, getContext(), configRepository));
    }

    protected void add(IScheduingDelay code, ISchedulingItemContext schedulingItemContext) {
        this.map.put(code.getCode(), this.configRepository.newItem(schedulingItemContext));
    }

    private void checkAddable(IScheduingKey code) {
        if (this.scheduler == null) {
            throw new ApBusinessException("排程服務尚未初始化，無法新增任務{0}", code.getCode());
        }
        if (this.map.containsKey(code.getCode())) {
            throw new ApBusinessException("任務代碼重複{0}", code.getCode());
        }
    }

    /**
     * 重新載入任務，任務排程中或執行中不允許變更，需先關閉
     */
    @Override
    public void propertiesChange() {
        busy();
        synchronized (this) {
            final long activeCount = this.map.values().stream().filter(this::isActive).count();
            if (activeCount > 0) {
                throw new ApBusinessException("尚有{0}個任務啟動中，請先關閉後再變更參數", activeCount);
            }
            final List<IScheduingDelay> scheduingDelays = this.repository.getDelayTask();
            final List<IScheduingCron> scheduingCrons = this.repository.getCcheduingCronsTask();
            checkDuplicateCode(scheduingDelays, scheduingCrons);
            releaseScheduler();
            init(scheduingDelays, scheduingCrons);
        }
    }

    private boolean isActive(ISchedulingItemContext item) {
        final String status = item.getStatus();
        return SchedulingStatusCodes.RUNNABLE.name().equals(status)
                || SchedulingStatusCodes.RUNNNIG.name().equals(status);
    }

    private void checkDuplicateCode(List<IScheduingDelay> scheduingDelays, List<IScheduingCron> scheduingCrons) {
        final Set<String> codes = new HashSet<>();
        final List<IScheduingKey> keys = new ArrayList<>(scheduingDelays);
        keys.addAll(scheduingCrons);
        keys.forEach(key -> {
            if (!codes.add(key.getCode())) {
                throw new ApBusinessException("任務代碼重複{0}", key.getCode());
            }
        });
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
        scheduler.setAwaitTerminationSeconds(springSchedulingProperites.getCloseTimeout());
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

    /**
     * 取得任務清單快照，修改回傳清單不影響管理器
     *
     * @return 任務清單
     */
    public List<ISchedulingItemContext> getTasks() {
        return new ArrayList<>(this.map.values());
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

                this.map.values().forEach(item -> {
                    try {
                        item.cancel();// 通知關閉
                    } catch (final ApBusinessException e) {
                        log.error("強制關閉忽略警告 error:", e);
                    }
                });
                this.map.values().forEach(item -> {
                    try {
                        item.destroy();
                    } catch (final ApBusinessException e) {
                        log.error("強制關閉忽略警告 error:", e);
                    }

                });//
                final boolean terminated = releaseScheduler();
                if (terminated) {
                    awaitThreadsExit();
                }
                this.springSchedulingHook.downHook();
            } finally {
                this.lock = false;
                log.info("down end..");
            }

        }

    }

    /**
     * 關閉排程器
     *
     * @return 執行緒池是否已完全終止(未逾時)
     */
    private boolean releaseScheduler() {
        boolean terminated = true;
        if (this.scheduler != null) {
            this.scheduler.setAwaitTerminationSeconds(springSchedulingProperites.getCloseTimeout());
            this.scheduler.destroy();
            final ScheduledThreadPoolExecutor executor = this.scheduler.getScheduledThreadPoolExecutor();
            if (!executor.isTerminated()) {
                try {
                    this.shutdownPolicy.onTimeout(new SchedulingShutdownContext(executor));
                } catch (final RuntimeException e) {
                    log.error("關閉逾時處理失敗 error:", e);
                }
            }
            terminated = executor.isTerminated();
            this.scheduler = null;
        }
        if (this.taskRegistrar != null) {
            this.taskRegistrar.destroy();
            this.taskRegistrar = null;
        }
        return terminated;
    }

    /**
     * 執行緒池終止後 worker thread 仍可能短暫存活，等待其結束避免隨即 up 時誤判任務未停止
     */
    private void awaitThreadsExit() {
        for (final Thread thread : this.threads) {
            if (thread == Thread.currentThread()) {
                continue;
            }
            try {
                thread.join(THREAD_EXIT_TIMEOUT);
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        this.threads.removeIf(thread -> !thread.isAlive());
    }

    private void init(List<IScheduingDelay> scheduingDelays, List<IScheduingCron> scheduingCrons) {
        this.map.clear();

        this.taskRegistrar = new ScheduledTaskRegistrar();
        final int size = scheduingDelays.size() + scheduingCrons.size() + this.extractThreadSize;
        log.info(" Scheduling execute thread size:{}", size);
        final ThreadPoolTaskScheduler scheduler = initScheduler(size + 2);
        this.scheduler = scheduler;
        this.taskRegistrar.setTaskScheduler(scheduler);

        try {
            scheduingDelays.forEach(this::add);
            scheduingCrons.forEach(this::add);
        } catch (final RuntimeException e) {
            log.error("Scheduling init error", e);
            this.map.clear();
            releaseScheduler();
            throw e;
        }
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
                final ISchedulingContext context = getContext();
                if (!context.isExecuter()) {
                    throw new ApBusinessException("非執行者{0}無權限操作", context.getHostName());
                }
                this.threads.removeIf(thread -> !thread.isAlive());
                if (!this.threads.isEmpty()) {
                    throw new ApBusinessException("任務尚未完全停止，請稍後");
                }
                final List<IScheduingDelay> scheduingDelays = this.repository.getDelayTask();
                final List<IScheduingCron> scheduingCrons = this.repository.getCcheduingCronsTask();
                checkDuplicateCode(scheduingDelays, scheduingCrons);
                init(scheduingDelays, scheduingCrons);
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

    /**
     * 設定關閉逾時處理策略，未設定時逾時不額外處理
     *
     * @param shutdownPolicy 策略，如 {@link InterruptSchedulingShutdownPolicy}
     */
    public void setShutdownPolicy(ISchedulingShutdownPolicy shutdownPolicy) {
        this.shutdownPolicy = Objects.requireNonNull(shutdownPolicy);
    }

    class SchedulingShutdownContext implements ISchedulingShutdownContext {

        private final ScheduledThreadPoolExecutor executor;

        SchedulingShutdownContext(ScheduledThreadPoolExecutor executor) {
            this.executor = executor;
        }

        @Override
        public int getCloseTimeout() {
            return springSchedulingProperites.getCloseTimeout();
        }

        @Override
        public List<ISchedulingItemContext> getRunningTasks() {
            return SpringSchedulingManager.this.map.values().stream()
                    .filter(item -> SchedulingStatusCodes.RUNNNIG.name().equals(item.getStatus()))
                    .sorted(Comparator.comparing(ISchedulingItemContext::getCode)).collect(Collectors.toList());
        }

        @Override
        public List<Thread> getAliveThreads() {
            return SpringSchedulingManager.this.threads.stream()
                    .filter(thread -> thread.isAlive() && thread != Thread.currentThread())
                    .collect(Collectors.toList());
        }

        @Override
        public void interrupt() {
            this.executor.shutdownNow();
        }

        @Override
        public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
            return this.executor.awaitTermination(timeout, unit);
        }

        @Override
        public boolean isTerminated() {
            return this.executor.isTerminated();
        }
    }

    @Override
    public ISchedulingContext getContext() {
        return new SchedulingContext();
    }

}
