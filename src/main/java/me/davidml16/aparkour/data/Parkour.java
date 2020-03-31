package me.davidml16.aparkour.data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;

import me.davidml16.aparkour.Main;
import org.bukkit.Material;

public class Parkour {
	
	private String id;
	private String name;
	private Location spawn;
	private Location start;
	private Location end;
	private Location statsHologram;
	private Location topHologram;

	private List<WalkableBlock> walkableBlocks;
	private List<Reward> rewards;

	private boolean permissionRequired;
	private String permission;
	private String permissionMessage;
	
	public Parkour(String id, String name, Location spawn, Location start, Location end, Location statsHologram, Location topHologram) {
		this.id = id;
		this.name = name;
		this.spawn = spawn;
		this.start = start;
		this.end = end;
		this.statsHologram = statsHologram;
		this.topHologram = topHologram;
		this.walkableBlocks = new ArrayList<WalkableBlock>();
		this.rewards = new ArrayList<Reward>();
		this.permissionRequired = false;
		this.permission = "";
		this.permissionMessage = "";
	}
	
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Location getSpawn() {
		return spawn;
	}

	public Location getStart() {
		return start;
	}

	public Location getEnd() {
		return end;
	}

	public Location getStatsHologram() {
		return statsHologram;
	}

	public Location getTopHologram() {
		return topHologram;
	}

	public List<WalkableBlock> getWalkableBlocks() {
		return walkableBlocks;
	}

	public void setWalkableBlocks(List<WalkableBlock> walkableBlocks) {
		this.walkableBlocks = walkableBlocks;
	}

	public List<Reward> getRewards() {
		return rewards;
	}

	public void setRewards(List<Reward> rewards) {
		this.rewards = rewards;
	}

	public boolean isPermissionRequired() {
		return permissionRequired;
	}

	public void setPermissionRequired(boolean permissionRequired) {
		this.permissionRequired = permissionRequired;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public String getPermissionMessage() {
		return permissionMessage;
	}

	public void setPermissionMessage(String permissionMessage) {
		this.permissionMessage = permissionMessage;
	}

	public void saveParkour() {
		Main.getInstance().getParkourHandler().getConfig(id).set("parkour.walkableBlocks", Main.getInstance().getParkourHandler().getWalkableBlocksString(walkableBlocks));

		if (!Main.getInstance().getParkourHandler().getConfig(id).contains("parkour.rewards")) {
			Main.getInstance().getParkourHandler().getConfig(id).set("parkour.rewards", new ArrayList<>());
		} else {
			Main.getInstance().getParkourHandler().getConfig(id).set("parkour.rewards", new ArrayList<>());
			for(Reward reward : rewards) {
				Main.getInstance().getParkourHandler().getConfig(id).set("parkour.rewards." + reward.getId() + ".firstTime", reward.isFirstTime());
				Main.getInstance().getParkourHandler().getConfig(id).set("parkour.rewards." + reward.getId() + ".permission", reward.getPermission());
				Main.getInstance().getParkourHandler().getConfig(id).set("parkour.rewards." + reward.getId() + ".command", reward.getCommand());
			}
		}

		if (!Main.getInstance().getParkourHandler().getConfig(id).contains("parkour.permissionRequired")) {
			Main.getInstance().getParkourHandler().getConfig(id).set("parkour.permissionRequired.enabled", false);
			Main.getInstance().getParkourHandler().getConfig(id).set("parkour.permissionRequired.permission", "aparkour.permission." + id);
			Main.getInstance().getParkourHandler().getConfig(id).set("parkour.permissionRequired.enabled", "&cYou dont have permission to start this parkour!");
		}

		Main.getInstance().getParkourHandler().saveConfig(id);
	}

}
