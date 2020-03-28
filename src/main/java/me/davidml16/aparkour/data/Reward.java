package me.davidml16.aparkour.data;

public class Reward {

	private String permission;
	private String command;
	private boolean firstTime;

	public Reward(String permission, String command, boolean firstTime) {
		this.permission = permission;
		this.command = command;
		this.firstTime = firstTime;
	}

	public String getPermission() {
		return permission;
	}

	public String getCommand() {
		return command;
	}

	public boolean isFirstTime() {
		return firstTime;
	}
}
