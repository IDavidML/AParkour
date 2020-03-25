package me.davidml16.aparkour.commands;

import java.util.ArrayList;
import java.util.List;

import me.davidml16.aparkour.data.Parkour;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.davidml16.aparkour.Main;

public class autoCompleter_AParkour implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		if (!(sender instanceof Player)) {
			return null;
		}

		Player p = (Player) sender;

		List<String> list = new ArrayList<String>();
		List<String> auto = new ArrayList<String>();

		if (args.length == 1) {
			list.add("stats");
			list.add("top");
			list.add("list");
			if (Main.getInstance().getPlayerDataHandler().playerHasPermission(p)) {
				list.add("create");
				list.add("remove");
				list.add("set");
				list.add("config");
				list.add("reload");
			}
		} else if (args[0].equalsIgnoreCase("remove")) {
			if (Main.getInstance().getPlayerDataHandler().playerHasPermission(p)) {
				list.addAll(Main.getInstance().getParkourHandler().getConfig().getConfigurationSection("parkours").getKeys(false));
			}
		} else if (args[0].equalsIgnoreCase("config")) {
			if (Main.getInstance().getPlayerDataHandler().playerHasPermission(p)) {
				list.addAll(Main.getInstance().getParkourHandler().getConfig().getConfigurationSection("parkours").getKeys(false));
			}
		} else if (args[0].equalsIgnoreCase("set")) {
			if (args.length == 3) {
				if (Main.getInstance().getPlayerDataHandler().playerHasPermission(p)) {
					list.add("name");
					list.add("spawn");
					list.add("start");
					list.add("end");
					list.add("stats");
					list.add("top");
				}
			} else {
				if (Main.getInstance().getPlayerDataHandler().playerHasPermission(p)) {
					list.addAll(Main.getInstance().getParkourHandler().getConfig().getConfigurationSection("parkours").getKeys(false));
				}
			}
		}

		for (String s : list) {
			if (s.startsWith(args[args.length - 1])) {
				auto.add(s);
			}
		}

		return auto.isEmpty() ? list : auto;
	}

}