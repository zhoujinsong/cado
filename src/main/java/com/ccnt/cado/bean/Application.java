package com.ccnt.cado.bean;

import java.util.ArrayList;
import java.util.List;

public class Application extends MonitorObject{
	private int id;
	private String name;
	private String runingState;
	private List<Service> services;
	
	public Application() {
		super();
		this.services = new ArrayList<Service>();
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
	public List<Service> getServices() {
		return services;
	}
	public void setServices(List<Service> services) {
		this.services = services;
	}
	public boolean equals(Object o){
		if(!(o instanceof Application)){
			return false;
		}
		return name.equals(((Application)o).getName());
	}
}
