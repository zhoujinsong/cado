package com.ccnt.cado.bean;

import java.util.ArrayList;
import java.util.List;

public class Host {
	private String address;
	private String name;
	private List<ServiceInstance> instances;
	private List<MetricData> metrics;
	public Host() {
		super();
		this.instances = new ArrayList<ServiceInstance>();
		this.metrics = new ArrayList<MetricData>();
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<ServiceInstance> getInstances() {
		return instances;
	}
	public void setInstances(List<ServiceInstance> instances) {
		this.instances = instances;
	}
	public List<MetricData> getMetrics() {
		return metrics;
	}
	public void setMetrics(List<MetricData> metrics) {
		this.metrics = metrics;
	}
	
}
