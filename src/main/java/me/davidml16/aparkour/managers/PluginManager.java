package me.davidml16.aparkour.managers;

import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.data.Parkour;
import me.davidml16.aparkour.data.ParkourSession;
import me.davidml16.aparkour.enums.CommandBlockType;
import me.davidml16.aparkour.utils.ActionBar;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PluginManager {

    private Main main;
    public PluginManager(Main main) {
        this.main = main;
    }

    public void removePlayersFromParkour() {
        for (Player pl : Bukkit.getOnlinePlayers()) {
            if(main.getTimerManager().hasPlayerTimer(pl)) {
                ParkourSession session = main.getSessionHandler().getSession(pl);
                pl.teleport(session.getParkour().getSpawn());
                main.getParkourHandler().resetPlayer(pl);
                main.getSoundUtil().playFall(pl);
            }
        }
    }

    public void reloadAll() {
        main.setParkourItemsEnabled(main.getConfig().getBoolean("Items.Enabled"));
        if (main.isParkourItemsEnabled()) {
            main.getParkourItems().loadReturnItem();
            main.getParkourItems().loadCheckpointItem();
        }

        removePlayersFromParkour();

        main.getCommandBlocker().setCommands(main.getConfig().getStringList("CommandBlocker.Commands"));
        main.getCommandBlocker().setType(CommandBlockType.valueOf(main.getConfig().getString("CommandBlocker.Type").toUpperCase()));
        main.setHologramsEnabled(main.getConfig().getBoolean("Hologram.Enabled"));
        main.getHologramTask().stop();
        main.getLanguageHandler().setLanguage(main.getConfig().getString("Language").toLowerCase());
        main.getLanguageHandler().pushMessages();
        main.getStatsHologramManager().reloadStatsHolograms();
        main.getParkourHandler().loadParkours();
        main.getParkourHandler().setParkourGamemode(GameMode.valueOf(main.getConfig().getString("ParkourGamemode")));
        main.getDatabaseHandler().loadTables();
        main.getPlayerDataHandler().loadAllPlayerData();
        main.getRewardHandler().loadRewards();
        main.getTopHologramManager().setReloadInterval(main.getConfig().getInt("Tasks.ReloadInterval"));
        main.getTopHologramManager().restartTimeLeft();
        main.getConfigGUI().loadGUI();
        main.getConfigGUI().reloadAllGUI();
        main.getWalkableBlocksGUI().loadGUI();
        main.getWalkableBlocksGUI().reloadAllGUI();
        main.getRewardsGUI().loadGUI();
        main.getRewardsGUI().reloadAllGUI();
        main.getCheckpointsGUI().loadGUI();
        main.getCheckpointsGUI().reloadAllGUI();
        main.getHologramsGUI().loadGUI();
        main.getHologramsGUI().reloadAllGUI();
        main.getTitlesGUI().loadGUI();
        main.getTitlesGUI().reloadAllGUI();
        main.getMiscellaneousGUI().loadGUI();
        main.getMiscellaneousGUI().reloadAllGUI();
        main.getParkourHandler().loadHolograms();
        main.getTopHologramManager().loadTopHolograms();
        main.getHologramTask().start();
    }

}
