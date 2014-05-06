package com.ccnt.cado.bean;

import java.util.ArrayList;
import java.util.List;

public class Host extends MonitorObject{
	private int id;
	private String address;
	private String name;
	private List<ServiceInstance> instances;
	private List<MetricData> Props;
	public Host() {
		super();
		this.instances = new ArrayList<ServiceInstance>();
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	public List<MetricData> getProps() {
		return Props;
	}
	public void setProps(List<MetricData> props) {
		Props = props;
	}
	@Override
	public boolean equals(Object o){
		if(!(o instanceof Host)){
			return false;
		}
		return name.equals(((Host) o).getName());
	}
}
