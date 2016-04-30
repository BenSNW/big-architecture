package hx.dubbo.service.provider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Service {
		
	String name();
	int port() default 1088;
	int timeout() default 3;
	String protocol() default "rest";
	String registry() default "zookeeper://localhost:2181";
}
