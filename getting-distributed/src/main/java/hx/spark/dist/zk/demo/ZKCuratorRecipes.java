package hx.spark.dist.zk.demo;

import java.io.IOException;
import java.io.Serializable;

import org.apache.curator.framework.recipes.queue.DistributedPriorityQueue;
import org.apache.curator.framework.recipes.queue.DistributedQueue;

public class ZKCuratorRecipes {

	@SuppressWarnings({ "unused", "null" })
	public static void main(String[] args) {
		DistributedQueue<Serializable> zkCliQueue;
		DistributedPriorityQueue<Integer> curatorPQueue = null;
		try {
			curatorPQueue.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
