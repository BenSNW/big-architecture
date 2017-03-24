package hx.dubbo.service;


import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceLauncher {

	private static final Logger logger = LoggerFactory.getLogger(ServiceLauncher.class);
	
	public static void run(Class<?> source, String... args) {
		try {
			ServiceScan scan = source.getAnnotation(ServiceScan.class);
			if (scan.classes().length > 0)
				for (Class<?> clazz : scan.classes())
					loadService(clazz);
			else if (scan.basePackages().length > 0)
				for (String basePackage : scan.basePackages())
					scanPackage(basePackage);
			else  // no scanning info found, scan based on the package of the source class			
				scanPackage(source.getPackage().getName());
			
			logger.info("Service started");
		} catch (Throwable th) {
			logger.error("Service date failed", th);
		}
	}
	
	private static void loadService(Class<?> clazz) {
		ServiceAnnotation service = clazz.getAnnotation(ServiceAnnotation.class);
		System.out.println(service.name());
	}
	
	private static void scanPackage(String basePackage) {
		String packagePath = basePackage.replace(".", "/") + "/**/*.class";
		logger.info(packagePath);
		Enumeration<URL> urls = null;
		try {
			urls = ClassLoader.getSystemResources(packagePath);
			while (urls.hasMoreElements()) {
				logger.info(urls.nextElement().toString());
			};
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
