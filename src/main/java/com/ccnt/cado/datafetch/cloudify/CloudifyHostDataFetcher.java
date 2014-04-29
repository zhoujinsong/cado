package com.ccnt.cado.datafetch.cloudify;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.codehaus.jackson.map.ObjectMapper;

import com.ccnt.cado.bean.Host;
import com.ccnt.cado.bean.MetricData;
import com.ccnt.cado.bean.cloudify.CloudifyConfig;
import com.ccnt.cado.bean.cloudify.CloudifyHost;
import com.ccnt.cado.datafetch.HostDataFetcher;
import com.ccnt.cado.exception.RestException;
import com.ccnt.cado.util.RestClient;
import com.ccnt.cado.util.SSHClient;

public class CloudifyHostDataFetcher implements HostDataFetcher{
	private SSHClient ssh;
	private RestClient rest;
	private CloudifyConfig config;
	private ObjectMapper mapper;
	public CloudifyHostDataFetcher(CloudifyConfig config) {
		super();
		this.config = config;
		ssh = new SSHClient();
		rest = new RestClient();
		mapper = new ObjectMapper();
	}

	public CloudifyConfig getConfig() {
		return config;
	}

	public void setConfig(CloudifyConfig config) {
		this.config = config;
	}

	@SuppressWarnings("unchecked")
	public List<Host> fetchData(){
		List<Host> result = new ArrayList<Host>();
		if(config == null){
			return result;
		}
		String url = "http://";
		url += config.getHost() + ":" + config.getRestPort() + "/" +config.getVersion() +
				"/templates";
		try {
			String response = rest.get(url);
			Map<?, ?> map = mapper.readValue(response, Map.class);
			if(map.get("status").equals("Success")){
				Map<?,?> tempDeses = (Map<?, ?>) ((Map<?, ?>) map.get("response")).get("templates");
				Set<String> keys = (Set<String>) tempDeses.keySet();
				for(String key : keys){
					Map<?,?> tempDes = (Map<?, ?>) tempDeses.get(key);
					String username = (String) (tempDes.containsKey("username") ? tempDes.get("username") : null);
					String password = (String) (tempDes.containsKey("password") ? tempDes.get("password") : null);
					List<Map<?,?>> nodes = (List<Map<?, ?>>) ((Map<?, ?>) tempDes.get("custom")).get("nodesList");
					for(Map<?,?> node : nodes){
						CloudifyHost host = new CloudifyHost();
						host.setAddress((String)node.get("host-list"));
						host.setName((String)node.get("host-list"));
						host.setUsername(node.containsKey("username") ? (String)node.get("username") : username );
						host.setPassword(node.containsKey("password") ? (String)node.get("password") : password );
						fetchMetricDataOfHost(host);
						result.add(host);
					}
				}
			}
		} catch (RestException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	private void fetchMetricDataOfHost(CloudifyHost host){
		if(ssh.connect(host.getAddress()) && ssh.auth(host.getUsername(), host.getPassword())){
			String meminfo = ssh.excute("cat /proc/meminfo");
			String cpuinfo = ssh.excute("cat /proc/cpuinfo");
			StringTokenizer memStk = new StringTokenizer(meminfo);
			StringTokenizer cpuStk1 = new StringTokenizer(cpuinfo,"\n");
			memStk.nextToken();
			host.getMetrics().add(new MetricData("MemTotal", Integer.parseInt(memStk.nextToken()),"kB"));
			for(int i=0;i<6;i++){
				cpuStk1.nextToken();
			}
			StringTokenizer cpuStk = new StringTokenizer(cpuStk1.nextToken());
			for(int i=0;i<3;i++){
				cpuStk.nextToken();
			}
			host.getMetrics().add(new MetricData("Cpu", Double.parseDouble(cpuStk.nextToken()),"MHz"));
		}
	}
	
}
