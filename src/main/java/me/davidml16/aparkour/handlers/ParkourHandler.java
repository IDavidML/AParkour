package me.davidml16.aparkour.handlers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.davidml16.aparkour.data.Parkour;
import me.davidml16.aparkour.data.WalkableBlock;
import org.bukkit.GameMode;
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

	private boolean kickFromParkourOnFail;
	private GameMode parkourGamemode;

	public ParkourHandler() {
		this.parkours = new HashMap<String, Parkour>();
		this.parkourFile = new File(Main.getInstance().getDataFolder() + "/parkours.yml");
		this.parkourConfig = YamlConfiguration.loadConfiguration(parkourFile);
		this.kickFromParkourOnFail = Main.getInstance().getConfig().getBoolean("KickFromParkourOnFail.Enabled");
		this.parkourGamemode = GameMode.valueOf(Main.getInstance().getConfig().getString("ParkourGamemode"));
	}

	public HashMap<String, Parkour> getParkours() {
		return parkours;
	}

	public boolean isKickFromParkourOnFail() {
		return kickFromParkourOnFail;
	}

	public void setKickFromParkourOnFail(boolean kickFromParkourOnFail) {
		this.kickFromParkourOnFail = kickFromParkourOnFail;
	}

	public GameMode getParkourGamemode() {
		return parkourGamemode;
	}

	public void setParkourGamemode(GameMode parkourGamemode) {
		this.parkourGamemode = parkourGamemode;
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
			if(!Character.isDigit(id.charAt(0))) {
				if (validParkourData(id)) {
					Location spawn = (Location) parkourConfig.get("parkours." + id + ".spawn");
					Location start = (Location) parkourConfig.get("parkours." + id + ".start");
					Location end = (Location) parkourConfig.get("parkours." + id + ".end");
					Location statsHologram = null;
					Location topHologram = null;

					if (Main.getInstance().isHologramsEnabled()) {
						if ((Location) parkourConfig.get("parkours." + id + ".holograms.stats") != null) {
							statsHologram = (Location) parkourConfig.get("parkours." + id + ".holograms.stats");
						}

						if ((Location) parkourConfig.get("parkours." + id + ".holograms.top") != null) {
							topHologram = (Location) parkourConfig.get("parkours." + id + ".holograms.top");
						}
					}

					if (parkours.size() < 21) {
						Parkour parkour = new Parkour(id, name, spawn, start, end, statsHologram, topHologram);
						parkours.put(id, parkour);

						if (parkourConfig.contains("parkours." + id + ".walkableBlocks")) {
							List<WalkableBlock> walkable = getWalkableBlocks(id);
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
			} else {
				Main.log.sendMessage(ColorManager.translate("    &c'" + name + "' not loaded because parkour id starts with a number!"));
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

	public List<WalkableBlock> getWalkableBlocks(String id) {
		List<WalkableBlock> walkable = new ArrayList<WalkableBlock>();
		if(parkourConfig.contains("parkours." + id + ".walkableBlocks")) {
			for (String block : parkourConfig.getStringList("parkours." + id + ".walkableBlocks")) {
				String[] parts = block.split(":");
				Material material = null;
				material = Material.getMaterial(Integer.parseInt(parts[0]));
				byte data = parts.length == 2 ? Byte.parseByte(parts[1]) : 0;
				WalkableBlock walkableBlockk = new WalkableBlock(Integer.parseInt(parts[0]), data);
				if (material != null && !walkable.contains(walkableBlockk)) {
					if (walkable.size() < 21) {
						walkable.add(walkableBlockk);
					}
				}
			}
		}
		return walkable;
	}

	public List<String> getWalkableBlocksString(List<WalkableBlock> walkable) {
		List<String> list = new ArrayList<String>();
		for(WalkableBlock block : walkable) {
			list.add(Material.getMaterial(block.getId()).getId() + ":" + block.getData());
		}
		return list;
	}

	public void saveWalkableBlocksString(String id, List<WalkableBlock> walkable) {
		List<String> list = new ArrayList<String>();
		for(WalkableBlock block : walkable) {
			list.add(Material.getMaterial(block.getId()).getId() + ":" + block.getData());
		}

		getConfig().set("parkours." + id + ".walkableBlocks", list);
		saveConfig();
	}

}
