package hx.dubbo.demo;

import hx.dubbo.service.ServiceAnnotation;

@ServiceAnnotation(name="demoService")
public class ServiceProvider {

	public String sayHi(String name) {
		return "Hi " + name + " !";
	}
	
}
