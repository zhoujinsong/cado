package com.ccnt.cado.algorithm.monitor;
import java.util.List;

import com.ccnt.cado.algorithm.data.DataFetcher;
import com.ccnt.cado.algorithm.data.Deploy;
import com.ccnt.cado.algorithm.data.Unit;
import com.ccnt.cado.algorithm.data.VM;

public class SystemMonitor {
	private Unit weigh; //监控指标的权重
	private DataFetcher fetcher; //从数据库获取数据
	
	public SystemMonitor(Unit weigh,DataFetcher fetcher){
		this.weigh = weigh;
		this.fetcher = fetcher;
	}
	
	public Unit getWeigh() {
		return weigh;
	}

	public DataFetcher getFetcher() {
		return fetcher;
	}

	public void setFetcher(DataFetcher fetcher) {
		this.fetcher = fetcher;
	}

	public void setWeigh(Unit weigh) {
		this.weigh = weigh;
	}
	//计算系统当前状态
	public double computeSysState(){	
		List<VM> vms = fetcher.getVMsData();
		return computeSysState(vms);
	}
	public double computeSysState(List<VM> vms){
		double result = 0.0;
		Unit used = new Unit(0, 0, 0, 0);
		Unit max = new Unit(0, 0, 0, 0);
		for(VM vm : vms){
			used.addUnit(vm.getUsedMetrics());
			max.addUnit(vm.getStaticMetircs());
		}
		result =  weigh.getCpu()*used.getCpu() / max.getCpu() +
		weigh.getMemeory() * used.getMemeory() / max.getMemeory() +
		weigh.getIo() * used.getIo() / max.getIo() +
		weigh.getNet() * used.getNet() / max.getNet();
		return result;
	}
	//计算消耗率最大的虚拟机
	public VM getTop(List<VM> vms, Unit weigh){
		double max = Double.MIN_VALUE;
		VM result = null;
		for(VM vm : vms){
			double tmp = computePercent(vm).multiplyWeigh(weigh);
			if(tmp > max){
				result  = vm;
			}
		}
		return result;
	}
	//计算消耗率最低的虚拟机
	public VM getButtom(List<VM> vms, Unit weigh){
		double min = Double.MAX_VALUE;
		VM result = null;
		for(VM vm : vms){
			double tmp = computePercent(vm).multiplyWeigh(weigh);
			if(tmp < min){
				result  = vm;
			}
		}
		return result;
	}
	//计算使用率超过percent的虚拟机
	public List<VM> getTopN(double percent,List<VM> vms){
		double tmp = 0;
		for(VM vm : vms){
			Unit used = vm.getUsedMetrics();
			Unit max = vm.getStaticMetircs();
			tmp = weigh.getCpu()*used.getCpu() / max.getCpu() +
					weigh.getMemeory() * used.getMemeory() / max.getMemeory() +
					weigh.getIo() * used.getIo() / max.getIo() +
					weigh.getNet() * used.getNet() / max.getNet();
			if(tmp >= percent){
				vms.add(vm);
			}
		}
		return vms;
	}
	//计算使用率低于percent的虚拟机
	public List<VM> getBottomN(double percent,List<VM> vms){
		double tmp = 0;
		for(VM vm : vms){
			Unit used = vm.getUsedMetrics();
			Unit max = vm.getStaticMetircs();
			tmp = weigh.getCpu()*used.getCpu() / max.getCpu() +
					weigh.getMemeory() * used.getMemeory() / max.getMemeory() +
					weigh.getIo() * used.getIo() / max.getIo() +
					weigh.getNet() * used.getNet() / max.getNet();
			if(tmp <= percent){
				vms.add(vm);
			}
		}
		return vms;
	}
	public Unit computePercent(VM vm){
		Unit used = vm.getUsedMetrics();
		Unit max = vm.getStaticMetircs();
		
		return new Unit(used.getCpu() / max.getCpu(), 
				used.getMemeory()/max.getMemeory(), 
				used.getIo()/ max.getIo(), 
				used.getNet() / max.getNet());
	}
	//获取一台虚拟机上消耗资源最多的部署单元
	public Deploy computeMostConsume(VM vm, List<Deploy> list, Unit weigh){
		Deploy result = null;
		double max = Double.MIN_VALUE;
		for(Deploy d : list){
			if(d.getVmId() == vm.getVmId()){
				double tmp = d.getMetrics().multiplyWeigh(weigh);
				if(tmp > max){
					max = tmp;
					result = d;
				}
			}
		}
		return result;
	}
}
