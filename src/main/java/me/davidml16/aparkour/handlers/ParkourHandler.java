package me.davidml16.aparkour.handlers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.davidml16.aparkour.data.Parkour;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.managers.ColorManager;

public class ParkourHandler {

	private HashMap<String, Parkour> parkours;
	private File parkourFile;
	private FileConfiguration parkourConfig;

	public ParkourHandler() {
		this.parkours = new HashMap<String, Parkour>();
		this.parkourFile = new File(Main.getInstance().getDataFolder() + "/parkours.yml");
		this.parkourConfig = YamlConfiguration.loadConfiguration(parkourFile);
	}

	public HashMap<String, Parkour> getParkours() {
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
		for (Parkour parkour : parkours.values())
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
				Location statsHologram = null;
				Location topHologram = null;
				
				if (Main.getInstance().isHologramsEnabled()) {
					if((Location) parkourConfig.get("parkours." + id + ".holograms.stats") != null) {
                        statsHologram = (Location) parkourConfig.get("parkours." + id + ".holograms.stats");
                    }

                    if((Location) parkourConfig.get("parkours." + id + ".holograms.top") != null) {
                        topHologram = (Location) parkourConfig.get("parkours." + id + ".holograms.top");
                    }
				}

				if (parkours.size() < 21) {
					Parkour parkour = new Parkour(id, name, spawn, start, end, statsHologram, topHologram);
					parkours.put(id, parkour);

					if(parkourConfig.contains("parkours." + id + ".walkableBlocks")) {
						List<Material> walkable = getWalkableBlocks(id);
						parkour.setWalkableBlocks(walkable);
						saveWalkableBlocksString(id, walkable);
						saveConfig();
					}

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
			return parkourConfig.contains("parkours." + id + ".spawn")
					&& parkourConfig.contains("parkours." + id + ".start")
					&& parkourConfig.contains("parkours." + id + ".end");
	}

	public Parkour getParkourById(String id) {
		for (Parkour parkour : parkours.values()) {
			if (parkour.getId().equalsIgnoreCase(id))
				return parkour;
		}
		return null;
	}

	public Parkour getParkourByLocation(Location loc) {
		for (Parkour parkour : parkours.values()) {
			if (loc.equals(parkour.getStart()) || loc.equals(parkour.getEnd()))
				return parkours.get(parkour.getId());
		}
		return null;
	}

	public List<Material> getWalkableBlocks(String id) {
		List<Material> walkable = new ArrayList<Material>();
		if(parkourConfig.contains("parkours." + id + ".walkableBlocks")) {
			for (String mat : parkourConfig.getStringList("parkours." + id + ".walkableBlocks")) {
				Material material = null;
				material = Material.matchMaterial(mat);
				if (material != null && !walkable.contains(material)) {
					if (walkable.size() < 21) {
						walkable.add(material);
					}
				}
			}
		}
		return walkable;
	}

	public List<String> getWalkableBlocksString(List<Material> walkable) {
		List<String> list = new ArrayList<String>();
		for(Material material : walkable) {
			list.add(material.name());
		}
		return list;
	}

	public void saveWalkableBlocksString(String id, List<Material> walkable) {
		List<String> list = new ArrayList<String>();
		for(Material material : walkable) {
			list.add(material.name());
		}

		getConfig().set("parkours." + id + ".walkableBlocks", list);
		saveConfig();
	}

}
