package com.ccnt.cado.algorithm.monitor;
import java.util.List;

import com.ccnt.cado.algorithm.data.DataFetcher;
import com.ccnt.cado.algorithm.data.Deploy;
import com.ccnt.cado.algorithm.data.SysState;
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
	public SysState computeSysState(double max, double min){	
		List<VM> vms = fetcher.getVMsData();
		return computeCurrentState(vms, max, min);
	}
	
	private double computeScore(double x, double max, double min) {
		if(max <= min){
			System.out.println("[ERROR]最大最小阈值出错！");
			return 0;
		}
		double best = (max - min) / 2 + min;
		double score = 0;
		if(x <= max && x >= min){
			score = 100 - (x - best) * 40 / (max - best);
		}else if(x > max){
			score = 60 - 1.5 * 60 * (x - max)/(1 - max); // 2为惩罚因子
		}else{
			score = 60 - 1.5 * 60 * (min - x)/min; 
		}		
		if(score < 0 ){
			score = 0;
		}
		return score;
	}
	private double computeVariance(double[] usages, double average){
		double result = 0;
		for(int i = 0; i < usages.length; i++){
			result += (usages[i] - average) * (usages[i] - average);
		}
		return result;
	}
	public SysState computeCurrentState(List<VM> vms, double max, double min){
		SysState state = new SysState();
		
		Unit used, vmstatic;
		double usage = 0, score = 0;
		double[] usages = new double[vms.size()];
		
		for(int i = 0; i < vms.size(); i++) {
			VM vm = vms.get(i);
			used = vm.getUsedMetrics();
			vmstatic = vm.getStaticMetircs();
			
			usages[i] = weigh.getCpu() * used.getCpu() / vmstatic.getCpu() +
			weigh.getMemeory() * used.getMemeory() / vmstatic.getMemeory() +
			weigh.getIo() * used.getIo() / vmstatic.getIo() + 
			weigh.getNet() * used.getNet() / vmstatic.getNet();
			
			usage += usages[i];
			score += this.computeScore(usages[i], max, min);
		}
		state.setScore(score / vms.size());
		state.setUsage(usage / vms.size());
		state.setVariance(this.computeVariance(usages, usage / vms.size()));
		
		return state;
	}
	/*public double computeSysState(List<VM> vms, double max, double min){
		
		double result = 0.0;

		Unit used, vmstatic;
		for(VM vm : vms) {
			used = vm.getUsedMetrics();
			vmstatic
			= vm.getStaticMetircs();
			
			double usage = weigh.getCpu() * used.getCpu() / vmstatic.getCpu() +
			weigh.getMemeory() * used.getMemeory() / vmstatic.getMemeory() +
			weigh.getIo() * used.getIo() / vmstatic.getIo() + 
			weigh.getNet() * used.getNet() / vmstatic.getNet();
			
		}	
		return result / vms.size();
	}*/
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
