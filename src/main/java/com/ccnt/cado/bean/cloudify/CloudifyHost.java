package com.ccnt.cado.bean.cloudify;

import com.ccnt.cado.bean.Host;

public class CloudifyHost extends Host{
	private String username;
	private String password;
	
	public CloudifyHost() {
		super();
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
