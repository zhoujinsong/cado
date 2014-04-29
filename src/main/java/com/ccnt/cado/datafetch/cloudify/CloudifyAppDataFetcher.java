package com.ccnt.cado.datafetch.cloudify;

import java.io.IOException;
import java.util.List;



import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.map.ObjectMapper;

import com.ccnt.cado.bean.Application;
import com.ccnt.cado.bean.Host;
import com.ccnt.cado.bean.MetricData;
import com.ccnt.cado.bean.Service;
import com.ccnt.cado.bean.cloudify.CloudifyConfig;
import com.ccnt.cado.bean.cloudify.CloudifyServiceInstance;
import com.ccnt.cado.datafetch.AppDataFetcher;
import com.ccnt.cado.exception.RestException;
import com.ccnt.cado.util.Logger;
import com.ccnt.cado.util.RestClient;

public class CloudifyAppDataFetcher extends Logger implements AppDataFetcher {
	private RestClient rest;
	private ObjectMapper mapper;
	private CloudifyConfig config;
	public CloudifyAppDataFetcher(CloudifyConfig config) {
		super();
		this.config = config;
		this.rest = new RestClient();
		mapper = new ObjectMapper();
	}
	
	public CloudifyConfig getConfig() {
		return config;
	}

	public void setConfig(CloudifyConfig config) {
		this.config = config;
	}

	@SuppressWarnings("unchecked")
	public void fetchData(List<Host> hosts){
		if(config == null){
			return;
		}
		String url = "http://";
		try {
			url += config.getHost() + ":" + config.getRestPort() + config.getProps().get("cloudify.url.appDescription");
			String response = rest.get(url);
			Map<?, ?> map = mapper.readValue(response, Map.class);
			if(map.get("status").equals("success")){
				List<Map<?,?>> appDeses = (List<Map<?, ?>>) map.get("response");
				for(Map<?,?> appDes : appDeses){
					Application app = new Application();
					app.setName((String) appDes.get("applicationName"));
					app.setState((String) appDes.get("applicationState"));
					List<Map<?,?>> serviceDeses = (List<Map<?, ?>>) appDes.get("servicesDescription");
					for(Map<?,?> serviceDes : serviceDeses){
						Service service = new Service();
						service.setApplication(app);
						service.setName((String) serviceDes.get("serviceName"));
						service.setState((String) serviceDes.get("serviceState"));
						app.getServices().add(service);
						List<Map<?,?>> instanceDeses = (List<Map<?, ?>>) serviceDes.get("instancesDescription");
						for(Map<?,?> instanceDes : instanceDeses){
							CloudifyServiceInstance instance = new CloudifyServiceInstance();
							instance.setService(service);
							instance.setName((String) instanceDes.get("instanceName"));
							instance.setState((String) instanceDes.get("instanceStatus"));
							instance.setInstanceId((Integer) instanceDes.get("instanceId"));
							service.getInstances().add(instance);
							String hostName = (String) instanceDes.get("hostName");
							fetchMetricDataOfInstance(instance);
							for(Host host : hosts){
								if(hostName.equals(host.getName())){
									host.getInstances().add(instance);
									instance.setHost(host);
									break;
								}
							}
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RestException e){
			error(e.getErrorMsg());
			e.printStackTrace();
		}
	}
	@SuppressWarnings("unchecked")
	private void fetchMetricDataOfInstance(CloudifyServiceInstance instance) throws IOException{
		String url = "http://";
		url += config.getHost() + ":" +config.getRestPort() + "/" + 
		config.getVersion() + "/deployments/" + instance.getService().getApplication().getName() +
		"/service/" + instance.getService().getName() + "/instances/" + instance.getInstanceId() +
		"/metrics";
		try {
			String response = rest.get(url);
			Map<?, ?> map = mapper.readValue(response, Map.class);
			if(map.get("status").equals("Success")){
				Map<?,?> resp = (Map<?, ?>) map.get("response");
				Map<?,?> metricsData = (Map<?, ?>) resp.get("serviceInstanceMetricsData");
				Map<?,?> metrics = (Map<?, ?>) metricsData.get("metrics");
				
				Set<String> keys = (Set<String>) metrics.keySet();
				for(String key : keys){
					MetricData metric = new MetricData();
					metric.setName(key);
					metric.setValue(metrics.get(key).toString());
					instance.getMetrics().add(metric);
				}
			}
		} catch (RestException e) {
			error(e.getErrorMsg());
			e.printStackTrace();
		}
	}

}
