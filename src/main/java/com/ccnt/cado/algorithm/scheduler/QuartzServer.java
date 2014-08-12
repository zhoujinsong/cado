package com.ccnt.cado.algorithm.scheduler;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

public class QuartzServer {
	public static void main(String[] args){
		QuartzServer q = new QuartzServer();
		try {
			q.startScheduler();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
	public void startScheduler() throws SchedulerException{
		Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
		JobDetail job = JobBuilder.newJob(ScheduleJob.class).
				withIdentity("job1","default_group")
				.build();
		Trigger trigger = TriggerBuilder
				.newTrigger().withIdentity("trigger1","default_group")
				.startNow()
				.withSchedule(SimpleScheduleBuilder.simpleSchedule()
						.withIntervalInSeconds(20)
						.repeatForever())
				.build();

	    scheduler.scheduleJob(job, trigger);
	    scheduler.start();
	}
}
