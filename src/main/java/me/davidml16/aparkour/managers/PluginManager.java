package me.davidml16.aparkour.managers;

import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.data.Parkour;
import me.davidml16.aparkour.utils.RestartItemUtil;

public class PluginManager {

    public static void reloadAll() {
        if (Main.getInstance().getConfig().getBoolean("RestartItem.Enabled")) {
            RestartItemUtil.loadReturnItem();
        }

        for(Parkour parkour : Main.getInstance().getParkourHandler().getParkours().values()) {
            parkour.saveParkour();
        }

        Main.getInstance().setHologramsEnabled(Main.getInstance().getConfig().getBoolean("Hologram.Enabled"));
        Main.getInstance().getHologramTask().stop();
        Main.getInstance().getLanguageHandler().setLanguage(Main.getInstance().getConfig().getString("Language").toLowerCase());
        Main.getInstance().getLanguageHandler().pushMessages();
        Main.getInstance().getParkourHandler().loadParkours();
        Main.getInstance().getDatabaseHandler().loadTables();
        Main.getInstance().getPlayerDataHandler().loadAllPlayerData();
        Main.getInstance().getPlayerDataHandler().saveAllPlayerData();
        Main.getInstance().getRewardHandler().saveConfig();
        Main.getInstance().getRewardHandler().loadRewards();
        Main.getInstance().getStatsHologramManager().reloadStatsHolograms();
        Main.getInstance().getTopHologramManager().setReloadInterval(Main.getInstance().getConfig().getInt("Tasks.ReloadInterval"));
        Main.getInstance().getTopHologramManager().restartTimeLeft();
        Main.getInstance().getTopHologramManager().loadTopHolograms();
        Main.getInstance().getHologramTask().start();
        Main.getInstance().getConfigGUI().reloadAllGUI();
        Main.getInstance().getWalkableBlocksGUI().reloadAllGUI();
    }

}
