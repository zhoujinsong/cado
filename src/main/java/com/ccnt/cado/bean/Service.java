package com.ccnt.cado.bean;

import java.util.ArrayList;
import java.util.List;

public class Service extends MonitorObject{
	private int id;
	private String name;
	private String runingState;
	private Application application;
	private List<ServiceInstance> instances;
	
	public Service() {
		super();
		this.instances = new ArrayList<ServiceInstance>();
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRuningState() {
		return runingState;
	}
	public void setRuningState(String runingState) {
		this.runingState = runingState;
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
	public boolean equals(Object o){
		if(!(o instanceof Service)){
			return false;
		}
		Service service = (Service)o;
		return name.equals(service.getName()) && application.getName().equals(service.getApplication().getName());
	}
}
