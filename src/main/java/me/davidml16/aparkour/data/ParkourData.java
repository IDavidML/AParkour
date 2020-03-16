package me.davidml16.aparkour.data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;

import me.davidml16.aparkour.Main;

public class ParkourData {
	
	private String id;
	private String name;
	private Location spawn;
	private Location start;
	private Location end;
	private Location statsHologram;
	private Location topHologram;
	private List<UUID> players;
	
	public ParkourData(String id, String name, Location spawn, Location start, Location end, Location statsHologram, Location topHologram) {
		this.id = id;
		this.name = name;
		this.spawn = spawn;
		this.start = start;
		this.end = end;
		this.statsHologram = statsHologram;
		this.topHologram = topHologram;
		this.players = new ArrayList<UUID>();
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Location getSpawn() {
		return spawn;
	}

	public void setSpawn(Location spawn) {
		this.spawn = spawn;
	}

	public Location getStart() {
		return start;
	}

	public void setStart(Location start) {
		this.start = start;
	}

	public Location getEnd() {
		return end;
	}

	public void setEnd(Location end) {
		this.end = end;
	}

	public Location getStatsHologram() {
		return statsHologram;
	}

	public void setStatsHologram(Location statsHologram) {
		this.statsHologram = statsHologram;
	}

	public Location getTopHologram() {
		return topHologram;
	}

	public void setTopHologram(Location topHologram) {
		this.topHologram = topHologram;
	}

	public List<UUID> getPlayers() {
		return players;
	}

	public void saveParkour() {
		Main.getInstance().getParkourHandler().getConfig().set("parkours." + id + ".name", name);
		Main.getInstance().getParkourHandler().getConfig().set("parkours." + id + ".spawn", spawn);
		Main.getInstance().getParkourHandler().getConfig().set("parkours." + id + ".start", start);
		Main.getInstance().getParkourHandler().getConfig().set("parkours." + id + ".end", end);
		Main.getInstance().getParkourHandler().getConfig().set("parkours." + id + ".holograms.stats", statsHologram);
		Main.getInstance().getParkourHandler().getConfig().set("parkours." + id + ".holograms.top", topHologram);
		Main.getInstance().getParkourHandler().saveConfig();
	}

	@Override
	public String toString() {
		return "ParkourData [id=" + id + ", name=" + name + ", spawn=" + spawn + ", start=" + start + ", end=" + end
				+ ", statsHologram=" + statsHologram + ", topHologram=" + topHologram + ", players=" + players + "]";
	}

}
