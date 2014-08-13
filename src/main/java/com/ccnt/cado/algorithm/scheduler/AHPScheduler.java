package com.ccnt.cado.algorithm.scheduler;

import java.util.List;

import com.ccnt.cado.algorithm.data.Deploy;
import com.ccnt.cado.algorithm.data.SysState;
import com.ccnt.cado.algorithm.data.Unit;
import com.ccnt.cado.algorithm.data.VM;
import com.ccnt.cado.algorithm.monitor.SystemMonitor;

public class AHPScheduler implements AppScheduler{
	private Unit ahpWeigh;
	public List<ScheduleResult> doSchedule(List<Deploy> units, List<VM> vms) {
		
		return null;
	}
	public List<Deploy> doSchedule(SystemMonitor monitor,double max, double min) {
		System.out.println("AHP������ִ�е��ȣ���ֵmax="+max+", min="+min);
		List<Deploy> deployList = monitor.getFetcher().getDeployUnitsData();
		List<VM> vmList = monitor.getFetcher().getVMsData();
		SysState state = monitor.computeSysState(max, min);
		System.out.println("����ϵͳ��ǰ״̬      ���֣�"+state.getScore() +" ʹ���ʣ�"+state.getUsage() +"  ���"+state.getVariance());
		if(state.getScore() < 60){
			System.out.println("ϵͳ״̬���ѣ������Ż��׶Ρ�����");
			if(state.getUsage() > max){
				//ϵͳ״̬������ֵ��Ǩ��
				System.out.println("-----------------------------------------------------");
				System.out.println("ϵͳ״̬������ֵ����Ǩ��....");
				double tmp = state.getUsage();
				while(tmp > max){
					VM from = monitor.getTop(vmList, ahpWeigh);
					System.out.println("ռ������ߵ��������"+from.getVmId());
					Deploy d = monitor.computeMostConsume(from, deployList, ahpWeigh);
					System.out.println("�����"+from.getVmId()+"����������Դ��Ӧ�ã�"+d.getUnitId());
					//��ѹ����С��һ̨����
					VM to = this.computeMinPressure(vmList, d);
					if(to == null){
						System.out.println("�޷�Ǩ�ƣ�ֻ��һ̨�����");
						break;
					}else{
						migrate(d, to,from);
						SysState ss = monitor.computeCurrentState(vmList, max, min);
						System.out.println("Ǩ�ƺ�ϵͳ  ����" + ss.getScore() +"  ʹ���ʣ�"+ss.getUsage() +" ���"+ss.getVariance());
					}
				}
			}else if(state.getUsage() < min){
				//ϵͳ״̬���ͣ��ϲ�
				double tmp = state.getUsage();
				while(tmp < min){
					VM vm1 = monitor.getButtom(vmList, ahpWeigh);
					vmList.remove(vm1);
					VM vm2 = monitor.getButtom(vmList, ahpWeigh);
					if(vm2 == null){
						System.out.println("ֻ��һ̨��������޷����ϲ�");
						break;
					}else{
						merge(vm2, vm1, deployList);
						tmp = monitor.computeCurrentState(vmList, max, min).getUsage();
					}
				}
			}
			
		}else{
			System.out.println("ϵͳ״̬���ã������Ż�������");
		}
		return deployList;
	}
	private VM computeMinPressure(List<VM> vmList, Deploy d) {
		int minVmId = -1;
		double minPressure = Double.MAX_VALUE;
		for(VM vm : vmList){
			double currentPressure = deployPressure(d, vm);
			if(currentPressure != -1){
				if(currentPressure < minPressure && vm.getVmId() != d.getVmId()){
					minVmId = vm.getVmId();
					minPressure = currentPressure;
				}
			}
		}
		if(minVmId == -1){
			return null;
		}else{
			return getVmById(minVmId, vmList);
		}
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
		System.out.println("[INFO]��ʼ��AHP������....Ȩֵ��" +
				"cpu="+ahpWeigh.getCpu()+", memory="+ahpWeigh.getMemeory()+
				" ,net="+ ahpWeigh.getNet()+", io="+ahpWeigh.getIo());
		this.ahpWeigh = ahpWeigh;
	}
	
	//�������vm2������Ӧ��Ǩ�Ƶ�vm1��
	public VM merge(VM vm1, VM vm2, List<Deploy> deployes){
		for(Deploy d : deployes){
			if(d.getVmId() == vm2.getVmId()){
				if(canMigrate(d, vm1)){
					this.migrate(d, vm1,vm2);
					System.out.println("��Ӧ��"+d.getUnitId() +"��"+vm2.getVmId()+"�ϲ���"+vm1.getVmId());
				}else{
					System.out.println("[ERROR] Ŀ������û�п��ÿռ�");
				}
			}
		}
		return vm1;
	}
	private boolean canMigrate(Deploy from, VM to){
		Unit fromUnit = from.getMetrics();
		Unit used = to.getUsedMetrics();
		
		if((fromUnit.getCpu() + used.getCpu()) > to.getStaticMetircs().getCpu()
				|| (fromUnit.getMemeory() + used.getMemeory()) > to.getStaticMetircs().getMemeory()
				||(fromUnit.getIo() + used.getIo()) > to.getStaticMetircs().getIo()
				||(fromUnit.getNet() + used.getNet()) > to.getStaticMetircs().getNet()){
			return false;
		}else{
			return true;
		}
	}
	//��Ӧ��Ǩ�Ƶ���һ̨�����
	public void migrate(Deploy from, VM to, VM fromVm){
		if(!canMigrate(from, to)){
			System.out.println("���ÿռ䲻�㣬Ǩ��ʧ�ܣ������¿��������������");
			return;
		}
		System.out.println("[INFO]�������"+fromVm.getVmId()+"Ǩ��Ӧ��"+from.getUnitId()+"�������"+to.getVmId());
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
	//�������������Ӧ�õ�ѹ��ֵ
	public double deployPressure(Deploy d, VM vm){
		if(!canMigrate(d, vm)){
			System.out.println("����"+vm.getVmId()+"�ռ䲻�㣬�޷�����"+d.getUnitId());
			return -1;
		}
		Unit used = vm.getUsedMetrics();
		Unit max = vm.getStaticMetircs();
		Unit tmp = new Unit(max.getCpu() - used.getCpu(),max.getMemeory() - used.getMemeory(),max.getIo() - used.getIo(), max.getNet() - used.getNet());
		return tmp.multiplyWeigh(ahpWeigh);
	}
}
