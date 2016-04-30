package hx.dubbo.service.demo;

import hx.dubbo.service.ComponentScan;
import hx.dubbo.service.ServiceLauncher;

@ComponentScan
public class ServideDemo {

	public static void main(String[] args) {
		ServiceLauncher.run(ServideDemo.class, args);
	}
}
