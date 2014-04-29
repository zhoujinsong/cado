package com.ccnt.cado.bean;

public class MetricData {
	private String name;
	private Object value;
	private String unit;
	
	public MetricData() {
		super();
	}
	public MetricData(String name, Object value) {
		super();
		this.name = name;
		this.value = value;
	}
	
	public MetricData(String name, Object value, String unit) {
		super();
		this.name = name;
		this.value = value;
		this.unit = unit;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
}
