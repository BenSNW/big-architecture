package hx.dubbo.service;

<<<<<<< HEAD
=======
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.Enumeration;

>>>>>>> 1647f7d60ace22c7d439e81d116a56ee324a1776
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceLauncher {

	private static final Logger logger = LoggerFactory.getLogger(ServiceLauncher.class);
	
<<<<<<< HEAD
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
		
=======
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
>>>>>>> 1647f7d60ace22c7d439e81d116a56ee324a1776
	}
	
}
