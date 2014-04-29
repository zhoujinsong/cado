package com.ccnt.cado.datafetch;

import java.util.List;

import com.ccnt.cado.bean.Host;

public interface HostDataFetcher extends DataFetcher{
	public List<Host> fetchData();
}
