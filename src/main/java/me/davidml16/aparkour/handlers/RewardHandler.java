package me.davidml16.aparkour.handlers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
			if (Main.getInstance().getParkourHandler().getConfig(parkour.getId()).contains("parkour.rewards")) {
				if (Main.getInstance().getParkourHandler().getConfig(parkour.getId()).getConfigurationSection("parkour.rewards") != null) {
					for (String id : Main.getInstance().getParkourHandler().getConfig(parkour.getId()).getConfigurationSection("parkour.rewards").getKeys(false)) {
						if (validRewardData(parkour.getId(), id)) {
							String permission = Main.getInstance().getParkourHandler().getConfig(parkour.getId()).getString("parkour.rewards." + id + ".permission");
							String command = Main.getInstance().getParkourHandler().getConfig(parkour.getId()).getString("parkour.rewards." + id + ".command");
							boolean firstTime = Main.getInstance().getParkourHandler().getConfig(parkour.getId()).getBoolean("parkour.rewards." + id + ".firstTime");
							rewards.add(new Reward(permission, command, firstTime));

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

	public boolean validRewardData(String parkourID, String rewardID) {
		return Main.getInstance().getParkourHandler().getConfig(parkourID).contains("parkour.rewards." + rewardID + ".permission")
				&& Main.getInstance().getParkourHandler().getConfig(parkourID).contains("parkour.rewards." + rewardID + ".command")
				&& Main.getInstance().getParkourHandler().getConfig(parkourID).contains("parkour.rewards." + rewardID + ".firstTime");
	}

	public void giveParkourRewards(Player p, String id, boolean firstTime) {
		Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {
			@Override
			public void run() {
				for (Reward reward : Main.getInstance().getParkourHandler().getParkours().get(id).getRewards()) {
					if(reward.isFirstTime() == firstTime) {
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
