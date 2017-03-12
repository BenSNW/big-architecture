package hx.jdk.forkjoin;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.stream.Collectors;

/**
 * @see http://howtodoinjava.com/java-7/forkjoin-framework-tutorial-forkjoinpool-example/
 * @see http://coopsoft.com/ar/CalamityArticle.html#references
 *
 * <p></>Created by Benchun on 1/6/17
 */
public class ForkJoinDemo {

    public static void main(String[] args) {

        ForkJoinPool forkJoinPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());

        System.out.println(forkJoinPool.invoke(new SubRecursiveTask(10)));

        System.out.println(forkJoinPool.invoke(new SubForkJoinTask(10)));

        System.out.println(new FibonacciForkJoinTask().getGenericType());
    }

    static class SubRecursiveTask extends RecursiveTask<Integer> {

        int n;

        SubRecursiveTask(int n) { this.n = n;}

        @Override
        protected Integer compute() {
            System.out.println(n);
            if (n < 2)
                return n;
            return new SubRecursiveTask(n-1).compute() + new SubRecursiveTask(n-2).compute();
        }
    }

    static class SubForkJoinTask extends RecursiveTask<Integer> {

        int n;

        SubForkJoinTask(int n) { this.n = n;}

        @Override
        protected Integer compute() {
            System.out.println(n);
            if (n < 2) return n;
            List<RecursiveTask<Integer>> forkJoinTasks = Arrays.asList(
                    new SubForkJoinTask(n-1), new SubForkJoinTask(n-2));

            forkJoinTasks.stream().forEach(ForkJoinTask::fork);

            return forkJoinTasks.stream().collect(Collectors.summingInt(ForkJoinTask::join));
        }
    }

    // bad design, diffrent UniqueForkJoinTask<T> will share global task id,
    // better to use Composite to hold all tasks instead of Inheritance
    static abstract class UniqueForkJoinTask<T> extends RecursiveTask<T> {

        static AtomicInteger globalId = new AtomicInteger(0);
        final int uniqueId;

        static AtomicIntegerArray tasks;
        static volatile boolean initialized;

        T dummyResult = null;

        protected UniqueForkJoinTask() {
            uniqueId = globalId.incrementAndGet();
            if (!initialized)
                tasks = new AtomicIntegerArray(1024);
        }

        public static void initialize(int maxTaskNum) {
            if (maxTaskNum < 0 || maxTaskNum > 100000)
                throw new IllegalArgumentException();
            initialized = true;
            tasks = new AtomicIntegerArray(maxTaskNum);
        }

        public void setDummyResult(T dummyResult) {
            this.dummyResult = dummyResult;
        }

        private T getDummyResult() {
            if (dummyResult != null)
                return dummyResult;
            if (getGenericType() == String.class)
                return (T) "";
            return null;
        }

        // see hx.util.RttiUtil
        protected Class<?> getGenericType() {
            System.out.println(Arrays.toString(getClass().getInterfaces()));
            System.out.println(Arrays.toString(getClass().getGenericInterfaces()));

            System.out.println(getClass().getGenericSuperclass().getTypeName());
            System.out.println(getClass().getGenericSuperclass().getClass().getName());

            return (Class<?>) ((ParameterizedType) (getClass().getGenericSuperclass()) ).getActualTypeArguments()[0];
        }

        @Override
        protected T compute() {
            return getDummyResult();
        }

        protected abstract T doCompute();

    }

    static class FibonacciForkJoinTask extends UniqueForkJoinTask<Integer> {

        @Override
        protected Integer doCompute() {
            return 1;
        }
    }

    static class ForkJoinTaskManager<T> {
        AtomicInteger globalId = new AtomicInteger(0);

        static AtomicIntegerArray tasks;
        static volatile boolean initialized;

        T dummyResult = null;
    }

}
