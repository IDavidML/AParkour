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
	private List<Material> walkableBlocks;
	
	public Parkour(String id, String name, Location spawn, Location start, Location end, Location statsHologram, Location topHologram) {
		this.id = id;
		this.name = name;
		this.spawn = spawn;
		this.start = start;
		this.end = end;
		this.statsHologram = statsHologram;
		this.topHologram = topHologram;
		this.walkableBlocks = new ArrayList<Material>();
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

	public List<Material> getWalkableBlocks() {
		return walkableBlocks;
	}

	public void setWalkableBlocks(List<Material> walkableBlocks) {
		this.walkableBlocks = walkableBlocks;
	}

	public void saveParkour() {
		Main.getInstance().getParkourHandler().getConfig().set("parkours." + id + ".name", name);
		Main.getInstance().getParkourHandler().getConfig().set("parkours." + id + ".spawn", spawn);
		Main.getInstance().getParkourHandler().getConfig().set("parkours." + id + ".start", start);
		Main.getInstance().getParkourHandler().getConfig().set("parkours." + id + ".end", end);
		Main.getInstance().getParkourHandler().getConfig().set("parkours." + id + ".holograms.stats", statsHologram);
		Main.getInstance().getParkourHandler().getConfig().set("parkours." + id + ".holograms.top", topHologram);

		Main.getInstance().getParkourHandler().getConfig().set("parkours." + id + ".walkableBlocks", Main.getInstance().getParkourHandler().getWalkableBlocksString(walkableBlocks));
		Main.getInstance().getParkourHandler().saveConfig();
	}

}
