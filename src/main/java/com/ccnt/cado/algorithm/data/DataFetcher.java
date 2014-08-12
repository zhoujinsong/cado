package com.ccnt.cado.algorithm.data;
import java.util.ArrayList;
import java.util.List;
/**
 * 
 * @author LS
 * 获取格式化之后的所有虚拟机、部署单元的监控数据
 */
public class DataFetcher {
	public List<VM> getVMsData(){
		//测试数据
		List<VM> vms = new ArrayList<VM>();
		VM vm1 = new VM(1,new Unit(8,512,10,10), new Unit(1.7,400,5,7));
		VM vm2 = new VM(2,new Unit(4,1024,20,20), new Unit(3.5,800,5,7));
		VM vm3 = new VM(3,new Unit(8,2048,40,40), new Unit(7.5,1900,5,7));

		vms.add(vm1);
		vms.add(vm2);
		vms.add(vm3);
		
		return vms;
	}
	public List<Deploy> getDeployUnitsData(){
		List<Deploy> deploys = new ArrayList<Deploy>();
		//test migriate
		//vm1 
		/*Deploy dp1 = new Deploy(new Unit(0.5,200, 3, 3), 1, 1);
		Deploy dp2 = new Deploy(new Unit(1,200, 2,4),1,2);
		
		//vm2
		Deploy dp3 = new Deploy(new Unit(2,500, 3, 3), 2, 3);
		Deploy dp4 = new Deploy(new Unit(1.5,300, 2,4),2,4);
		
		//vm3
		Deploy dp5 = new Deploy(new Unit(4,1500, 3, 3), 3, 5);
		Deploy dp6 = new Deploy(new Unit(3.5,400, 2,4),3,6);*/
		
		//test merge
		Deploy dp1 = new Deploy(new Unit(0.5,200, 3, 3), 1, 1);
		Deploy dp2 = new Deploy(new Unit(1,200, 2,4),1,2);
		
		//vm2
		Deploy dp3 = new Deploy(new Unit(2,500, 3, 3), 2, 3);
		Deploy dp4 = new Deploy(new Unit(1.5,300, 2,4),2,4);
		
		//vm3
		Deploy dp5 = new Deploy(new Unit(0.5,200, 3, 3), 3, 5);
		Deploy dp6 = new Deploy(new Unit(0.5,400, 2,4),3,6);
		
		deploys.add(dp1);
		deploys.add(dp2);
		deploys.add(dp3);
		deploys.add(dp4);
		deploys.add(dp5);
		deploys.add(dp6);
		
		return deploys;
	}
} 
