package com.ccnt.cado.datafetch;

import java.util.List;

import com.ccnt.cado.bean.Host;

public interface AppDataFetcher extends DataFetcher{
	public void fetchData(List<Host> hosts);
}
