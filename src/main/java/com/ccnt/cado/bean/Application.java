package com.ccnt.cado.bean;

import java.util.ArrayList;
import java.util.List;

public class Application {
	private String name;
	private String state;
	private List<Service> services;
	
	public Application() {
		super();
		this.services = new ArrayList<Service>();
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
	public List<Service> getServices() {
		return services;
	}
	public void setServices(List<Service> services) {
		this.services = services;
	}
	
}
