package me.davidml16.aparkour.handlers;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.davidml16.aparkour.data.UserData;

public class PlayerDataHandler {

	public HashMap<UUID, UserData> data = new HashMap<UUID, UserData>();

	public HashMap<UUID, UserData> getPlayersData() {
		return data;
	}

	public UserData getData(Player p) {
		if (data.containsKey(p.getUniqueId()))
			return data.get(p.getUniqueId());
		return null;
	}

	public UserData getData(UUID uuid) {
		if (data.containsKey(uuid))
			return data.get(uuid);
		return null;
	}

	public boolean playerExists(Player p) {
		return data.containsKey(p.getUniqueId());
	}

	public void loadPlayerData(Player p) {
		data.put(p.getUniqueId(), new UserData(p.getUniqueId()));
	}
	
	public void saveAllPlayerData() {
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			getData(p).save();
		}
	}
	
	public void loadAllPlayerData() {
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			loadPlayerData(p);
		}
	}

	public void savePlayerInventory(Player p) {
		UserData data = getData(p);

		ItemStack[] inventory = new ItemStack[p.getInventory().getContents().length];
		for (int i = 0; i < data.getInventory().length; i++) {
			inventory[i] = p.getInventory().getItem(i);
		}
		data.setInventory(inventory);
		data.setArmour(p.getInventory().getArmorContents());
		
		p.getInventory().clear();
		p.getInventory().setArmorContents(null);
		p.updateInventory();
	}
	
	public void restorePlayerInventory(Player p) {
		UserData data = getData(p);
		p.getInventory().clear();
		p.getInventory().setHelmet(data.getArmour()[3]);
		p.getInventory().setChestplate(data.getArmour()[2]);
		p.getInventory().setLeggings(data.getArmour()[1]);
		p.getInventory().setBoots(data.getArmour()[0]);
		
		ItemStack[] inventory = data.getInventory();
		for (int i = 0; i < inventory.length; i++) {
			p.getInventory().setItem(i, inventory[i]);;
		}
		
		p.updateInventory();

		data.setArmour(null);
		data.setInventory(new ItemStack[p.getInventory().getContents().length]);
	}
	
	public boolean playerHasPermission(Player p) {
		return p.hasPermission("aparkour.admin") || p.isOp();
	}

}
