package hx.dubbo.registry;

import org.apache.curator.framework.CuratorFramework;

import hx.dubbo.service.ServiceException;

public class ZookeeperRegistry extends AbstractServiceRegistry {

	private CuratorFramework client;
	
	public ZookeeperRegistry(RegistryUri uri) {
		super(uri);
		
	}

	@Override
	public void doRegister(Class<?> service) throws ServiceException {
		client.close();
	}

}
