package me.davidml16.aparkour.data;

public class RewardData {

	private String permission;
	private String command;

	public RewardData(String permission, String command) {
		this.permission = permission;
		this.command = command;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

}
