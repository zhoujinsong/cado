package com.ccnt.cado.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Logger {
	private Log log;
	public Logger() {
		super();
		this.log = LogFactory.getLog(getClass());
		
	}
	public void debug(String logInfo){
		if(log.isDebugEnabled()){
			log.debug(logInfo);
		}
	}
	public void info(String logInfo){
		if(log.isInfoEnabled()){
			log.info(logInfo);
		}
	}
	public void warn(String logInfo){
		if(log.isWarnEnabled()){
			log.warn(logInfo);
		}
	}
	public void error(String logInfo){
		if(log.isErrorEnabled()){
			log.error(logInfo);
		}
	}
}
