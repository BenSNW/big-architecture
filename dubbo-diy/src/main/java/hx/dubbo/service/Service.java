package hx.dubbo.service;

import hx.dubbo.registry.RegistryUri;

public interface Service {

	String id();
	String name();
	int port();
	int timeout();
	RegistryUri registryUri();
	Protocol protocol();
	LoadBalancer loadBalancer();
	
	default String server() {
		return "netty";
	}
	
}
