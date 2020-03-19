package me.davidml16.aparkour.data;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.inventory.ItemStack;

import com.gmail.filoghost.holographicdisplays.api.Hologram;

import me.davidml16.aparkour.Main;

public class Profile {

	private UUID uuid;
	
	private ItemStack[] inventory;
	private ItemStack[] armour;

	private HashMap<String, Integer> lastTimes;
	private HashMap<String, Integer> bestTimes;
	private HashMap<String, Hologram> holograms;

	public Profile(UUID uuid) {
		this.uuid = uuid;
		this.inventory = new ItemStack[36];
		this.lastTimes = Main.getInstance().getDatabaseHandler().getPlayerLastTimes(uuid);
		this.bestTimes = Main.getInstance().getDatabaseHandler().getPlayerBestTimes(uuid);
		this.holograms = new HashMap<String, Hologram>();
	}

	public void save() {
		for(Parkour parkour : Main.getInstance().getParkourHandler().getParkours().values()) {
			try {
				Main.getInstance().getDatabaseHandler().setTimes(uuid, lastTimes.get(parkour.getId()), bestTimes.get(parkour.getId()), parkour.getId());
			} catch (SQLException e) {
				e.printStackTrace();
			}
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

	public ItemStack[] getArmour() {
		return armour;
	}

	public void setArmour(ItemStack[] armour) {
		this.armour = armour;
	}

	public HashMap<String, Hologram> getHolograms() {
		return holograms;
	}

	@Override
	public String toString() {
		return "Profile [uuid=" + uuid + ", inventory=" + Arrays.toString(inventory) + ", armour="
				+ Arrays.toString(armour) + ", lastTimes=" + lastTimes + ", bestTimes=" + bestTimes + ", holograms="
				+ holograms + "]";
	}

}
