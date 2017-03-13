package hx.sboot.scheduler;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Profile("scheduler-simple")
@SpringBootApplication
@EnableScheduling
@EnableAutoConfiguration( exclude={
		DataSourceAutoConfiguration.class,
		HibernateJpaAutoConfiguration.class
})
public class SimpleScheduler {

	public static void main(String[] args) {
		new SpringApplicationBuilder()
			.bannerMode(Banner.Mode.OFF)
			.sources(SimpleScheduler.class)
			.web(false)
			.build()
			.run(args);
	}
}

@Component
class ScheduledTasks {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

//    @Scheduled(fixedRate = 5000)
//    public void fixedCcurrentTime() {
//        System.out.println("Fixed rate: The time is now " + dateFormat.format(new Date()));
//    }
    
    /**
     * Using a cron-like expression, extending the usual UN*X definition to include triggers
     * on the second as well as minute, hour, day of month, month and day of week
     * 
     * "0 0 * * * *" = the top of every hour of every day.
	 * "10 * * * * *" = every ten seconds.
	 * "0 0 8-10 * * *" = 8, 9 and 10 o'clock of every day.
	 * "0 0/30 8-10 * * *" = 8:00, 8:30, 9:00, 9:30 and 10 o'clock every day.
	 * "0 0 9-17 * * MON-FRI" = on the hour nine-to-five weekdays
	 * "0 0 0 25 12 ?" = every Christmas Day at midnight
     */
    @Scheduled(cron="0/10 * * * * *")
    public void cronCurrentTime() {
        System.out.println("Cron-triggered: The time is now " + dateFormat.format(new Date()));
    }
    
}