package com.ccnt.cado.datafetch;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ccnt.cado.datastorage.DataStorer;

public class DataFetchJob implements Job{

	@Override
	public void execute(JobExecutionContext ctx) throws JobExecutionException {
		JobDataMap jobDataMap = ctx.getJobDetail().getJobDataMap();
		MonitorObject obj = (MonitorObject) jobDataMap.get("monitorObj");
		Metric metric = (Metric) jobDataMap.get("metric");
		DataFetchScheduler scheduler = (DataFetchScheduler) jobDataMap.get("scheduler");
		DataStorer dataStorer = (DataStorer) jobDataMap.get("dataStorer");
		String response = metric.getCommandExcutor().execute(metric.getCommand(), 
				metric.getCommandArgumentLoader().getArguments(obj));
		metric.getCommandResolver().resolve(response, obj ,scheduler,dataStorer);
	}

}
