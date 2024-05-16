package io.github.h800572003.scheduling;

import io.github.h800572003.exception.CancelExecpetion;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SchedulingProgressTest {


    @Test
    public void testZeroTotal() {
        TestingSchedulingProgress target = new TestingSchedulingProgress();
        Assertions.assertEquals("0/0", target.getMessage());
        Assertions.assertEquals(0, target.getProgress());
    }

    @Test
    public void test1_1() {
        TestingSchedulingProgress target = new TestingSchedulingProgress();
        target.setTotal(1);
        target.incrementCounter();
        Assertions.assertEquals("1/1", target.getMessage());
        Assertions.assertEquals(100, target.getProgress());
    }

    @Test
    public void test1_2() {
        TestingSchedulingProgress target = new TestingSchedulingProgress();
        target.setTotal(2);
        target.incrementCounter();
        Assertions.assertEquals("1/2", target.getMessage());
        Assertions.assertEquals(50, target.getProgress());
    }

    @Test
    public void test3_4() {
        TestingSchedulingProgress target = new TestingSchedulingProgress();
        target.setTotal(4);
        target.incrementCounter();
        target.incrementCounter();
        target.incrementCounter();
        Assertions.assertEquals("3/4", target.getMessage());
        Assertions.assertEquals(75, target.getProgress());
    }

    @Test
    public void test3_2() {
        TestingSchedulingProgress target = new TestingSchedulingProgress();
        target.setTotal(2);
        target.incrementCounter();
        target.incrementCounter();
        target.incrementCounter();
        Assertions.assertEquals("3/2", target.getMessage());
        Assertions.assertEquals(100, target.getProgress());
    }

    @Test
    public void test_rest() {
        TestingSchedulingProgress target = new TestingSchedulingProgress();
        target.setTotal(2);
        target.incrementCounter();
        target.incrementCounter();
        target.incrementCounter();
        target.reset();
        Assertions.assertEquals("0/0", target.getMessage());
        Assertions.assertEquals(0, target.getProgress());
    }

    static class TestingSchedulingProgress {

        private final SchedulingProgress schedulingProgress;
        private final FakeIScheduingTaskContext scheduingTaskContext;

        TestingSchedulingProgress() {
            this.scheduingTaskContext = new FakeIScheduingTaskContext();
            this.schedulingProgress = new SchedulingProgress(scheduingTaskContext);
        }

        public String getMessage() {
            return scheduingTaskContext.message;

        }

        public int getProgress() {
            return scheduingTaskContext.progress;
        }

        public void setTotal(int i) {
            schedulingProgress.setTotal(i);
        }

        public void incrementCounter() {
            schedulingProgress.incrementCounter();
        }

        public void reset() {
            schedulingProgress.reset();
        }
    }

    static class FakeIScheduingTaskContext implements IScheduingTaskContext {

        private String message;
        private int progress;

        public FakeIScheduingTaskContext() {
        }

        @Override
        public void checkUp() throws CancelExecpetion {

        }

        @Override
        public void updateMessage(String message) {
            this.message = message;
        }

        @Override
        public void setProgress(int progress) {
            this.progress = progress;
        }

        @Override
        public ISchedulingProgress getSchedulingProgress() {
            return null;
        }

        @Override
        public <T> T getService(Class<T> pclass) {
            return null;
        }
    }
}