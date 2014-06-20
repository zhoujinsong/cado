package com.ccnt.cado.test;


import com.ccnt.cado.datafetch.DataFetchService;
import com.ccnt.cado.datastorage.DataStorer;
import com.ccnt.cado.datastorage.MongoDataStorer;

public class Test {
	public static void main(String[] args) {
		DataStorer dataStorer = new MongoDataStorer();
		DataFetchService dataFetchService = new DataFetchService(dataStorer);
		dataFetchService.start();
	}
}
