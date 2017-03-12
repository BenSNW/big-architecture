package hx.jdk.advanced;

import rx.Observable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * @see http://codingjunkie.net/completable-futures-part1/
 * @see https://github.com/bbejeck/Java-8/blob/a6fd55fffe856456fc5f3e6945ed15cc1a06a9c8/src/test/java/bbejeck/concurrent/CompletableFutureTest.java
 * @see http://www.deadcoderising.com/java8-writing-asynchronous-code-with-completablefuture/
 *
 * Composing Tasks: Composing means taking the results of one successful CompletableFuture as input to another CompletableFuture via a Function
 * Combining Tasks: Combining is accomplished by taking 2 successful CompletionFutures and having the results from both used as parameters to a BiFunction to produce another CompletableFuture
 *
 * <p>Created by Benchun on 1/6/17
 */
public class BrightFuture {

    public static void main(String[] args) {

        CompletableFuture<Integer> result = new CompletableFuture<>();
        AtomicInteger count = new AtomicInteger(0);
        Observable.just("1", "2", "3", "err", "4").subscribe(
                ev -> {
                    try {
                        int x = Integer.parseInt(ev);
                        count.addAndGet(x);
                    } catch (NumberFormatException e) { }
                },
                throwable -> result.complete(0),
                () -> {
                    try {
                        //simulate io delay
                        Thread.sleep(3000);
                    } catch (InterruptedException e) { }
                    result.complete(count.get());
                }
        );

        result.thenApply(x -> "event count: " + x).thenAcceptAsync(System.out::println);

    }

    public void compareSuppliers() {
        Supplier<String> supplier = () -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "simple String supplier";
        };

        ExceptionalSupplier<String> throwingSupplier = () -> {
            Thread.sleep(1000);
            return "throwing String supplier";
        };
    }

    /**
     * wrap the Supplier to allowing throwing any Excetion in lambda
     *
     * @param <T>
     */
    @FunctionalInterface
    interface ExceptionalSupplier<T> extends Supplier<T> {

        default T get() {
            try {
                return getWithException();
            } catch (Exception ex) {
                throw new RuntimeException(ex.getMessage(), ex);
            }
        }

        T getWithException() throws Exception;
    }
}
