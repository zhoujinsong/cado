package com.ccnt.cado.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.trilead.ssh2.Connection;
import com.trilead.ssh2.Session;
import com.trilead.ssh2.StreamGobbler;

public class SSHClient extends Logger{
	private Connection conn;
	
	public SSHClient() {
		super();
	}
	public boolean connect(String hostname){
		try {
			conn = new Connection(hostname);
			conn.connect();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
	}
	public boolean auth(String username,String password){
		if(conn != null){
			try {
				return conn.authenticateWithPassword(username, password);
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}else{
			return false;
		}
	}
	public String excute(String command){
		String result = null;
		if(conn.isAuthenticationComplete()){
			Session session = null;
			try {
				session = conn.openSession();
				session.execCommand(command);
				result = inputStream2String(session.getStdout());
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				if(session != null){
					session.close();
				}
			}
		}
		return result;
	}
	@SuppressWarnings("resource")
	private String inputStream2String(InputStream input) throws IOException{
		StringBuffer sb = new StringBuffer();
		InputStream is = new StreamGobbler(input);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		while(true){
			String line = br.readLine();
			if(line == null){
				break;
			}
			sb.append(line+"\n");
			
		}
		return sb.toString();
	}
}
