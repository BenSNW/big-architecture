package hx.spring.boot.scheduler;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.quartz.QuartzJobBean;

@Profile("scheduler")
public class SimpleQuartzJob extends QuartzJobBean {
    
    private SimpleJob job;
    private int timeout;
    
	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		job.printMessage();
	}

	public SimpleJob getJob() {
		return job;
	}

	public void setJob(SimpleJob job) {
		this.job = job;
	}
	
	public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
	
	public int getTimeout() {
        return this.timeout;
    }
     
}