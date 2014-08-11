package com.ccnt.cado.algorithm.scheduler;

import java.util.List;

import com.ccnt.cado.algorithm.data.Deploy;
import com.ccnt.cado.algorithm.data.Unit;
import com.ccnt.cado.algorithm.data.VM;
import com.ccnt.cado.algorithm.monitor.SystemMonitor;

public class AHPScheduler implements AppScheduler{
	private Unit ahpWeigh;
	public List<ScheduleResult> doSchedule(List<Deploy> units, List<VM> vms) {
		
		return null;
	}
	public List<Deploy> doSchedule(SystemMonitor monitor,double max, double min) {
		List<Deploy> deployList = monitor.getFetcher().getDeployUnitsData();
		List<VM> vmList = monitor.getFetcher().getVMsData();
		double state = monitor.computeSysState();
		if(state > max){
			//系统状态超过阈值，迁移
			double tmp = state;
			while(tmp > max){
				VM from = monitor.getTop(vmList, ahpWeigh);
				Deploy d = monitor.computeMostConsume(from, deployList, ahpWeigh);
				//找压力最小的一台部署
				int minVmId = Integer.MAX_VALUE;
				for(VM vm : vmList){
					if(deployPressure(d, vm) < minVmId && vm.getVmId() != d.getVmId()){
						minVmId = vm.getVmId();
					}
				}
				VM to = getVmById(minVmId, vmList);
				migrate(d, to,from);
				tmp = monitor.computeSysState(vmList);
			}
		}else if(state < min){
			//系统状态过低，合并
			double tmp = state;
			while(tmp < min){
				VM vm1 = monitor.getButtom(vmList, ahpWeigh);
				vmList.remove(vm1);
				VM vm2 = monitor.getButtom(vmList, ahpWeigh);
				merge(vm2, vm1, deployList);
				tmp = monitor.computeSysState(vmList);
			}
		}
		return deployList;
	}
	public VM getVmById(int id, List<VM> vms){
		VM result = null;
		for(VM vm : vms){
			if(vm.getVmId() == id){
				result = vm;
			}
		}
		return result;
	}
	public AHPScheduler(Unit ahpWeigh){
		this.ahpWeigh = ahpWeigh;
	}
	
	//将虚拟机vm2的所有应用迁移到vm1上
	public VM merge(VM vm1, VM vm2, List<Deploy> deployes){
		for(Deploy d : deployes){
			if(d.getVmId() == vm2.getVmId()){
				this.migrate(d, vm1,vm2);
			}
		}
		return vm1;
	}
	//将应用迁移到另一台虚拟机
	public void migrate(Deploy from, VM to, VM fromVm){
		Unit used = to.getUsedMetrics();
		Unit deploy = from.getMetrics();
		used.addUnit(deploy);
		to.setUsedMetrics(used);
		
		Unit fromUsed = fromVm.getUsedMetrics();
		Unit tmp = new Unit(fromUsed.getCpu() - deploy.getCpu(), 
				fromUsed.getMemeory() - deploy.getMemeory(), 
				fromUsed.getIo() - deploy.getIo(), 
				fromUsed.getNet() - deploy.getNet());
		fromVm.setUsedMetrics(tmp);	
		
		from.setVmId(to.getVmId());
	}
	//计算虚拟机部署应用的压力值
	public double deployPressure(Deploy d, VM vm){
		Unit used = vm.getUsedMetrics();
		Unit max = vm.getStaticMetircs();
		Unit tmp = new Unit(max.getCpu() - used.getCpu(),max.getMemeory() - used.getMemeory(),max.getIo() - used.getIo(), max.getNet() - used.getNet());
		return tmp.multiplyWeigh(ahpWeigh);
	}
}
