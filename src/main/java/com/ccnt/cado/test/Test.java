package com.ccnt.cado.test;

import java.util.List;

import com.ccnt.cado.bean.Host;
import com.ccnt.cado.bean.cloudify.CloudifyConfig;
import com.ccnt.cado.datafetch.cloudify.CloudifyAppDataFetcher;
import com.ccnt.cado.datafetch.cloudify.CloudifyHostDataFetcher;

public class Test {
	public static void main(String[] args) {
		CloudifyConfig config = new CloudifyConfig("192.168.2.92", 8100, "2.7.0");
		CloudifyHostDataFetcher chdf = new CloudifyHostDataFetcher(config);
		CloudifyAppDataFetcher cadf = new CloudifyAppDataFetcher(config);
		List<Host> hosts = chdf.fetchData();
		cadf.fetchData(hosts);
		System.out.println(hosts.size());
	}
}
