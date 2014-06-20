package com.ccnt.cado.cache;

public class FetchStoreCache {
	
	private static class SingletonHelper{
		private static FetchStoreCache cache = new FetchStoreCache();
	}
	private FetchStoreCache(){
		super();
	}
	public FetchStoreCache getCache(){
		return SingletonHelper.cache;
	}
}
