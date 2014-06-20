package com.ccnt.cado.datastorage;

import java.util.List;
import java.util.Map;

import com.ccnt.cado.datafetch.MetricData;
import com.ccnt.cado.datafetch.MonitorObject;

public interface DataStorer {
	public List<Map<String,Object>> getMonitorObjects(Map<String,Object> queryConditions);
	public void dropAll();
	public void put(MonitorObject object);
	public void put(MetricData metricData);
	public void remove(MonitorObject object);
}
