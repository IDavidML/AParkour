package me.davidml16.aparkour.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import me.davidml16.aparkour.data.Parkour;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.davidml16.aparkour.Main;

public class TabCompleter_AParkour implements TabCompleter {

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
			list.add("cancel");
			list.add("checkpoint");
			if (Main.getInstance().getPlayerDataHandler().playerHasPermission(p, "aparkour.admin")) {
				list.add("create");
				list.add("remove");
				list.add("set");
				list.add("setup");
				list.add("reload");
			}
		} else if (args[0].equalsIgnoreCase("remove")) {
			if (Main.getInstance().getPlayerDataHandler().playerHasPermission(p, "aparkour.admin")) {
				for (File file : Objects.requireNonNull(new File(Main.getInstance().getDataFolder(), "parkours").listFiles())) {
					list.add(file.getName().toLowerCase().replace(".yml", ""));
				}
			}
		} else if (args[0].equalsIgnoreCase("setup")) {
			if (Main.getInstance().getPlayerDataHandler().playerHasPermission(p, "aparkour.admin")) {
				for (File file : Objects.requireNonNull(new File(Main.getInstance().getDataFolder(), "parkours").listFiles())) {
					list.add(file.getName().toLowerCase().replace(".yml", ""));
				}
			}
		} else if (args[0].equalsIgnoreCase("set")) {
			if (args.length == 3) {
				if (Main.getInstance().getPlayerDataHandler().playerHasPermission(p, "aparkour.admin")) {
					list.add("name");
					list.add("spawn");
					list.add("start");
					list.add("end");
					list.add("stats");
					list.add("top");
				}
			} else {
				if (Main.getInstance().getPlayerDataHandler().playerHasPermission(p, "aparkour.admin")) {
					for (File file : Objects.requireNonNull(new File(Main.getInstance().getDataFolder(), "parkours").listFiles())) {
						list.add(file.getName().toLowerCase().replace(".yml", ""));
					}
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