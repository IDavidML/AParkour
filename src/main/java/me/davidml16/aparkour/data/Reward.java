package me.davidml16.aparkour.data;

public class Reward {

	private String permission;
	private String command;

	public Reward(String permission, String command) {
		this.permission = permission;
		this.command = command;
	}

	public String getPermission() {
		return permission;
	}

	public String getCommand() {
		return command;
	}

}
