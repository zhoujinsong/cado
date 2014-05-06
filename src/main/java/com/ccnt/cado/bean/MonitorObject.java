package com.ccnt.cado.bean;

public class MonitorObject {
	public static final int MONSTATE_NEWONE = 0;
	public static final int MONSTATE_SAME = 1;
	public static final int MONSTATE_CHANGED = 2;
	public static final int MONSTATE_DELETE = -1;
	private int state;
	public MonitorObject() {
		super();
		state = 0;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
}
