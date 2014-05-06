package com.ccnt.cado.datafetch.cloudify;

import com.ccnt.cado.bean.MonitorData;
import com.ccnt.cado.bean.cloudify.CloudifyConfig;
import com.ccnt.cado.datafetch.DataFetcher;

public class CloudifyDataFetcher implements DataFetcher{
	private CloudifyHostDataFetcher chdf;
	private CloudifyAppDataFetcher cadf;
	
	public CloudifyDataFetcher(CloudifyConfig config) {
		super();
		chdf = new CloudifyHostDataFetcher(config);
		cadf = new CloudifyAppDataFetcher(config);
	}

	public void fetchData(MonitorData data) {
		chdf.fetchData(data.getHosts());
		cadf.fetchData(data.getHosts(),data.getApplications(),data.getServices(),data.getInstances());
	}

}
