package hx.dubbo.demo;

import hx.dubbo.service.ServiceScan;
import hx.dubbo.service.ServiceLauncher;

@ServiceScan(classes=AnnotationServiceProvider.class)
public class ServideDemo {

	public static void main(String[] args) {
		try {
			ServiceLauncher.run(ServideDemo.class, args);
		} catch (Throwable th) {
			th.printStackTrace();
		}
	}
}
