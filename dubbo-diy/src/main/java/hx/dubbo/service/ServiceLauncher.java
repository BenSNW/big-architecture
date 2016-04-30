package hx.dubbo.service;

import java.lang.annotation.Annotation;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceLauncher {

	private static final Logger logger = LoggerFactory.getLogger(ServiceLauncher.class);
	
	public static void run(Class<?> source, String... args) {
		Annotation[] annotations = source.getAnnotations();
		boolean packageAnnotated = false;
		for (Annotation anno : annotations) {
			if (anno.annotationType() == ComponentScan.class) {
				packageAnnotated = true;
				
				String[] basePackages = ((ComponentScan) anno).basePackages();
				logger.info(Arrays.toString(basePackages));
				
				logger.info("Service Started");
			}
		}
		
		if (!packageAnnotated) {
			logger.info("No ComponentScan configuration class found");
			loadConfig(source);
		}
	}
	
	private static Class<?>[] loadConfig(Class<?> baseClass) {
		
		return loadConfig(baseClass.getPackage());
		
	}
	
	private static Class<?>[] loadConfig(Package basePackage) {
		
		return null;
		
	}

	
}
