package hx.dubbo.registry;

import hx.dubbo.service.ServiceAnnotation;
import hx.dubbo.service.ServiceException;

public abstract class AbstractServiceRegistry implements ServiceRegistry {

	protected final RegistryUri uri;
	
	public AbstractServiceRegistry(RegistryUri uri) {
		if (uri == null)
			throw new ServiceException("RegistryUri cannot be null");
		this.uri = uri;
	}
	
	@Override
	public RegistryUri getRegistryUri() {
		return this.uri;
	}
	
	@Override
	public final void register(Class<?> service) throws ServiceException {
		if (service.getAnnotation(ServiceAnnotation.class) == null)
			throw new ServiceException("Invalid service: no ServiceAnnotation found");
		doRegister(service);
	}

	protected abstract void doRegister(Class<?> service);

}
