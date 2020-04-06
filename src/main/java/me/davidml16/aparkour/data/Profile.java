package me.davidml16.aparkour.data;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import com.gmail.filoghost.holographicdisplays.api.Hologram;

import me.davidml16.aparkour.Main;
import org.bukkit.potion.PotionEffect;

public class Profile {

	private Main main;

	private UUID uuid;
	private Parkour parkour;

	private ItemStack[] inventory;
	private ItemStack[] armor;

	private Integer lastCheckpoint;
	private Location lastCheckpointLocation;

	private GameMode lastGamemode;

	private Collection<PotionEffect> potionEffects;

	private HashMap<String, Integer> lastTimes;
	private HashMap<String, Integer> bestTimes;
	private HashMap<String, Hologram> holograms;

	public Profile(Main main, UUID uuid) {
		this.main = main;
		this.uuid = uuid;
		this.parkour = null;
		this.inventory = new ItemStack[36];
		this.armor = new ItemStack[4];
		this.potionEffects = null;
		this.lastGamemode = null;
		this.lastCheckpoint = -1;
		this.lastCheckpointLocation = null;
		this.lastTimes = this.main.getDatabaseHandler().getPlayerLastTimes(uuid);
		this.bestTimes = this.main.getDatabaseHandler().getPlayerBestTimes(uuid);
		this.holograms = new HashMap<String, Hologram>();
	}

	public void save() {
		for(Parkour parkour : main.getParkourHandler().getParkours().values()) {
			try {
				main.getDatabaseHandler().setTimes(uuid, lastTimes.get(parkour.getId()), bestTimes.get(parkour.getId()), parkour.getId());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void save(String id) {
		try {
			main.getDatabaseHandler().setTimes(uuid, lastTimes.get(id), bestTimes.get(id), id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public HashMap<String, Integer> getLastTimes() {
		return lastTimes;
	}

	public HashMap<String, Integer> getBestTimes() {
		return bestTimes;
	}

	public void setLastTime(int total, String parkour) {
		lastTimes.put(parkour, total);
	}

	public void setBestTime(int total, String parkour) {
		bestTimes.put(parkour, total);
	}

	public boolean isBestTime(int time, String parkour) {
		return time < bestTimes.get(parkour);
	}

	public ItemStack[] getInventory() {
		return inventory;
	}

	public void setInventory(ItemStack[] inventory) {
		this.inventory = inventory;
	}

	public ItemStack[] getArmor() {
		return armor;
	}

	public void setArmor(ItemStack[] armor) {
		this.armor = armor;
	}

	public Collection<PotionEffect> getPotionEffects() {
		return potionEffects;
	}

	public void setPotionEffects(Collection<PotionEffect> potionEffects) {
		this.potionEffects = potionEffects;
	}

	public GameMode getLastGamemode() {
		return lastGamemode;
	}

	public void setLastGamemode(GameMode lastGamemode) {
		this.lastGamemode = lastGamemode;
	}

	public HashMap<String, Hologram> getHolograms() {
		return holograms;
	}

	public Parkour getParkour() {
		return parkour;
	}

	public void setParkour(Parkour parkour) {
		this.parkour = parkour;
	}

	public Integer getLastCheckpoint() {
		return lastCheckpoint;
	}

	public void setLastCheckpoint(Integer lastCheckpoint) {
		this.lastCheckpoint = lastCheckpoint;
	}

	public Location getLastCheckpointLocation() {
		return lastCheckpointLocation;
	}

	public void setLastCheckpointLocation(Location lastCheckpointLocation) {
		this.lastCheckpointLocation = lastCheckpointLocation;
	}
}
