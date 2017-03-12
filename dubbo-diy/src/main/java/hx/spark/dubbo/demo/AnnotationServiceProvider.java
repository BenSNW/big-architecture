package hx.spark.dubbo.demo;

import hx.dubbo.service.ServiceAnnotation;

@ServiceAnnotation(name="demoService")
public class AnnotationServiceProvider {

	public String sayHi(String name) {
		return "Hi " + name + " !";
	}
	
}
