package hx.dubbo.service.registry;

public class RegistryUri {

	private String uri;
	private String protocol; // zookeeper or redis
	private String host;
	private String port;
	
	public RegistryUri(String uri) {
		super();
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
