package com.ccnt.cado.datafetch.cloudify;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.map.ObjectMapper;

import com.ccnt.cado.bean.Application;
import com.ccnt.cado.bean.Host;
import com.ccnt.cado.bean.MetricData;
import com.ccnt.cado.bean.Service;
import com.ccnt.cado.bean.ServiceInstance;
import com.ccnt.cado.bean.cloudify.CloudifyConfig;
import com.ccnt.cado.bean.cloudify.CloudifyServiceInstance;
import com.ccnt.cado.datafetch.AppDataFetcher;
import com.ccnt.cado.exception.RestException;
import com.ccnt.cado.util.Logger;
import com.ccnt.cado.util.RestClient;

import static com.ccnt.cado.bean.MonitorObject.*;

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
	public void fetchData(List<Host> hosts,List<Application> applications,
			List<Service> services, List<ServiceInstance> instances){
		if(config == null){
			return;
		}
		String url = "http://";
		try {
			url += config.getHost() + ":" + config.getRestPort() + config.getUrls().get(CloudifyConfig.APP_DES_URL);
			String response = rest.get(url);
			Map<?, ?> map = mapper.readValue(response, Map.class);
			if(map.get("status").equals("success")){
				List<Map<?,?>> appDeses = (List<Map<?, ?>>) map.get("response");
				for(Map<?,?> appDes : appDeses){
					Application app = null;
					Application tempApp = new Application();
					tempApp.setName((String) appDes.get("applicationName"));
					String applicationState = (String) appDes.get("applicationState");
					for(Application a : applications){
						if(a.equals(tempApp)){
							app = a;
							if(app.getRuningState().equals(applicationState)){
								app.setState(MONSTATE_SAME);
							}else{
								app.setRuningState(applicationState);
								app.setState(MONSTATE_CHANGED);
							}
							break;
						}
					}
					if(app == null){
						app = tempApp;
						app.setRuningState(applicationState);
						applications.add(app);
					}
					List<Map<?,?>> serviceDeses = (List<Map<?, ?>>) appDes.get("servicesDescription");
					for(Map<?,?> serviceDes : serviceDeses){
						Service service = null;
						Service tempService = new Service();
						tempService.setName((String) serviceDes.get("serviceName"));
						tempService.setApplication(app);
						String serviceState = (String) serviceDes.get("serviceState");
						for(Service s : services){
							if(s.equals(tempService)){
								service = s;
								if(service.getRuningState().equals(serviceState)){
									service.setState(MONSTATE_SAME);
								}else{
									service.setRuningState(serviceState);
									service.setState(MONSTATE_CHANGED);
								}
								break;
							}
						}
						if(service == null){
							service = tempService;
							services.add(service);
							service.setRuningState(serviceState);
							app.getServices().add(service);
						}
						List<Map<?,?>> instanceDeses = (List<Map<?, ?>>) serviceDes.get("instancesDescription");
						for(Map<?,?> instanceDes : instanceDeses){
							CloudifyServiceInstance instance = null;
							CloudifyServiceInstance tempInstance = new CloudifyServiceInstance();
							tempInstance.setName((String) instanceDes.get("instanceName"));
							tempInstance.setInstanceId((Integer) instanceDes.get("instanceId"));
							String instanceState = (String) instanceDes.get("instanceStatus");
							for(ServiceInstance si : instances){
								if(si.equals(tempInstance)){
									if(!(si instanceof CloudifyServiceInstance)){
										CloudifyServiceInstance cloudifyInstance = new CloudifyServiceInstance(si);
										cloudifyInstance.setInstanceId((Integer) instanceDes.get("instanceId"));
										instances.remove(si);
										instances.add(cloudifyInstance);
										si = cloudifyInstance;
									}
									instance = (CloudifyServiceInstance) si;
									if(instance.getRuningState().equals(instanceState)){
										instance.setState(MONSTATE_SAME);
									}else{
										instance.setRuningState(instanceState);
										instance.setState(MONSTATE_CHANGED);
									}
									break;
								}
							}
							if(instance == null){
								instance = tempInstance;
								instance.setRuningState(instanceState);
								service.getInstances().add(instance);
								instance.setService(service);
								instances.add(instance);
							}
							String hostName = (String) instanceDes.get("hostName");
							fetchMetricDataOfInstance(instance);
							for(Host host : hosts){
								if(hostName.equals(host.getName())){
									if(!host.getInstances().contains(instance)){
										host.getInstances().add(instance);
									}
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
		List<MetricData> metricDatas = new ArrayList<MetricData>();
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
					metricDatas.add(metric);
				}
			}
		} catch (RestException e) {
			error(e.getErrorMsg());
			e.printStackTrace();
		} finally{
			instance.setMetrics(metricDatas);
			instance.setMetricDate(new Date());
		}
	}


}
