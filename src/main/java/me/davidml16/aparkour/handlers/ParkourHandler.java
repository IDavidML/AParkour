package me.davidml16.aparkour.handlers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.data.ParkourData;
import me.davidml16.aparkour.managers.ColorManager;

public class ParkourHandler {

	private HashMap<String, ParkourData> parkours;
	private File parkourFile;
	private FileConfiguration parkourConfig;

	public ParkourHandler() {
		this.parkours = new HashMap<String, ParkourData>();
		this.parkourFile = new File(Main.getInstance().getDataFolder() + "/parkours.yml");
		this.parkourConfig = YamlConfiguration.loadConfiguration(parkourFile);
	}

	public HashMap<String, ParkourData> getParkours() {
		return parkours;
	}

	public void saveConfig() {
		try {
			if (!parkourConfig.contains("parkours"))
				parkourConfig.createSection("parkours");

			parkourConfig.save(parkourFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public FileConfiguration getConfig() {
		return parkourConfig;
	}

	public void saveParkours() {
		for (ParkourData parkour : parkours.values())
			parkour.saveParkour();
	}

	public void loadParkours() {
		Main.log.sendMessage(ColorManager.translate(""));
		Main.log.sendMessage(ColorManager.translate("  &eLoading parkours:"));
		for (String id : parkourConfig.getConfigurationSection("parkours").getKeys(false)) {
			String name = parkourConfig.getString("parkours." + id + ".name");
			if (validParkourData(id)) {
				Location spawn = (Location) parkourConfig.get("parkours." + id + ".spawn");
				Location start = (Location) parkourConfig.get("parkours." + id + ".start");
				Location end = (Location) parkourConfig.get("parkours." + id + ".end");
				Location statsHologram; 
				Location topHologram;
				
				if (Main.getInstance().getStatsHologramManager().isHologramsEnabled()) {
					statsHologram = (Location) parkourConfig.get("parkours." + id + ".holograms.stats");
					topHologram = (Location) parkourConfig.get("parkours." + id + ".holograms.top");
				} else {
					statsHologram = null;
					topHologram = null;
				}

				if (parkours.size() < 21) {
					parkours.put(id, new ParkourData(id, name, spawn, start, end, statsHologram, topHologram));
					Main.log.sendMessage(ColorManager.translate("    &a'" + name + "' loaded!"));
				} else {
					Main.log.sendMessage(ColorManager
							.translate("    &c'" + name + "' not loaded because maximum parkours limit reached!"));
				}
			} else {
				Main.log.sendMessage(ColorManager.translate("    &c'" + name + "' not loaded because parkour data is not correct!"));
			}
		}
		
		if(parkours.size() == 0)
			Main.log.sendMessage(ColorManager.translate("    &cNo parkour has been loaded!"));
		
		Main.log.sendMessage(ColorManager.translate(""));
	}

	public boolean validParkourData(String id) {
		if (Main.getInstance().getStatsHologramManager().isHologramsEnabled())
			return parkourConfig.contains("parkours." + id + ".spawn")
					&& parkourConfig.contains("parkours." + id + ".start")
					&& parkourConfig.contains("parkours." + id + ".end")
					&& parkourConfig.contains("parkours." + id + ".holograms.stats")
					&& parkourConfig.contains("parkours." + id + ".holograms.top");
		else
			return parkourConfig.contains("parkours." + id + ".spawn")
					&& parkourConfig.contains("parkours." + id + ".start")
					&& parkourConfig.contains("parkours." + id + ".end");
	}

	public ParkourData getParkourByLocation(Location loc) {
		for (ParkourData parkour : parkours.values()) {
			if (loc.equals(parkour.getStart()) || loc.equals(parkour.getEnd()))
				return parkours.get(parkour.getId());
		}
		return null;
	}

	public ParkourData getParkourByPlayer(Player p) {
		for (ParkourData parkour : parkours.values()) {
			if (parkour.getPlayers().contains(p.getUniqueId()))
				return parkours.get(parkour.getId());
		}
		return null;
	}

}
