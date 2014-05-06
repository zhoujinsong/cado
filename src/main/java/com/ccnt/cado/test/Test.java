package com.ccnt.cado.test;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.ccnt.cado.bean.Application;
import com.ccnt.cado.bean.Host;
import com.ccnt.cado.bean.MonitorData;
import com.ccnt.cado.bean.Service;
import com.ccnt.cado.bean.ServiceInstance;
import com.ccnt.cado.bean.cloudify.CloudifyConfig;
import com.ccnt.cado.bean.cloudify.CloudifyServiceInstance;
import com.ccnt.cado.datafetch.AppDataFetcher;
import com.ccnt.cado.datafetch.DataFetcher;
import com.ccnt.cado.datafetch.HostDataFetcher;
import com.ccnt.cado.datafetch.cloudify.CloudifyAppDataFetcher;
import com.ccnt.cado.datafetch.cloudify.CloudifyDataFetcher;
import com.ccnt.cado.datafetch.cloudify.CloudifyHostDataFetcher;
import com.ccnt.cado.datastorage.DataStorer;
import com.ccnt.cado.datastorage.MongoDataStorer;
import com.ccnt.cado.exception.AuthenticationException;
import com.mongodb.DB;
import com.mongodb.MongoClient;


public class Test {
	public static void main(String[] args) {		
		CloudifyConfig config;
		try {
			config = new CloudifyConfig("2.7.0");
			DataFetcher df = new CloudifyDataFetcher(config);
			DataStorer ds = new MongoDataStorer();
			MonitorData data = ds.getData();
			for(int i=0;i<10;i++){
				df.fetchData(data);
				ds.storeData(data);
				Thread.sleep(2000);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (AuthenticationException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
