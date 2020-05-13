package me.davidml16.aparkour.data;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import javafx.util.Pair;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import com.gmail.filoghost.holographicdisplays.api.Hologram;

import me.davidml16.aparkour.Main;
import org.bukkit.potion.PotionEffect;

public class Profile {

	private Main main;

	private UUID uuid;

	private ItemStack[] inventory;
	private ItemStack[] armor;

	private GameMode lastGamemode;
	private boolean lastFlyMode;

	private Collection<PotionEffect> potionEffects;

	private HashMap<String, Long> lastTimes;
	private HashMap<String, Long> bestTimes;
	private HashMap<String, Hologram> holograms;

	public Profile(Main main, UUID uuid) {
		this.main = main;
		this.uuid = uuid;
		this.inventory = new ItemStack[36];
		this.armor = new ItemStack[4];
		this.potionEffects = null;
		this.lastGamemode = null;
		this.lastFlyMode = false;
		this.lastTimes = new HashMap<>();
		this.bestTimes = new HashMap<>();
		this.holograms = new HashMap<>();
	}

	public void save() {
		for(String parkour : main.getParkourHandler().getParkours().keySet()) {
			try {
				main.getDatabaseHandler().setTimes(uuid, lastTimes.get(parkour), bestTimes.get(parkour), parkour);
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

	public void setLastTimes(HashMap<String, Long> lastTimes) {
		this.lastTimes = lastTimes;
	}

	public void setBestTimes(HashMap<String, Long> bestTimes) {
		this.bestTimes = bestTimes;
	}

	public HashMap<String, Long> getLastTimes() {
		return lastTimes;
	}

	public HashMap<String, Long> getBestTimes() {
		return bestTimes;
	}

	public void setLastTime(Long total, String parkour) {
		lastTimes.put(parkour, total);
	}

	public void setBestTime(Long total, String parkour) {
		bestTimes.put(parkour, total);
	}

	public boolean isBestTime(Long time, String parkour) {
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

	public boolean isLastFlyMode() { return lastFlyMode; }

	public void setLastFlyMode(boolean lastFlyMode) { this.lastFlyMode = lastFlyMode; }

	public HashMap<String, Hologram> getHolograms() {
		return holograms;
	}

	@Override
	public String toString() {
		return "Profile{" +
				"main=" + main +
				", uuid=" + uuid +
				", inventory=" + Arrays.toString(inventory) +
				", armor=" + Arrays.toString(armor) +
				", lastGamemode=" + lastGamemode +
				", lastFlyMode=" + lastFlyMode +
				", potionEffects=" + potionEffects +
				", lastTimes=" + lastTimes +
				", bestTimes=" + bestTimes +
				", holograms=" + holograms +
				'}';
	}
}
