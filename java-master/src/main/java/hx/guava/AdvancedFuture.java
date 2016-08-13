package hx.guava;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

/**
 * https://github.com/google/guava/wiki/ListenableFutureExplained
 * 
 * <li>A traditional Future represents the result of an asynchronous computation:
 * a computation that may or may not have finished producing a result yet.
 * A Future can be a handle to an in-progress computation or a promise from
 * a service to supply us with a result.
 * 
 * <li>A ListenableFuture allows you to register callbacks to be executed
 * once the computation is complete, or if the computation is already complete,
 * immediately. This simple addition makes it possible to efficiently support
 * many operations that the basic Future interface cannot support.

 * @author Created by BenSNW on Jun 25, 2016
 *
 * @see java.util.concurrent.CompletableFuture
 */
public class AdvancedFuture {

	public static void main(String[] args) {
		ExecutorService executor = Executors.newFixedThreadPool(10);
		ListeningExecutorService service = MoreExecutors.listeningDecorator(executor);
		ListenableFuture<Integer> explosion = service
				.submit(new Callable<Integer>() {
					public Integer call() {
						return 555;
					}
				});
		Futures.addCallback(explosion, new FutureCallback<Integer>() {
			
			public void onSuccess(Integer explosion) {

			}

			public void onFailure(Throwable thrown) {

			}
		});
	}
}
