package hx.dist.zk.demo;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;

public class ZKConnectionWatcher implements Watcher {

	private static final int SESSION_TIMEOUT = 5000;
	private CountDownLatch connectedSignal = new CountDownLatch(1);
	protected ZooKeeper zk;
	
	public void connect(String server) throws IOException, InterruptedException {
		zk = new ZooKeeper(server, SESSION_TIMEOUT, this);
		connectedSignal.await();
	}

	@Override
	public void process(WatchedEvent event) {
		if (event.getState() == KeeperState.SyncConnected) {
			connectedSignal.countDown();
		}
	}

	public void close() throws InterruptedException {
		zk.close();
	}

}
