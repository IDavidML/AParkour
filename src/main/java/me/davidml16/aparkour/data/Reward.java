package me.davidml16.aparkour.data;

public class Reward {

	private String permission;
	private String command;
	private String parkour;

	private boolean isGlobalReward;

	public Reward(String permission, String command) {
		this(permission, command, null);
	}

	public Reward(String permission, String command, String parkour) {
		this.permission = permission;
		this.command = command;
		this.parkour = parkour;
		this.isGlobalReward = parkour == null;
	}

	public String getPermission() {
		return permission;
	}

	public String getCommand() {
		return command;
	}

	public String getParkour() {
		return parkour;
	}

	public boolean isGlobalReward() {
		return isGlobalReward;
	}
}
