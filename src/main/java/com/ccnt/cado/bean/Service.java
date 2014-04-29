package com.ccnt.cado.bean;

import java.util.ArrayList;
import java.util.List;

public class Service {
	private String name;
	private String state;
	private Application application;
	private List<ServiceInstance> instances;
	
	public Service() {
		super();
		this.instances = new ArrayList<ServiceInstance>();
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
	public Application getApplication() {
		return application;
	}
	public void setApplication(Application application) {
		this.application = application;
	}
	public List<ServiceInstance> getInstances() {
		return instances;
	}
	public void setInstances(List<ServiceInstance> instances) {
		this.instances = instances;
	}
	
}
