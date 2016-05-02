package hx.dubbo.service;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceLauncher {

	private static final Logger logger = LoggerFactory.getLogger(ServiceLauncher.class);
	
	public static void run(Class<?> source, String... args) {
		Annotation[] annotations = source.getAnnotations();
		String[] basePackages = null;
		for (Annotation anno : annotations) {
			// check for ComponentScan nanotation
			if (anno.annotationType() == ComponentScan.class) {				
				basePackages = ((ComponentScan) anno).basePackages();
				// basePackages leaved as default value, namely empty array
				if (basePackages.length == 0)
					loadConfig(source.getPackage().getName());
				else {
					for (String basePackage : basePackages) {
						if (Package.getPackage(basePackage) == null) {
							logger.warn("Invalid package name found: {}", basePackage);
						} else {
							loadConfig(basePackage);
						}
					}
				}
				break;
			}
		}
		
		if (basePackages == null) {
			logger.info("No ComponentScan configuration class found");
			loadConfig(source.getPackage().getName());
		}
	}
	
	private static void loadConfig(String basePackage) {
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
