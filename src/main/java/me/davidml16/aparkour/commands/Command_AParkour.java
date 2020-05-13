package me.davidml16.aparkour.commands;

import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.api.events.ParkourCheckpointEvent;
import me.davidml16.aparkour.api.events.ParkourReturnEvent;
import me.davidml16.aparkour.data.Parkour;
import me.davidml16.aparkour.data.ParkourSession;
import me.davidml16.aparkour.managers.ColorManager;
import me.davidml16.aparkour.utils.ActionBar;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class Command_AParkour implements CommandExecutor {

    private Main main;
    public Command_AParkour(Main main) {
        this.main = main;
    }

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
            if (main.getParkourHandler().getParkours().size() > 0) {
                main.getStatsGUI().open(p);
            } else {
                String message = main.getLanguageHandler().getMessage("Commands.NoStats");
                if(message.length() > 0)
                    p.sendMessage(message);
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("play")) {
            if(main.isJoinByGUI())
                main.getPlayParkourGUI().open(p);
            return true;
        }

        if (args[0].equalsIgnoreCase("list")) {
            sendParkourList(p);
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!main.getPlayerDataHandler().playerHasPermission(p, "aparkour.admin")) {
                String message = main.getLanguageHandler().getMessage("Commands.NoPerms");
                if(message.length() > 0)
                    p.sendMessage(message);
                return false;
            }

            main.reloadConfig();
            main.getPluginManager().reloadAll();

            String message = main.getLanguageHandler().getMessage("Commands.Reload");
            if(message.length() > 0)
                p.sendMessage(message);

            return true;
        }

        if (args[0].equalsIgnoreCase("create")) {
            if (!main.getPlayerDataHandler().playerHasPermission(p, "aparkour.admin")) {
                String message = main.getLanguageHandler().getMessage("Commands.NoPerms");
                if(message.length() > 0)
                    p.sendMessage(message);
                return false;
            }

            if (args.length == 1 || args.length == 2) {
                p.sendMessage(ColorManager.translate(
                        main.getLanguageHandler().getPrefix() + " &cUsage: /aparkour create [id] [name]"));
                return false;
            }

            String id = args[1].toLowerCase();
            if(!Character.isDigit(id.charAt(0))) {
                if (main.getParkourHandler().parkourExists(id)) {
                    p.sendMessage(ColorManager.translate(
                            main.getLanguageHandler().getPrefix() + " &cThis parkour already exists!"));
                    return true;
                }

                if(main.getParkourHandler().createParkour(id)) {
                    FileConfiguration config = main.getParkourHandler().getConfig(id);
                    config.set("parkour.name", args[2]);
                    config.set("parkour.icon", "389:0");
                    config.set("parkour.plateHolograms.start.enabled", false);
                    config.set("parkour.plateHolograms.start.distanceBelowPlate", 2.5D);
                    config.set("parkour.plateHolograms.end.enabled", false);
                    config.set("parkour.plateHolograms.end.distanceBelowPlate", 2.5D);
                    config.set("parkour.plateHolograms.checkpoints.enabled", false);
                    config.set("parkour.plateHolograms.checkpoints.distanceBelowPlate", 2.5D);
                    config.set("parkour.titles.start.enabled", false);
                    config.set("parkour.titles.end.enabled", false);
                    config.set("parkour.titles.checkpoint.enabled", false);
                    config.set("parkour.permissionRequired.enabled", false);
                    config.set("parkour.permissionRequired.permission", "aparkour.permission." + id);
                    config.set("parkour.permissionRequired.message", "&cYou dont have permission to start this parkour!");
                    config.set("parkour.rewards.example.firstTime", true);
                    config.set("parkour.rewards.example.permission", "*");
                    config.set("parkour.rewards.example.command", "give %player% diamond 1");
                    config.set("parkour.rewards.example.chance", 100);
                    config.set("parkour.walkableBlocks", new ArrayList<>());
                    config.set("parkour.checkpoints", new ArrayList<>());
                    main.getParkourHandler().saveConfig(id);
                    main.getConfigGUI().loadGUI(id);
                    main.getWalkableBlocksGUI().loadGUI(id);
                    main.getRewardsGUI().loadGUI(id);
                    p.sendMessage(ColorManager.translate(main.getLanguageHandler().getPrefix()
                            + " &aSuccesfully created parkour &e" + id + " &awith the name &e" + args[2]));
                }
                return true;
            } else {
                p.sendMessage(ColorManager.translate(
                        main.getLanguageHandler().getPrefix() + " &cThe parkour id cannot start with a number, use for example 'p1'."));
                return false;
            }
        }

        if (args[0].equalsIgnoreCase("reset")) {
            if (!main.getPlayerDataHandler().playerHasPermission(p, "aparkour.admin")) {
                String message = main.getLanguageHandler().getMessage("Commands.NoPerms");
                if(message.length() > 0)
                    p.sendMessage(message);
                return false;
            }

            if (args.length == 1 || args.length == 2) {
                p.sendMessage(ColorManager.translate(
                        main.getLanguageHandler().getPrefix() + " &cUsage: /aparkour reset [player] [parkourID]"));
                return false;
            }

            String player = args[1];
            Player target = Bukkit.getPlayer(player);

            if(target == null || !target.isOnline()) {
                p.sendMessage(ColorManager.translate(
                        main.getLanguageHandler().getPrefix() + " &cThis player is not online!"));
                return false;
            }

            String id = args[2].toLowerCase();
            if (!main.getParkourHandler().getParkourConfigs().containsKey(id)) {
                p.sendMessage(ColorManager.translate(
                        main.getLanguageHandler().getPrefix() + " &cThis parkour doesn't exists!"));
                return true;
            }

            main.getPlayerDataHandler().getData(target).setLastTime(0L, id);
            main.getPlayerDataHandler().getData(target).setBestTime(0L, id);
            try {
                main.getDatabaseHandler().setTimes(target.getUniqueId(), 0L, 0L, id);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            main.getStatsHologramManager().reloadStatsHologram(target, id);

            p.sendMessage(ColorManager.translate(
                    main.getLanguageHandler().getPrefix() + " &aSuccesfully reseted times of parkour &e" + id + " &ato player &e" + target.getName()));

            return true;
        }

        if (args[0].equalsIgnoreCase("setup")) {
            if (!main.getPlayerDataHandler().playerHasPermission(p, "aparkour.admin")) {
                String message = main.getLanguageHandler().getMessage("Commands.NoPerms");
                if(message.length() > 0)
                    p.sendMessage(message);
                return false;
            }

            if (args.length == 1) {
                p.sendMessage(ColorManager.translate(
                        main.getLanguageHandler().getPrefix() + " &cUsage: /aparkour config [id]"));
                return true;
            }

            String id = args[1].toLowerCase();
            if (!main.getParkourHandler().getParkourConfigs().containsKey(id)) {
                p.sendMessage(ColorManager.translate(
                        main.getLanguageHandler().getPrefix() + " &cThis parkour doesn't exists!"));
                return true;
            }

            main.getConfigGUI().open(p, id);
            return true;
        }

        if (args[0].equalsIgnoreCase("cancel")) {
            if (main.getTimerManager().hasPlayerTimer(p)) {
                ParkourSession session = main.getSessionHandler().getSession(p);
                p.teleport(session.getParkour().getSpawn());

                String message = main.getLanguageHandler().getMessage("Messages.Return");
                if(message.length() > 0)
                    p.sendMessage(message);

                main.getParkourHandler().resetPlayer(p);

                main.getSoundUtil().playReturn(p);

                Bukkit.getPluginManager().callEvent(new ParkourReturnEvent(p, session.getParkour()));
                return true;
            } else {
                String message = main.getLanguageHandler().getMessage("Messages.NotInParkour");
                if(message.length() > 0)
                    p.sendMessage(message);
                return false;
            }
        }

        if (args[0].equalsIgnoreCase("checkpoint")) {
            if (main.getTimerManager().hasPlayerTimer(p)) {
                ParkourSession session = main.getSessionHandler().getSession(p);

                if (session.getLastCheckpoint() < 0) {
                    String message = main.getLanguageHandler().getMessage("Messages.Return");
                    if(message.length() > 0)
                        p.sendMessage(message);

                    if(main.isKickParkourOnFail()) {
                        main.getParkourHandler().resetPlayer(p);
                        p.teleport(session.getParkour().getSpawn());
                    } else {
                        Location loc = session.getParkour().getStart().getLocation().clone();
                        loc.add(0.5, 0, 0.5);
                        loc.setPitch(p.getLocation().getPitch());
                        loc.setYaw(p.getLocation().getYaw());
                        p.teleport(loc);
                    }

                    Bukkit.getPluginManager().callEvent(new ParkourReturnEvent(p, session.getParkour()));
                } else if (session.getLastCheckpoint() >= 0) {
                    p.teleport(session.getLastCheckpointLocation());
                    String message = main.getLanguageHandler().getMessage("Messages.ReturnCheckpoint");
                    if(message.length() > 0)
                        p.sendMessage(message.replaceAll("%checkpoint%", Integer.toString(session.getLastCheckpoint() + 1)));
                    Bukkit.getPluginManager().callEvent(new ParkourCheckpointEvent(p, session.getParkour()));
                }

                main.getSoundUtil().playReturn(p);

                p.setNoDamageTicks(40);
                return true;
            } else {
                String message = main.getLanguageHandler().getMessage("Messages.NotInParkour");
                if(message.length() > 0)
                    p.sendMessage(message);
                return false;
            }
        }

        if (args[0].equalsIgnoreCase("remove")) {
            if (!main.getPlayerDataHandler().playerHasPermission(p, "aparkour.admin")) {
                String message = main.getLanguageHandler().getMessage("Commands.NoPerms");
                if(message.length() > 0)
                    p.sendMessage(message);
                return false;
            }

            if (args.length == 1) {
                p.sendMessage(ColorManager.translate(
                        main.getLanguageHandler().getPrefix() + " &cUsage: /aparkour remove [id]"));
                return true;
            }
            String id = args[1].toLowerCase();
            if (!main.getParkourHandler().getParkourConfigs().containsKey(id)) {
                p.sendMessage(ColorManager.translate(
                        main.getLanguageHandler().getPrefix() + " &cThis parkour doesn't exists!"));
                return true;
            }

            for (Player pl : Bukkit.getOnlinePlayers()) {
                main.getStatsHologramManager().removeStatsHologram(pl, id);
                if(main.getTimerManager().hasPlayerTimer(pl)) {
                    ParkourSession session = main.getSessionHandler().getSession(pl);
                    if(session.getParkour().getId().equals(id)) {
                        pl.teleport(session.getParkour().getSpawn());

                        if (main.getTimerManager().isActionBarEnabled()) {
                            ActionBar.sendActionbar(pl, " ");
                        }

                        main.getParkourHandler().resetPlayer(pl);

                        main.getSoundUtil().playFall(pl);

                        Bukkit.getPluginManager().callEvent(new ParkourReturnEvent(p, session.getParkour()));
                    }
                }
            }

            main.getTopHologramManager().removeHologram(id);
            main.getParkourHandler().removeHologram(id);

            if(main.getParkourHandler().removeParkour(id)) {
                main.getParkourHandler().getParkours().remove(id);

                if (main.getConfigGUI().getGuis().containsKey(id)) {
                    for (UUID uuid : main.getConfigGUI().getOpened().keySet()) {
                        if (main.getConfigGUI().getOpened().get(uuid).equals(id)) {
                            Bukkit.getPlayer(uuid).closeInventory();
                        }
                    }
                    main.getConfigGUI().getGuis().remove(id);
                }

                if (main.getWalkableBlocksGUI().getGuis().containsKey(id)) {
                    for (UUID uuid : main.getWalkableBlocksGUI().getOpened().keySet()) {
                        if (main.getWalkableBlocksGUI().getOpened().get(uuid).getParkour().equals(id)) {
                            Bukkit.getPlayer(uuid).closeInventory();
                        }
                    }
                    main.getWalkableBlocksGUI().getGuis().remove(id);
                }

                if (main.getRewardsGUI().getGuis().containsKey(id)) {
                    for (UUID uuid : main.getRewardsGUI().getOpened().keySet()) {
                        if (main.getRewardsGUI().getOpened().get(uuid).getParkour().equals(id)) {
                            Bukkit.getPlayer(uuid).closeInventory();
                        }
                    }
                    main.getRewardsGUI().getGuis().remove(id);
                }

                if (main.getCheckpointsGUI().getGuis().containsKey(id)) {
                    for (UUID uuid : main.getCheckpointsGUI().getOpened().keySet()) {
                        if (main.getCheckpointsGUI().getOpened().get(uuid).getParkour().equals(id)) {
                            Bukkit.getPlayer(uuid).closeInventory();
                        }
                    }
                    main.getRewardsGUI().getGuis().remove(id);
                }

                p.sendMessage(ColorManager.translate(
                        main.getLanguageHandler().getPrefix() + " &aSuccesfully deleted parkour &e" + id));
            }
            return true;
        }

        p.sendMessage(ColorManager.translate(
                main.getLanguageHandler().getPrefix() + " &cInvalid argument, use /aparkour to see available commands"));
        return false;
    }

    private void sendParkourList(Player p) {
        p.sendMessage("");
        p.sendMessage(ColorManager.translate("  &a&lParkours availables:"));
        p.sendMessage("");

        if (main.getParkourHandler().getParkours().size() == 0) {
            p.sendMessage(ColorManager.translate("    &7- &c&lAny parkour created!"));
        } else {
            for (Parkour parkour : main.getParkourHandler().getParkours().values()) {
                p.sendMessage(ColorManager.translate("    &7- &e&lID: &a&l" + parkour.getId() + " &7&l| &e&lName: &a&l" + parkour.getName()));
            }
        }

        p.sendMessage("");
    }

    private void sendCommandHelp(Player p) {
        p.sendMessage("");
        p.sendMessage(ColorManager.translate("&7 - &a/aparkour [stats, list]"));
        p.sendMessage("");
        if(main.isJoinByGUI())
            p.sendMessage(ColorManager.translate("&7 - &a/aparkour play"));
        p.sendMessage(ColorManager.translate("&7 - &a/aparkour cancel"));
        p.sendMessage(ColorManager.translate("&7 - &a/aparkour checkpoint"));
        p.sendMessage("");
        if (main.getPlayerDataHandler().playerHasPermission(p, "aparkour.admin")) {
            p.sendMessage(ColorManager.translate("&7 - &a/aparkour create [id] [name]"));
            p.sendMessage(ColorManager.translate("&7 - &a/aparkour reset [player] [id]"));
            p.sendMessage(ColorManager.translate("&7 - &a/aparkour remove [id]"));
            p.sendMessage(ColorManager.translate("&7 - &a/aparkour setup [id]"));
            p.sendMessage(ColorManager.translate("&7 - &a/aparkour reload"));
            p.sendMessage("");
        }
    }

}
