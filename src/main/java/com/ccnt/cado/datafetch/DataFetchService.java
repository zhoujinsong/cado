package com.ccnt.cado.datafetch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ccnt.cado.datastorage.DataStorer;
import com.ccnt.cado.service.Service;

public class DataFetchService implements Service{
	private DataStorer dataStorer;
	private DataFetchScheduler dataFetchScheduler;

	public DataFetchService(DataStorer dataStorer) {
		super();
		this.dataStorer = dataStorer;
		this.dataFetchScheduler = new DataFetchScheduler(dataStorer);
	}

	
	public void start() {
		dataFetchScheduler.start();
		Map<String,Object> query = new HashMap<String,Object>();
		query.put("class", "platform");
		List<Map<String,Object>> attributesArray = dataStorer.getMonitorObjects(query);
		dataStorer.dropAll();
		for(Map<String,Object> attributes : attributesArray){
			String name = (String) attributes.get("name");
			if("cloudify".equals(name)){
				attributes.remove("_id");
				MonitorObject platform = CloudifyFactory.getFactory().createPlatform(attributes);
				dataStorer.put(platform);
				dataFetchScheduler.schedule(platform);
			}
		}
	}

	
	public void stop() {
		dataFetchScheduler.stop();
	}
	

}
