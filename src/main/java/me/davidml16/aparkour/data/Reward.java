package me.davidml16.aparkour.data;

public class Reward {

	private String id;
	private String permission;
	private String command;
	private boolean firstTime;

	public Reward(String id, String permission, String command, boolean firstTime) {
		this.id = id;
		this.permission = permission;
		this.command = command;
		this.firstTime = firstTime;
	}

	public String getId() {
		return id;
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
