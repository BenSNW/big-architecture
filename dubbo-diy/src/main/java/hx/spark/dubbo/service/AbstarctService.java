package hx.spark.dubbo.service;

import hx.dubbo.registry.RegistryUri;

public abstract class AbstarctService implements Service {

	@Override
	public final String id() {
		return "";
	}
	
	@Override
	public int port() {
		return 10880;
	}

	@Override
	public int timeout() {
		return 3000;
	}

	@Override
	public RegistryUri registryUri() {
		return RegistryUri.ZOOKEEPER;
	}
	
	@Override
	public Protocol protocol() {
		return Protocol.JSON_RPC;
	}

	@Override
	public LoadBalancer loadBalancer() {
		return LoadBalancer.ROUND_BIN;
	}

}
