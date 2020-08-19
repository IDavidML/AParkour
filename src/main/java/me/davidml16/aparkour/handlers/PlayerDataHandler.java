package me.davidml16.aparkour.handlers;

import java.util.HashMap;
import java.util.UUID;

import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.managers.ColorManager;
import me.davidml16.aparkour.utils.RepeatingTask;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.davidml16.aparkour.data.Profile;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitTask;

public class PlayerDataHandler {

	public HashMap<UUID, Profile> data = new HashMap<UUID, Profile>();

	private Main main;
	public PlayerDataHandler(Main main) {
		this.main = main;
	}

	public HashMap<UUID, Profile> getPlayersData() {
		return data;
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
		Profile profile = new Profile(main, p.getUniqueId());
		data.put(p.getUniqueId(), profile);

		main.getStatsHologramManager().loadStatsHolograms(p);

		main.getDatabaseHandler().getPlayerLastTimes(p.getUniqueId()).thenAccept(lastTimes -> {
			profile.setLastTimes((HashMap<String, Long>) lastTimes);
		});

		main.getDatabaseHandler().getPlayerBestTimes(p.getUniqueId()).thenAccept(bestTimes -> {
			profile.setBestTimes((HashMap<String, Long>) bestTimes);
		});

		for(String parkour : main.getParkourHandler().getParkours().keySet()) {
			RepeatingTask task = new RepeatingTask(main, 0, 10) {
				@Override
				public void run() {
					if(main.getStatsHologramManager().haveParkourData(p, parkour)) {
						main.getStatsHologramManager().reloadStatsHologram(p, parkour);
						cancel();
					}
				}
			};
		}

	}
	
	public void saveAllPlayerData() {
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			getData(p).save();
		}
	}
	
	public void loadAllPlayerData() {
		data.clear();
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
		data.setLastFlyMode(p.isFlying() || p.getAllowFlight());

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
		p.setAllowFlight(data.isLastFlyMode());

		p.updateInventory();

		data.setLastGamemode(null);
		data.setArmor(new ItemStack[4]);
		data.setInventory(new ItemStack[p.getInventory().getContents().length]);
		data.setPotionEffects(null);
	}
	
	public boolean playerHasPermission(Player p, String permission) {
		return p.hasPermission(permission) || p.isOp();
	}

	public String getPlayerName(World world, String name) {
		if(main.vaultEnabled())
			return ColorManager.translate(main.getChat().getPlayerPrefix(world, name) + name);
		else
			return ColorManager.translate(name);
	}

}
