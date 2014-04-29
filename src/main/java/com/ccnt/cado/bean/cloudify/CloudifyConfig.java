package com.ccnt.cado.bean.cloudify;

import java.io.IOException;
import java.util.Properties;

import com.ccnt.cado.bean.CloudConfig;

public class CloudifyConfig extends CloudConfig{
	private String host;
	private int restPort;
	private Properties props;
	public CloudifyConfig(String version) {
		super("Cloudify",version);
		this.props = new Properties();
	}
	public CloudifyConfig(String host, int restPort, String version) {
		super("Cloudify",version);
		this.host = host;
		this.restPort = restPort;
		this.props = new Properties();
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getRestPort() {
		return restPort;
	}
	public void setRestPort(int restPort) {
		this.restPort = restPort;
	}
	public Properties getProps() throws IOException {
		props.load(getClass().getClassLoader().getResourceAsStream("cloudify-" + getVersion() + ".properties"));
		return props;
	}
	
}
