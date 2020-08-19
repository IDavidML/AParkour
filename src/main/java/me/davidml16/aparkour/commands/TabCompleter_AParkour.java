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

	private Main main;
	public TabCompleter_AParkour(Main main) {
		this.main = main;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		if (!(sender instanceof Player)) {
			return null;
		}

		Player p = (Player) sender;

		List<String> list = new ArrayList<String>();
		List<String> auto = new ArrayList<String>();

		if (args.length == 1) {

			if(main.isJoinByGUI())
				list.add("play");

			list.add("stats");
			list.add("top");
			list.add("list");
			list.add("cancel");
			list.add("checkpoint");
			if (main.getPlayerDataHandler().playerHasPermission(p, "aparkour.admin")) {
				list.add("create");
				list.add("remove");
				list.add("setup");
				list.add("reload");
				list.add("reset");
			}
		} else if (args[0].equalsIgnoreCase("remove")) {
			if (main.getPlayerDataHandler().playerHasPermission(p, "aparkour.admin")) {
				for (File file : Objects.requireNonNull(new File(main.getDataFolder(), "parkours").listFiles())) {
					list.add(file.getName().replace(".yml", ""));
				}
			}
		} else if (args[0].equalsIgnoreCase("setup")) {
			if (main.getPlayerDataHandler().playerHasPermission(p, "aparkour.admin")) {
				for (File file : Objects.requireNonNull(new File(main.getDataFolder(), "parkours").listFiles())) {
					list.add(file.getName().replace(".yml", ""));
				}
			}
		} else if (args[0].equalsIgnoreCase("play")) {
			if (main.getPlayerDataHandler().playerHasPermission(p, "aparkour.admin")) {
				list.addAll(main.getParkourHandler().getParkours().keySet());
			}
		} else if (args[0].equalsIgnoreCase("top")) {
			if (main.getPlayerDataHandler().playerHasPermission(p, "aparkour.admin")) {
				list.addAll(main.getParkourHandler().getParkours().keySet());
			}
		} else if (args[0].equalsIgnoreCase("reset")) {
			if (args.length == 2) {
				if (main.getPlayerDataHandler().playerHasPermission(p, "aparkour.admin")) {
					for (Player target : main.getServer().getOnlinePlayers()) {
						list.add(target.getName());
					}
				}
			} else {
				if (main.getPlayerDataHandler().playerHasPermission(p, "aparkour.admin")) {
					list.addAll(main.getParkourHandler().getParkours().keySet());
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