package com.ccnt.cado.bean;

import java.util.ArrayList;
import java.util.List;

public class ServiceInstance {
	private String name;
	private String state;
	private Service service;
	private Host host;
	private List<MetricData> metrics;
	public ServiceInstance() {
		this.metrics = new ArrayList<MetricData>();
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
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
}
