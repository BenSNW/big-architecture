package hx.proxy.test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.Callable;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;

import org.junit.Test;

// https://gist.github.com/garcia-jj/819748
@SuppressWarnings("unchecked")
public class ProxyPerformanceTest {

    static final int ITERATIONS = 999999;

    @Test
    public void jdkCreateAndCall()
        throws Exception {
        for (int i = 0; i < ITERATIONS; i++) {
            createJdkProxy().call();
        }
    }

    @Test
    public void jdkCall()
        throws Exception {
        Callable<Integer> proxy = createJdkProxy();

        for (int i = 0; i < ITERATIONS; i++) {
            proxy.call();
        }
    }

    @Test
    public void javassistCreateAndCall()
        throws Exception {
        for (int i = 0; i < ITERATIONS; i++) {
            createJavassistProxy().call();
        }
    }

    @Test
    public void javassistCall()
        throws Exception {
        Callable<Integer> proxy = createJavassistProxy();

        for (int i = 0; i < ITERATIONS; i++) {
            proxy.call();
        }
    }

    @Test
    public void cglibCreateAndCall()
        throws Exception {
        for (int i = 0; i < ITERATIONS; i++) {
            createCglibProxy().call();
        }
    }

    @Test
    public void cglibCall() throws Exception {
        Callable<Integer> proxy = createCglibProxy();
        for (int i = 0; i < ITERATIONS; i++) {
            proxy.call();
        }
    }

    private Callable<Integer> createJdkProxy() {
        return (Callable<Integer>) Proxy.newProxyInstance(Callable.class.getClassLoader(),
                new Class[] { Callable.class }, new JdkInvocationHandler(new Counter()));
    }

    private Callable<Integer> createJavassistProxy() 
    			throws InstantiationException, IllegalAccessException {
       
    	ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setInterfaces(new Class[] { Callable.class });

        Class<?> proxyClass = proxyFactory.createClass();
        Callable<Integer> proxy = (Callable<Integer>) proxyClass.newInstance();
        ((ProxyObject) proxy).setHandler(new JavassistMethodInterceptor(new Counter()));
        return proxy;
    }

    private Callable<Integer> createCglibProxy() {
        Enhancer enhancer = new Enhancer();
        enhancer.setInterfaces(new Class[] { Callable.class });
        enhancer.setCallback(new CglibMethodInterceptor(new Counter()));

        Callable<Integer> proxy = (Callable<Integer>) enhancer.create();
        return proxy;
    }

    private static class JdkInvocationHandler implements InvocationHandler {
        final Object delegate;

        public JdkInvocationHandler(Object delegate) {
            this.delegate = delegate;
        }

        public Object invoke(Object object, Method method, Object[] args)
            throws Throwable {
            return method.invoke(delegate, args);
        }
    }

    private static class CglibMethodInterceptor implements MethodInterceptor {
        final Object delegate;

        public CglibMethodInterceptor(Object delegate) {
            this.delegate = delegate;
        }

        public Object intercept(Object object, Method method, Object[] args, MethodProxy methodProxy)
            throws Throwable {
            return methodProxy.invoke(delegate, args);
        }
    }

    private static class JavassistMethodInterceptor implements MethodHandler {
        final Object delegate;

        public JavassistMethodInterceptor(Object delegate) {
            this.delegate = delegate;
        }

        public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args)
            throws Throwable {
            return thisMethod.invoke(delegate, args);
        }
    }

    private static class Counter implements Callable<Integer> {
        int count = 0;
        public Integer call()
            throws Exception {
            return count++;
        }
    }
}