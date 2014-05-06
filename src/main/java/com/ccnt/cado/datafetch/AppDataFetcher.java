package com.ccnt.cado.datafetch;

import java.util.List;

import com.ccnt.cado.bean.Application;
import com.ccnt.cado.bean.Host;
import com.ccnt.cado.bean.Service;
import com.ccnt.cado.bean.ServiceInstance;

public interface AppDataFetcher{
	public void fetchData(List<Host> hosts,List<Application> applications,
			List<Service> services, List<ServiceInstance> instances);
}
