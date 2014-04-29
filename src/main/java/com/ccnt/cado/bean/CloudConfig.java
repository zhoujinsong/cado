package com.ccnt.cado.bean;

public class CloudConfig {
	private String cloudName;
	private String version;
	public CloudConfig(String cloudName, String version) {
		super();
		this.cloudName = cloudName;
		this.version = version;
	}
	public String getCloudName() {
		return cloudName;
	}
	public void setCloudName(String cloudName) {
		this.cloudName = cloudName;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	
}
