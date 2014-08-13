package com.ccnt.cado.algorithm.scheduler;

import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ccnt.cado.algorithm.data.DataFetcher;
import com.ccnt.cado.algorithm.data.Deploy;
import com.ccnt.cado.algorithm.data.Unit;
import com.ccnt.cado.algorithm.monitor.SystemMonitor;

/**
 * ��ʱ��������
 * @author LS
 *
 */
public class ScheduleJob implements Job{
	private AppScheduler scheduler;

	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		Unit weigh = new Unit(0.5, 0.4, 0.1, 0);
		scheduler = new AHPScheduler(weigh);
		
		DataFetcher fetcher = new DataFetcher();
		SystemMonitor monitor = new SystemMonitor(weigh, fetcher);
		List<Deploy> result = scheduler.doSchedule(monitor, 0.9, 0.7);
	}
}
