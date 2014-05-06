package com.ccnt.cado.datastorage;

import java.util.List;

import com.ccnt.cado.bean.Application;
import com.ccnt.cado.bean.Host;
import com.ccnt.cado.bean.MonitorData;
import com.ccnt.cado.bean.Service;
import com.ccnt.cado.bean.ServiceInstance;

public interface DataStorer {
	public void storeData(MonitorData data);
	public MonitorData getData();
}
