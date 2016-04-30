package hx.dubbo.service.registry;

import hx.dubbo.service.ServiceException;

public interface ServiceRegistry {

	void register(RegistryUri uri) throws ServiceException;
	
}
