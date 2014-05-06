package com.ccnt.cado.bean;

import java.util.List;

public class MonitorData {
	List<Host> hosts;
	List<Application> applications;
	List<Service> services;
	List<ServiceInstance> instances;
	public List<Host> getHosts() {
		return hosts;
	}
	public void setHosts(List<Host> hosts) {
		this.hosts = hosts;
	}
	public List<Application> getApplications() {
		return applications;
	}
	public void setApplications(List<Application> applications) {
		this.applications = applications;
	}
	public List<Service> getServices() {
		return services;
	}
	public void setServices(List<Service> services) {
		this.services = services;
	}
	public List<ServiceInstance> getInstances() {
		return instances;
	}
	public void setInstances(List<ServiceInstance> instances) {
		this.instances = instances;
	}
}
