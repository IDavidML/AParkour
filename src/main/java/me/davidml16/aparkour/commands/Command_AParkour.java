package me.davidml16.aparkour.commands;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import me.davidml16.aparkour.data.Parkour;
import me.davidml16.aparkour.managers.PluginManager;
import me.davidml16.aparkour.utils.ActionBar;
import me.davidml16.aparkour.utils.LocationUtil;
import me.davidml16.aparkour.utils.RestartItemUtil;
import me.davidml16.aparkour.utils.SoundUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.managers.ColorManager;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class Command_AParkour implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String noperms = Main.getInstance().getLanguageHandler().getMessage("COMMANDS_NOPERMS", true);

        if (!(sender instanceof Player)) {
            sender.sendMessage(ColorManager.translate("ccThe commands only can be use by players!"));
            return true;
        }

        Player p = (Player) sender;

        if (args.length == 0) {
            sendCommandHelp(p);
            return true;
        }

        if (args[0].equalsIgnoreCase("stats")) {
            if (Main.getInstance().getParkourHandler().getParkours().size() > 0) {
                Main.getInstance().getStatsGUI().open(p);
            } else {
                p.sendMessage(Main.getInstance().getLanguageHandler().getMessage("COMMANDS_NOSTATS", true));
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("list")) {
            sendParkourList(p);
            return true;
        }

        if (args[0].equalsIgnoreCase("rltop")) {
            Main.getInstance().getTopHologramManager().reloadTopHolograms();
            return true;
        }

        if (args[0].equalsIgnoreCase("top")) {
            if (Main.getInstance().getParkourHandler().getParkours().size() > 0) {
                Main.getInstance().getRankingsGUI().open(p);
            } else {
                p.sendMessage(Main.getInstance().getLanguageHandler().getMessage("COMMANDS_NOPARKOURS", true));
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {

            Main.getInstance().reloadConfig();

            PluginManager.reloadAll();
            p.sendMessage(Main.getInstance().getLanguageHandler().getMessage("COMMANDS_RELOAD", true));

            return true;
        }

        if (args[0].equalsIgnoreCase("create")) {
            if (!Main.getInstance().getPlayerDataHandler().playerHasPermission(p)) {
                p.sendMessage(noperms);
                return false;
            }

            if (args.length == 1 || args.length == 2) {
                p.sendMessage(ColorManager.translate(
                        Main.getInstance().getLanguageHandler().getPrefix() + "&cUsage: /aparkour create [id] [name]"));
                return true;
            }

            String id = args[1];
            if (Main.getInstance().getParkourHandler().getConfig().contains("parkours." + id)) {
                p.sendMessage(ColorManager.translate(
                        Main.getInstance().getLanguageHandler().getPrefix() + "&cThis parkour already exists!"));
                return true;
            }
            Main.getInstance().getParkourHandler().getConfig().set("parkours." + id + ".name", args[2]);
            Main.getInstance().getParkourHandler().getConfig().set("parkours." + id + ".walkableBlocks", new ArrayList<>());
            Main.getInstance().getParkourHandler().saveConfig();
            Main.getInstance().getConfigGUI().loadGUI(id);
            Main.getInstance().getWalkableBlocksGUI().loadGUI(id);

            p.sendMessage(ColorManager.translate(Main.getInstance().getLanguageHandler().getPrefix()
                    + "&aSuccesfully created parkour &e" + id + " &awith the name &e" + args[2]));
            return true;
        }

        if (args[0].equalsIgnoreCase("config")) {
            if (!Main.getInstance().getPlayerDataHandler().playerHasPermission(p)) {
                p.sendMessage(noperms);
                return false;
            }

            if (args.length == 1) {
                p.sendMessage(ColorManager.translate(
                        Main.getInstance().getLanguageHandler().getPrefix() + "&cUsage: /aparkour config [id]"));
                return true;
            }

            String id = args[1];
            if (!Main.getInstance().getParkourHandler().getConfig().contains("parkours." + id)) {
                p.sendMessage(ColorManager.translate(
                        Main.getInstance().getLanguageHandler().getPrefix() + "&cThis parkour doesn't exists!"));
                return true;
            }

            Main.getInstance().getConfigGUI().open(p, id);
            return true;
        }

        if (args[0].equalsIgnoreCase("remove")) {
            if (!Main.getInstance().getPlayerDataHandler().playerHasPermission(p)) {
                p.sendMessage(noperms);
                return false;
            }

            if (args.length == 1) {
                p.sendMessage(ColorManager.translate(
                        Main.getInstance().getLanguageHandler().getPrefix() + "&cUsage: /aparkour remove [id]"));
                return true;
            }
            String id = args[1];
            if (!Main.getInstance().getParkourHandler().getConfig().contains("parkours." + id)) {
                p.sendMessage(ColorManager.translate(
                        Main.getInstance().getLanguageHandler().getPrefix() + "&cThis parkour doesn't exists!"));
                return true;
            }

            for (Player pl : Bukkit.getOnlinePlayers())
                Main.getInstance().getStatsHologramManager().removeStatsHologram(pl, id);

            Main.getInstance().getTopHologramManager().removeHologram(id);

            Main.getInstance().getParkourHandler().getConfig().set("parkours." + id, null);
            Main.getInstance().getParkourHandler().getParkours().remove(id);
            Main.getInstance().getParkourHandler().saveConfig();

            if(Main.getInstance().getConfigGUI().getGuis().containsKey(id)) {
                for(UUID uuid : Main.getInstance().getConfigGUI().getOpened().keySet()) {
                    if(Main.getInstance().getConfigGUI().getOpened().get(uuid).equals(id)) {
                        Bukkit.getPlayer(uuid).closeInventory();
                    }
                }
                Main.getInstance().getConfigGUI().getGuis().remove(id);
            }

            if(Main.getInstance().getWalkableBlocksGUI().getGuis().containsKey(id)) {
                for(UUID uuid : Main.getInstance().getWalkableBlocksGUI().getOpened().keySet()) {
                    if(Main.getInstance().getWalkableBlocksGUI().getOpened().get(uuid).equals(id)) {
                        Bukkit.getPlayer(uuid).closeInventory();
                    }
                }
                Main.getInstance().getWalkableBlocksGUI().getGuis().remove(id);
            }

            p.sendMessage(ColorManager.translate(
                    Main.getInstance().getLanguageHandler().getPrefix() + "&aSuccesfully deleted parkour &e" + id));
            return true;
        }

        if (args[0].equalsIgnoreCase("set")) {
            if (!Main.getInstance().getPlayerDataHandler().playerHasPermission(p)) {
                p.sendMessage(noperms);
                return false;
            }

            if (args.length == 1 || args.length == 2) {
                p.sendMessage(ColorManager.translate(Main.getInstance().getLanguageHandler().getPrefix()
                        + "&cUsage: /aparkour set [id] [name,spawn,start,end,stats,top]"));
                return true;
            }

            String id = args[1];
            if (!Main.getInstance().getParkourHandler().getConfig().contains("parkours." + id)) {
                p.sendMessage(ColorManager.translate(
                        Main.getInstance().getLanguageHandler().getPrefix() + "&cThis parkour doesn't exists!"));
                return true;
            }

            if (args[2].equalsIgnoreCase("name")) {
                if (args.length == 3) {
                    p.sendMessage(ColorManager.translate(Main.getInstance().getLanguageHandler().getPrefix()
                            + "&cUsage: /aparkour set [id] name newName"));
                    return true;
                }

                String name = args[3];
                Main.getInstance().getParkourHandler().getConfig().set("parkours." + id + ".name", name);
                Main.getInstance().getParkourHandler().saveConfig();
                p.sendMessage(ColorManager.translate(Main.getInstance().getLanguageHandler().getPrefix()
                        + "&aSuccesfully renamed parkour &e" + id + " &awith the new name &e" + name));
                return true;
            }

            if (args[2].equalsIgnoreCase("spawn")) {
                LocationUtil.setPosition(p, id, "spawn");
                return true;
            }

            if (args[2].equalsIgnoreCase("start")) {
                LocationUtil.setPosition(p, id, "start");
                return true;
            }

            if (args[2].equalsIgnoreCase("end")) {
                LocationUtil.setPosition(p, id, "end");
                return true;
            }

            if (args[2].equalsIgnoreCase("stats")) {
                LocationUtil.setHologram(p, id, "stats");
                return true;
            }

            if (args[2].equalsIgnoreCase("top")) {
                LocationUtil.setHologram(p, id, "top");
                return true;
            }

            p.sendMessage(ColorManager.translate(Main.getInstance().getLanguageHandler().getPrefix()
                    + "&cUsage: /aparkour set [id] [name,spawn,start,end,stats,top]"));
            return true;
        }
        return false;
    }

    private void sendParkourList(Player p) {
        p.sendMessage("");
        p.sendMessage(ColorManager.translate("  &a&lParkours availables:"));
        p.sendMessage("");

        if (Main.getInstance().getParkourHandler().getParkours().size() == 0) {
            p.sendMessage(ColorManager.translate("    &7- &c&lAny parkour created!"));
        } else {
            for (Parkour parkour : Main.getInstance().getParkourHandler().getParkours().values()) {
                p.sendMessage(ColorManager.translate("    &7- &e&lID: &a&l" + parkour.getId() + " &7&l| &e&lName: &a&l" + parkour.getName()));
            }
        }

        p.sendMessage("");
    }

    public void sendCommandHelp(Player p) {
        p.sendMessage("");
        p.sendMessage(ColorManager.translate("&7 - &a/aparkour stats"));
        p.sendMessage(ColorManager.translate("&7 - &a/aparkour top"));
        p.sendMessage(ColorManager.translate("&7 - &a/aparkour list"));
        p.sendMessage("");
        if (Main.getInstance().getPlayerDataHandler().playerHasPermission(p)) {
            p.sendMessage(ColorManager.translate("&7 - &a/aparkour create [id] [name]"));
            p.sendMessage(ColorManager.translate("&7 - &a/aparkour remove [id]"));
            p.sendMessage(ColorManager.translate("&7 - &a/aparkour set [id] [name,spawn,start,end,stats,top]"));
            p.sendMessage(ColorManager.translate("&7 - &a/aparkour config [id]"));
            p.sendMessage(ColorManager.translate("&7 - &a/aparkour reload"));
            p.sendMessage("");
        }
    }

}
