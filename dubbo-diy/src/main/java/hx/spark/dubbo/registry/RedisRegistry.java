package hx.spark.dubbo.registry;

import hx.dubbo.service.ServiceException;

public class RedisRegistry extends AbstractServiceRegistry {

	public RedisRegistry(RegistryUri uri) {
		super(uri);
	}

	@Override
	public void doRegister(Class<?> service) throws ServiceException {
		
	}

}
