package hx.spark.dist.zk.demo;

import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;

public class ZKConnectionClient extends ZKConnectionWatcher {

	public void create(String groupName) throws KeeperException, InterruptedException {
		String path = "/" + groupName;
		String createdPath = zk.create(path, null /* data */,
				Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		System.out.println("Created	" + createdPath);
	}

	public void join(String groupName, String memberName) throws KeeperException, InterruptedException {
		String path = "/" + groupName + "/" + memberName;
		String createdPath = zk.create(path, null /* data */,
				Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
		System.out.println("Created	" + createdPath);
	}

	public void list(String groupName) throws KeeperException, InterruptedException {
		String path = "/" + groupName;
		try {
			List<String> children = zk.getChildren(path, false);
			if (children.isEmpty()) {
				System.out.printf("No members in group %s\n", groupName);
				System.exit(1);
			}
			for (String child : children) {
				System.out.println(child);
			}
		} catch (KeeperException.NoNodeException e) {
			System.out.printf("Group %s	does not exist\n", groupName);
			System.exit(1);
		}
	}

	public void delete(String groupName) throws KeeperException, InterruptedException {
		String path = "/" + groupName;
		try {
			List<String> children = zk.getChildren(path, false);
			for (String child : children) {
				zk.delete(path + "/" + child, -1);
			}
			zk.delete(path, -1);
		} catch (KeeperException.NoNodeException e) {
			System.out.printf("Group %s	does not exist\n\n", groupName);
			System.exit(1);
		}
	}
	
	public static void main(String[] args) throws Exception {
		String server = "localhost"; // "172.30.248.252:2181"; // "172.30.251.23:2181";
		ZKConnectionClient zkClient = new ZKConnectionClient();
		zkClient.connect(server);
//		zkClient.create("dubbo-test");
//		zkClient.delete("dubbo/com.alibaba.dubbo.demo.user.facade.AnotherUserRestService");
		zkClient.list("dubbo");
		zkClient.close();
	}
}
