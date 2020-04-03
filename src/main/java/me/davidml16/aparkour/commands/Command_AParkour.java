package me.davidml16.aparkour.commands;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import me.davidml16.aparkour.conversation.CheckpointMenu;
import me.davidml16.aparkour.conversation.RewardMenu;
import me.davidml16.aparkour.data.Parkour;
import me.davidml16.aparkour.data.Plate;
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
import org.bukkit.configuration.file.FileConfiguration;
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
        if (!(sender instanceof Player)) {
            sender.sendMessage(ColorManager.translate("&cThe commands only can be use by players!"));
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
                p.sendMessage(Main.getInstance().getLanguageHandler().getMessage("Commands.NoStats"));
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("list")) {
            sendParkourList(p);
            return true;
        }

        if (args[0].equalsIgnoreCase("top")) {
            if (Main.getInstance().getParkourHandler().getParkours().size() > 0) {
                Main.getInstance().getRankingsGUI().open(p);
            } else {
                p.sendMessage(Main.getInstance().getLanguageHandler().getMessage("Commands.NoParkours"));
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!Main.getInstance().getPlayerDataHandler().playerHasPermission(p, "aparkour.admin")) {
                p.sendMessage(Main.getInstance().getLanguageHandler().getMessage("Commands.NoPerms"));
                return false;
            }

            Main.getInstance().reloadConfig();
            PluginManager.reloadAll();
            p.sendMessage(Main.getInstance().getLanguageHandler().getMessage("Commands.Reload"));


            return true;
        }

        if (args[0].equalsIgnoreCase("create")) {
            if (!Main.getInstance().getPlayerDataHandler().playerHasPermission(p, "aparkour.admin")) {
                p.sendMessage(Main.getInstance().getLanguageHandler().getMessage("Commands.NoPerms"));
                return false;
            }

            if (args.length == 1 || args.length == 2) {
                p.sendMessage(ColorManager.translate(
                        Main.getInstance().getLanguageHandler().getPrefix() + " &cUsage: /aparkour create [id] [name]"));
                return false;
            }

            String id = args[1];
            if(!Character.isDigit(id.charAt(0))) {
                if (Main.getInstance().getParkourHandler().parkourExists(id)) {
                    p.sendMessage(ColorManager.translate(
                            Main.getInstance().getLanguageHandler().getPrefix() + " &cThis parkour already exists!"));
                    return true;
                }

                if(Main.getInstance().getParkourHandler().createParkour(id)) {
                    FileConfiguration config = Main.getInstance().getParkourHandler().getConfig(id);
                    config.set("parkour.name", args[2]);
                    config.set("parkour.plateHolograms.start.enabled", false);
                    config.set("parkour.plateHolograms.start.distanceBelowPlate", 2.5D);
                    config.set("parkour.plateHolograms.end.enabled", false);
                    config.set("parkour.plateHolograms.end.distanceBelowPlate", 2.5D);
                    config.set("parkour.plateHolograms.checkpoints.enabled", false);
                    config.set("parkour.plateHolograms.checkpoints.distanceBelowPlate", 2.5D);
                    config.set("parkour.permissionRequired.enabled", false);
                    config.set("parkour.permissionRequired.permission", "aparkour.permission." + id);
                    config.set("parkour.permissionRequired.message", "&cYou dont have permission to start this parkour!");
                    config.set("parkour.rewards.example.firstTime", true);
                    config.set("parkour.rewards.example.permission", "*");
                    config.set("parkour.rewards.example.command", "give %player% diamond 1");
                    config.set("parkour.walkableBlocks", new ArrayList<>());
                    config.set("parkour.checkpoints", new ArrayList<>());
                    Main.getInstance().getParkourHandler().saveConfig(id);
                    Main.getInstance().getConfigGUI().loadGUI(id);
                    Main.getInstance().getWalkableBlocksGUI().loadGUI(id);
                    Main.getInstance().getRewardsGUI().loadGUI(id);
                    p.sendMessage(ColorManager.translate(Main.getInstance().getLanguageHandler().getPrefix()
                            + " &aSuccesfully created parkour &e" + id + " &awith the name &e" + args[2]));
                }
                return true;
            } else {
                p.sendMessage(ColorManager.translate(
                        Main.getInstance().getLanguageHandler().getPrefix() + " &cThe parkour id cannot start with a number, use for example 'p1'."));
                return false;
            }
        }

        if (args[0].equalsIgnoreCase("config")) {
            if (!Main.getInstance().getPlayerDataHandler().playerHasPermission(p, "aparkour.admin")) {
                p.sendMessage(Main.getInstance().getLanguageHandler().getMessage("Commands.NoPerms"));
                return false;
            }

            if (args.length == 1) {
                p.sendMessage(ColorManager.translate(
                        Main.getInstance().getLanguageHandler().getPrefix() + " &cUsage: /aparkour config [id]"));
                return true;
            }

            String id = args[1];
            if (!Main.getInstance().getParkourHandler().getParkourConfigs().containsKey(id)) {
                p.sendMessage(ColorManager.translate(
                        Main.getInstance().getLanguageHandler().getPrefix() + " &cThis parkour doesn't exists!"));
                return true;
            }

            Main.getInstance().getConfigGUI().open(p, id);
            return true;
        }

        if (args[0].equalsIgnoreCase("remove")) {
            if (!Main.getInstance().getPlayerDataHandler().playerHasPermission(p, "aparkour.admin")) {
                p.sendMessage(Main.getInstance().getLanguageHandler().getMessage("Commands.NoPerms"));
                return false;
            }

            if (args.length == 1) {
                p.sendMessage(ColorManager.translate(
                        Main.getInstance().getLanguageHandler().getPrefix() + " &cUsage: /aparkour remove [id]"));
                return true;
            }
            String id = args[1];
            if (!Main.getInstance().getParkourHandler().getParkourConfigs().containsKey(id)) {
                p.sendMessage(ColorManager.translate(
                        Main.getInstance().getLanguageHandler().getPrefix() + " &cThis parkour doesn't exists!"));
                return true;
            }

            for (Player pl : Bukkit.getOnlinePlayers()) {
                Main.getInstance().getStatsHologramManager().removeStatsHologram(pl, id);
                if(Main.getInstance().getTimerManager().hasPlayerTimer(pl)) {
                    Parkour parkour = Main.getInstance().getPlayerDataHandler().getData(pl).getParkour();
                    if(parkour.getId().equals(id)) {
                        Main.getInstance().getTimerManager().cancelTimer(pl);
                        Main.getInstance().getPlayerDataHandler().getData(pl).setParkour(null);

                        pl.setFlying(false);
                        pl.teleport(parkour.getSpawn());
                        if (Main.getInstance().getConfig().getBoolean("RestartItem.Enabled")) {
                            Main.getInstance().getPlayerDataHandler().restorePlayerInventory(pl);
                        }
                        if (Main.getInstance().getTimerManager().isActionBarEnabled()) {
                            ActionBar.sendActionbar(pl, " ");
                        }
                        SoundUtil.playFall(pl);

                        pl.setNoDamageTicks(40);
                    }
                }
            }

            Main.getInstance().getTopHologramManager().removeHologram(id);
            Main.getInstance().getParkourHandler().removeHologram(id);

            if(Main.getInstance().getParkourHandler().removeParkour(id)) {
                Main.getInstance().getParkourHandler().getParkours().remove(id);

                if (Main.getInstance().getConfigGUI().getGuis().containsKey(id)) {
                    for (UUID uuid : Main.getInstance().getConfigGUI().getOpened().keySet()) {
                        if (Main.getInstance().getConfigGUI().getOpened().get(uuid).equals(id)) {
                            Bukkit.getPlayer(uuid).closeInventory();
                        }
                    }
                    Main.getInstance().getConfigGUI().getGuis().remove(id);
                }

                if (Main.getInstance().getWalkableBlocksGUI().getGuis().containsKey(id)) {
                    for (UUID uuid : Main.getInstance().getWalkableBlocksGUI().getOpened().keySet()) {
                        if (Main.getInstance().getWalkableBlocksGUI().getOpened().get(uuid).equals(id)) {
                            Bukkit.getPlayer(uuid).closeInventory();
                        }
                    }
                    Main.getInstance().getWalkableBlocksGUI().getGuis().remove(id);
                }

                if (Main.getInstance().getRewardsGUI().getGuis().containsKey(id)) {
                    for (UUID uuid : Main.getInstance().getRewardsGUI().getOpened().keySet()) {
                        if (Main.getInstance().getRewardsGUI().getOpened().get(uuid).equals(id)) {
                            Bukkit.getPlayer(uuid).closeInventory();
                        }
                    }
                    Main.getInstance().getRewardsGUI().getGuis().remove(id);
                }

                if (Main.getInstance().getCheckpointsGUI().getGuis().containsKey(id)) {
                    for (UUID uuid : Main.getInstance().getCheckpointsGUI().getOpened().keySet()) {
                        if (Main.getInstance().getCheckpointsGUI().getOpened().get(uuid).equals(id)) {
                            Bukkit.getPlayer(uuid).closeInventory();
                        }
                    }
                    Main.getInstance().getRewardsGUI().getGuis().remove(id);
                }

                Main.getInstance().getRankingsGUI().reloadGUI();

                p.sendMessage(ColorManager.translate(
                        Main.getInstance().getLanguageHandler().getPrefix() + " &aSuccesfully deleted parkour &e" + id));
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("set")) {
            if (!Main.getInstance().getPlayerDataHandler().playerHasPermission(p, "aparkour.admin")) {
                p.sendMessage(Main.getInstance().getLanguageHandler().getMessage("Commands.NoPerms"));
                return false;
            }

            if (args.length == 1 || args.length == 2) {
                p.sendMessage(ColorManager.translate(Main.getInstance().getLanguageHandler().getPrefix()
                        + " &cUsage: /aparkour set [id] [name,spawn,start,end,stats,top]"));
                return true;
            }

            String id = args[1];
            if (!Main.getInstance().getParkourHandler().getParkourConfigs().containsKey(id)) {
                p.sendMessage(ColorManager.translate(
                        Main.getInstance().getLanguageHandler().getPrefix() + " &cThis parkour doesn't exists!"));
                return true;
            }

            if (args[2].equalsIgnoreCase("name")) {
                if (args.length == 3) {
                    p.sendMessage(ColorManager.translate(Main.getInstance().getLanguageHandler().getPrefix()
                            + " &cUsage: /aparkour set [id] name newName"));
                    return true;
                }

                String name = args[3];
                Main.getInstance().getParkourHandler().getConfig(id).set("parkours." + id + ".name", name);
                Main.getInstance().getParkourHandler().saveConfig(id);
                p.sendMessage(ColorManager.translate(Main.getInstance().getLanguageHandler().getPrefix()
                        + " &aSuccesfully renamed parkour &e" + id + " &awith the new name &e" + name));
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
                    + " &cUsage: /aparkour set [id] [name,spawn,start,end,stats,top]"));
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
        if (Main.getInstance().getPlayerDataHandler().playerHasPermission(p, "aparkour.admin")) {
            p.sendMessage(ColorManager.translate("&7 - &a/aparkour create [id] [name]"));
            p.sendMessage(ColorManager.translate("&7 - &a/aparkour remove [id]"));
            p.sendMessage(ColorManager.translate("&7 - &a/aparkour set [id] [name,spawn,start,end,stats,top]"));
            p.sendMessage(ColorManager.translate("&7 - &a/aparkour config [id]"));
            p.sendMessage(ColorManager.translate("&7 - &a/aparkour reload"));
            p.sendMessage("");
        }
    }

}
