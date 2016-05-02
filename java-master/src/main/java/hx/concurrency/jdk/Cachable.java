package hx.concurrency.jdk;

public interface Cachable<K, V> {

	/**
	 * A time-consuming (thus InterruptedException may be thrown during its process) task to be cached
	 * @param key
	 * @return
	 * @throws InterruptedException
	 */
	V get(K key) throws InterruptedException;
}
