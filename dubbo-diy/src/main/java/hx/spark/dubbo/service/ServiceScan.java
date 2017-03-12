package hx.spark.dubbo.service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ServiceScan {

	String[] basePackages() default {};
	
	Class<?>[] classes() default {};

	String[] value() default {};
	
}
