package hx.sboot.scheduler;

import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;


//http://websystique.com/spring/spring-4-quartz-scheduler-integration-example/
//http://docs.spring.io/spring/docs/current/spring-framework-reference/html/scheduling.html

//Spring: task scheduling = scheduler (trigger) + executor (asynchronous, thread pool) + job
//Quartz uses Trigger, Job and JobDetail objects to realize scheduling of all kinds of jobs

@Profile("scheduler")
@EnableAutoConfiguration( exclude={
		DataSourceAutoConfiguration.class,
		HibernateJpaAutoConfiguration.class,
		MybatisAutoConfiguration.class,
		WebMvcAutoConfiguration.class,
})
public class QuartzScheduler {

	public static void main(String[] args) {

//		try (ConfigurableApplicationContext context = 
//				new ClassPathXmlApplicationContext("scheduler/quartz-context.xml")) {
//			
//			System.out.println(context.getBeanDefinitionCount());
//		}
		
		new SpringApplicationBuilder()
			.bannerMode(Banner.Mode.OFF)
			.sources("scheduler/quartz-context.xml")
			.web(false)
			.build()
			.run(args);
		
	}
}

/**
 * Using the MethodInvokingJobDetailFactoryBean, you don’t need to create one-line jobs
 * that just invoke a method, instead you only need to create the actual business object
 * and its method to be scheduled.
 * 
 * @author BenSNW
 */
@Component("methodInvokingJobBean")
class MethodInvokingJobBean {

	public void printMessage() {
        System.out.println("I am called by MethodInvokingJobDetailFactoryBean using SimpleTriggerFactoryBean");
    }
	
}

@Component("simpleJobBean")
class SimpleJob {

	public void printMessage() {
		System.out.println("I am called by QuartzJobBean using CronTriggerFactoryBean");
    }
	
}