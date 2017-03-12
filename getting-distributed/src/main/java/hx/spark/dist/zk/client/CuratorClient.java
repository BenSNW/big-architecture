package hx.spark.dist.zk.client;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.zookeeper.CreateMode;

public class CuratorClient {

	protected final CuratorFramework client;

	public CuratorClient(String host, RetryPolicy policy) {
		client = CuratorFrameworkFactory.newClient(host, policy);
	}

	
	public CuratorClient(String host, int sTimeout, int cTimeout, RetryPolicy policy) {
		client = CuratorFrameworkFactory.newClient(host, sTimeout, cTimeout, policy);
	}
	
	public void createPersistent(String path) throws Exception {		
		client.create().withMode(CreateMode.PERSISTENT).forPath(path);		
	}
	
}
