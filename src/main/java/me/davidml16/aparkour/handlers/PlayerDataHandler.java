package me.davidml16.aparkour.handlers;

import java.util.HashMap;
import java.util.UUID;

import me.davidml16.aparkour.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.davidml16.aparkour.data.Profile;
import org.bukkit.potion.PotionEffect;

public class PlayerDataHandler {

	public HashMap<UUID, Profile> data = new HashMap<UUID, Profile>();

	public HashMap<UUID, Profile> getPlayersData() {
		return data;
	}

	private Main main;
	public PlayerDataHandler(Main main) {
		this.main = main;
	}

	public Profile getData(Player p) {
		if (data.containsKey(p.getUniqueId()))
			return data.get(p.getUniqueId());
		return null;
	}

	public Profile getData(UUID uuid) {
		if (data.containsKey(uuid))
			return data.get(uuid);
		return null;
	}

	public boolean playerExists(Player p) {
		return data.containsKey(p.getUniqueId());
	}

	public void loadPlayerData(Player p) {
		data.put(p.getUniqueId(), new Profile(main, p.getUniqueId()));
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
		Profile data = getData(p);

		ItemStack[] inventory = new ItemStack[p.getInventory().getContents().length];
		for (int i = 0; i < data.getInventory().length; i++) {
			inventory[i] = p.getInventory().getItem(i);
		}
		data.setInventory(inventory);
		data.setArmor(p.getInventory().getArmorContents());
		data.setPotionEffects(p.getActivePotionEffects());
		data.setLastGamemode(p.getGameMode());

		for(PotionEffect effect : p.getActivePotionEffects()) {
			p.removePotionEffect(effect.getType());
		}

		p.setGameMode(main.getParkourHandler().getParkourGamemode());
		p.getInventory().clear();
		p.getInventory().setArmorContents(null);
		p.updateInventory();
	}

	public void restorePlayerInventory(Player p) {
		Profile data = getData(p);

		p.getInventory().clear();

		ItemStack[] inventory = data.getInventory();
		for (int i = 0; i < inventory.length; i++) {
			p.getInventory().setItem(i, inventory[i]);
		}

		p.getInventory().setHelmet(data.getArmor()[3]);
		p.getInventory().setChestplate(data.getArmor()[2]);
		p.getInventory().setLeggings(data.getArmor()[1]);
		p.getInventory().setBoots(data.getArmor()[0]);

		p.addPotionEffects(data.getPotionEffects());
		p.setGameMode(data.getLastGamemode());

		p.updateInventory();

		data.setLastGamemode(null);
		data.setArmor(new ItemStack[4]);
		data.setInventory(new ItemStack[p.getInventory().getContents().length]);
		data.setPotionEffects(null);
	}
	
	public boolean playerHasPermission(Player p, String permission) {
		return p.hasPermission(permission) || p.isOp();
	}

}
