package com.ccnt.cado.bean.cloudify;

import com.ccnt.cado.bean.ServiceInstance;

public class CloudifyServiceInstance extends ServiceInstance{
	private int instanceId;
	
	public CloudifyServiceInstance(ServiceInstance instance) {
		super();
		super.setId(instance.getId());
		super.setName(instance.getName());
		super.setRuningState(instance.getRuningState());
		super.setState(instance.getState());
		super.setHost(instance.getHost());
		super.setService(instance.getService());
		super.setMetricDate(instance.getMetricDate());
		super.setMetrics(instance.getMetrics());
	}

	public CloudifyServiceInstance() {
		super();
	}

	public int getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(int instanceId) {
		this.instanceId = instanceId;
	}
	@Override
	public boolean equals(Object o){
		if(!(o instanceof CloudifyServiceInstance)){
			return false;
		}
		CloudifyServiceInstance instance = (CloudifyServiceInstance)o;
		return super.equals(o) && instanceId == instance.getInstanceId();
	}
}
