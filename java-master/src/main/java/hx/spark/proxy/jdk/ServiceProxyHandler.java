package hx.spark.proxy.jdk;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

public class ServiceProxyHandler implements InvocationHandler {

	protected void beforeInvoked(Object proxy, Method method, Object[] args)
			throws Throwable {
		System.out.println("Before service invoked");
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		beforeInvoked(proxy, method, args);
		Object obj = doInvoke(proxy, method, args);
		afterInvoked(proxy, method, args);
		return obj;
	}
	
	protected Object doInvoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		// call proxy.toString() will trigger stack overflow !!!
		System.out.println("Invoking object: " + proxy.getClass().getName());
		System.out.println("Invoked method: " + method);
		// deep to string if any arg itself is an array
		System.out.println("Passed parameters: " + Arrays.deepToString(args));
		
		for (Annotation annotation : method.getAnnotations()) {
			if (annotation.annotationType() == ServiceAnnotation.class)
				System.out.println("Method annotation: " + ((ServiceAnnotation) annotation).value());
		}
		
		return null;
	}

	protected void afterInvoked(Object proxy, Method method, Object[] args)
			throws Throwable {
		System.out.println("After service invoked");
	}
	
}
