package hx.proxy.jdk;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author BenSNW
 *
 */
public class JdkDynamicProxy {
	
	public static void main(String[] args) {
		
		SimpleService service = (SimpleService) Proxy.newProxyInstance(
				JdkDynamicProxy.class.getClassLoader(),
				new Class[] { SimpleService.class },
				new ServiceProxyHandler());
		service.serve(new String[] {"Hello", "Proxy"}, "From SimpleServiceHandler" );
		
		System.out.println();
		
		service = (SimpleService) Proxy.newProxyInstance(
				JdkDynamicProxy.class.getClassLoader(),
				new Class[] { SimpleService.class },
				new ServiceProxyHandler() {				
					@Override
					public void beforeInvoked(Object proxy, Method method, Object[] args)
							throws Throwable {
						System.out.println("Overriden service invoker pre-processor");
					}
				});
		service.serve(new String[] {"Hello", "Proxy"}, "From OverridenServiceHandler" );
		
		System.out.println();
		
		AnnotatedService as = (AnnotatedService) Proxy.newProxyInstance(
				JdkDynamicProxy.class.getClassLoader(),
				new Class[] { AnnotatedService.class },
				new ServiceProxyHandler());
		as.serve(new String[] {"Hello", "Proxy"}, "From AnnotatedService" );
	}
}
