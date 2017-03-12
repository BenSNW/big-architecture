package hx.spark.dubbo.registry;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hx.dubbo.service.ServiceException;

public class ZookeeperRegistry extends AbstractServiceRegistry {

	private final CuratorFramework client;
	private static final Logger logger = LoggerFactory.getLogger(ZookeeperRegistry.class);
	
	public ZookeeperRegistry(RegistryUri uri, RetryPolicy policy) {
		super(uri);
		client = CuratorFrameworkFactory.newClient(uri.getUri(), policy);
	}

	@Override
	protected void doRegister(Class<?> service) throws ServiceException {
		try {
			client.create().inBackground().forPath("", "".getBytes());
		} catch (Exception ex) {
			logger.error("", ex);
			throw new ServiceException("");
		}
	}

}
