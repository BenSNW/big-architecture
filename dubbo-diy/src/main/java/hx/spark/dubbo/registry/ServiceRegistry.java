package hx.spark.dubbo.registry;

import hx.dubbo.service.ServiceException;

public interface ServiceRegistry {
	
	RegistryUri getRegistryUri();
	
	void register(Class<?> service) throws ServiceException;

}
