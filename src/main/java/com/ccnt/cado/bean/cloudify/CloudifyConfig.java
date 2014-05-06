package com.ccnt.cado.bean.cloudify;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.ccnt.cado.bean.CloudConfig;

public class CloudifyConfig extends CloudConfig{
	private static final String HOST = "cloudify.hostAddress";
	private static final String REST_PORT = "cloudify.restPort";
	public static final String APP_DES_URL = "cloudify.url.appDescription";
	public static final String TEMPLATES_URL = "cloudify.url.templates";
	public static final String INSTANCE_METRICS_URL = "cloudify.url.instanceMetrics";
	private String host;
	private int restPort;
	private Map<String,String> urls;
	public CloudifyConfig(String version) throws IOException{
		super("Cloudify",version);
		Properties props = new Properties();
		props.load(getClass().getClassLoader().getResourceAsStream("cloudify-" + version + ".properties"));
		readProperties(props);
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
	public Map<String, String> getUrls() {
		return urls;
	}
	private void readProperties(Properties props){
		host = props.getProperty(HOST,"127.0.0.1");
		restPort = Integer.parseInt(props.getProperty(REST_PORT,"8100"));
		urls = new HashMap<String,String>();
		urls.put(APP_DES_URL, props.getProperty(APP_DES_URL));
		urls.put(TEMPLATES_URL, props.getProperty(TEMPLATES_URL));
		urls.put(INSTANCE_METRICS_URL, props.getProperty(INSTANCE_METRICS_URL));
	}
	
}
