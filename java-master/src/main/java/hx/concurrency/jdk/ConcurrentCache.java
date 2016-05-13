package hx.concurrency.jdk;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * Use a FutureTask to hold the value that is to be completed later
 * to avoid two threads execute the expensive task simultaneously.
 * 
 * @author BenSNW
 *
 * @param <K>
 * @param <V>
 */
public class ConcurrentCache<K, V> implements Cachable<K, V> {

	private final Map<K, Future<V>> cache = new ConcurrentHashMap<>();
	private final Cachable<K, V> c;
	
	public ConcurrentCache(Cachable<K, V> c) {
		this.c = c;
	}
	
	@Override
	public V get(K key) throws InterruptedException {
		while (true) {
            Future<V> f = cache.get(key);
            if (f == null) {
                Callable<V> eval = new Callable<V>() {                   
                	@Override
                	public V call() throws InterruptedException {
                        // execute the task by the real worker
                		return c.get(key);
                    }
                };
                FutureTask<V> ft = new FutureTask<V>(eval);
                f = cache.putIfAbsent(key, ft);
                if (f == null) {
                    f = ft;
                    ft.run();
                }
            }
            try {
                return f.get();
            } catch (CancellationException e) {
                cache.remove(key, f);
            } catch (ExecutionException e) {
                
            }
        }
	}

}
