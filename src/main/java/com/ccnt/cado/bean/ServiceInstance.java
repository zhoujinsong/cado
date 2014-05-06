package com.ccnt.cado.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ServiceInstance extends MonitorObject{
	private int id;
	private String name;
	private String runingState;
	private Service service;
	private Host host;
	private List<MetricData> metrics;
	private Date metricDate;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getRuningState() {
		return runingState;
	}
	public void setRuningState(String runingState) {
		this.runingState = runingState;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public Service getService() {
		return service;
	}
	public void setService(Service service) {
		this.service = service;
	}
	public Host getHost() {
		return host;
	}
	public void setHost(Host host) {
		this.host = host;
	}
	public List<MetricData> getMetrics() {
		return metrics;
	}
	public Date getMetricDate() {
		return metricDate;
	}
	public void setMetrics(List<MetricData> metrics) {
		this.metrics = metrics;
	}
	public void setMetricDate(Date metricDate) {
		this.metricDate = metricDate;
	}
	@Override
	public boolean equals(Object o){
		if(!(o instanceof ServiceInstance)){
			return false;
		}
		ServiceInstance instance = (ServiceInstance)o;
		return name.equals(instance.getName());
	}
}
