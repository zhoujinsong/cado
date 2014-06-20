package com.ccnt.cado.datafetch;

public class Metric {
	private int cycle;
	private String command;
	private CommandExecutor commandExcutor;
	private CommandResolver commandResolver;
	private CommandArgumentLoader commandArgumentLoader;
	public int getCycle() {
		return cycle;
	}
	public void setCycle(int cycle) {
		this.cycle = cycle;
	}
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public CommandExecutor getCommandExcutor() {
		return commandExcutor;
	}
	public void setCommandExcutor(CommandExecutor commandExcutor) {
		this.commandExcutor = commandExcutor;
	}
	public CommandResolver getCommandResolver() {
		return commandResolver;
	}
	public void setCommandResolver(CommandResolver commandResolver) {
		this.commandResolver = commandResolver;
	}
	public CommandArgumentLoader getCommandArgumentLoader() {
		return commandArgumentLoader;
	}
	public void setCommandArgumentLoader(CommandArgumentLoader commandArgumentLoader) {
		this.commandArgumentLoader = commandArgumentLoader;
	}

}
