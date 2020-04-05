package me.davidml16.aparkour.handlers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.davidml16.aparkour.data.Parkour;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.data.Reward;
import me.davidml16.aparkour.managers.ColorManager;
import org.bukkit.permissions.Permission;

public class RewardHandler {

	public void loadRewards() {
		Main.log.sendMessage(ColorManager.translate("  &eLoading rewards:"));
		for(Parkour parkour : Main.getInstance().getParkourHandler().getParkours().values()) {
			List<Reward> rewards = new ArrayList<Reward>();
			FileConfiguration config = Main.getInstance().getParkourHandler().getConfig(parkour.getId());
			if (config.contains("parkour.rewards")) {
				if (config.getConfigurationSection("parkour.rewards") != null) {
					for (String rewardid : config.getConfigurationSection("parkour.rewards").getKeys(false)) {
						if(!config.contains("parkour.rewards." + rewardid + ".chance")) {
							config.set("parkour.rewards." + rewardid + ".chance", 100);
							Main.getInstance().getParkourHandler().saveConfig(parkour.getId());
						}
						if (validRewardData(parkour.getId(), rewardid)) {
							String permission = config.getString("parkour.rewards." + rewardid + ".permission");
							String command = config.getString("parkour.rewards." + rewardid + ".command");
							boolean firstTime = config.getBoolean("parkour.rewards." + rewardid + ".firstTime");
							int chance = config.getInt("parkour.rewards." + rewardid + ".chance");
							rewards.add(new Reward(rewardid, permission, command, firstTime, chance));

							if (Main.getInstance().getServer().getPluginManager().getPermission(permission) == null) {
								Main.getInstance().getServer().getPluginManager().addPermission(new Permission(permission));
							}
						}
					}
				}
				parkour.setRewards(rewards);
			}
			Main.log.sendMessage(ColorManager.translate("    &a'" + parkour.getName() + "' &7- " + (rewards.size() > 0 ? "&a" : "&c") + rewards.size() + " rewards"));
		}

		if(Main.getInstance().getParkourHandler().getParkours().size() == 0)
			Main.log.sendMessage(ColorManager.translate("    &cNo rewards has been loaded!"));

		Main.log.sendMessage(ColorManager.translate(""));
	}

	private boolean validRewardData(String parkourID, String rewardID) {
		FileConfiguration config = Main.getInstance().getParkourHandler().getConfig(parkourID);
		return config.contains("parkour.rewards." + rewardID + ".permission")
				&& config.contains("parkour.rewards." + rewardID + ".command")
				&& config.contains("parkour.rewards." + rewardID + ".firstTime")
				&& config.contains("parkour.rewards." + rewardID + ".chance");
	}

	public void giveParkourRewards(Player p, String id, boolean firstTime) {
		Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
			Random random = new Random();
			for (Reward reward : Main.getInstance().getParkourHandler().getParkours().get(id).getRewards()) {
				if(reward.isFirstTime() == firstTime) {
					if(random.nextInt() * 100 <= reward.getChance()) {
						if (!reward.getPermission().equalsIgnoreCase("*")) {
							if (Main.getInstance().getPlayerDataHandler().playerHasPermission(p, reward.getPermission())) {
								Main.getInstance().getServer().dispatchCommand(Main.getInstance().getServer().getConsoleSender(), reward.getCommand().replaceAll("%player%", p.getName()));
							}
						} else {
							Main.getInstance().getServer().dispatchCommand(Main.getInstance().getServer().getConsoleSender(), reward.getCommand().replaceAll("%player%", p.getName()));
						}
					}
				}
			}
		}, 1L);
	}

}
