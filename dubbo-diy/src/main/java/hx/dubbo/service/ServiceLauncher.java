package hx.dubbo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceLauncher {

	private static final Logger logger = LoggerFactory.getLogger(ServiceLauncher.class);
	
	public static void run(Class<?> source, String... args) throws Throwable {
		try {
			ServiceScan scan = source.getAnnotation(ServiceScan.class);
			if (scan.classes().length > 0) {
				for (Class<?> clazz : scan.classes())
					loadService(clazz);
			} else if (scan.basePackages().length > 0) {
				for (String basePackage : scan.basePackages())
					loadConfig(basePackage);
			} else {
				// no ServiceScan found, scan based on the package of the source class
				loadConfig(source.getPackage().getName());
				logger.info("Service started");
			}
		} catch (Throwable th) {
			logger.error("Service start failed");
			th.printStackTrace();
		}
	}
	
	private static void loadService(Class<?> clazz) {
		ServiceAnnotation service = clazz.getAnnotation(ServiceAnnotation.class);
		System.out.println(service.name());
	}

	private static Class<?>[] loadConfig(String basePackage) {
		
		return null;
		
	}

	
}
