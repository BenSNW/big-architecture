package hx.dubbo.registry;

public class RegistryUri {

	private String uri;
	private String protocol; // zookeeper or redis
	private String host;
	private String port;
	
	public static final RegistryUri ZOOKEEPER = new RegistryUri("zookeeper://localhost:2181");
	public static final RegistryUri REDIS = new RegistryUri("redis://localhost:");
	
	public RegistryUri(String uri) {
		this.uri = uri;
	}

	public String getUri() {
		return uri;
	}

	public String getProtocol() {
		return protocol;
	}

	public String getHost() {
		return host;
	}

	public String getPort() {
		return port;
	}
	
}
