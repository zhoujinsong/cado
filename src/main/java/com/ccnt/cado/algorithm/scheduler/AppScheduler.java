package com.ccnt.cado.algorithm.scheduler;

import java.util.List;

import com.ccnt.cado.algorithm.data.Deploy;
import com.ccnt.cado.algorithm.data.VM;
import com.ccnt.cado.algorithm.monitor.SystemMonitor;

public interface AppScheduler {
	public List<Deploy> doSchedule(SystemMonitor monitor,double max, double min);
}
