package me.davidml16.aparkour.managers;

import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.data.Parkour;
import me.davidml16.aparkour.utils.ActionBar;
import me.davidml16.aparkour.utils.RestartItemUtil;
import me.davidml16.aparkour.utils.SoundUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class PluginManager {

    public static void removePlayersFromParkour() {
        for (Player pl : Bukkit.getOnlinePlayers()) {
            if(Main.getInstance().getTimerManager().hasPlayerTimer(pl)) {
                Main.getInstance().getTimerManager().cancelTimer(pl);
                Parkour parkour = Main.getInstance().getPlayerDataHandler().getData(pl).getParkour();
                if (parkour != null) {
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
    }

    public static void reloadAll() {
        if (Main.getInstance().getConfig().getBoolean("RestartItem.Enabled")) {
            RestartItemUtil.loadReturnItem();
        }

        for(Parkour parkour : Main.getInstance().getParkourHandler().getParkours().values()) {
            parkour.saveParkour();
        }

        removePlayersFromParkour();

        Main.getInstance().setHologramsEnabled(Main.getInstance().getConfig().getBoolean("Hologram.Enabled"));
        Main.getInstance().getHologramTask().stop();
        Main.getInstance().getLanguageHandler().setLanguage(Main.getInstance().getConfig().getString("Language").toLowerCase());
        Main.getInstance().getLanguageHandler().pushMessages();
        Main.getInstance().getParkourHandler().loadParkours();
        Main.getInstance().getParkourHandler().setKickFromParkourOnFail(Main.getInstance().getConfig().getBoolean("KickFromParkourOnFail.Enabled"));
        Main.getInstance().getParkourHandler().setParkourGamemode(GameMode.valueOf(Main.getInstance().getConfig().getString("ParkourGamemode")));
        Main.getInstance().getDatabaseHandler().loadTables();
        Main.getInstance().getPlayerDataHandler().loadAllPlayerData();
        Main.getInstance().getPlayerDataHandler().saveAllPlayerData();
        Main.getInstance().getRewardHandler().loadRewards();
        Main.getInstance().getRewardHandler().saveConfig();
        Main.getInstance().getStatsHologramManager().reloadStatsHolograms();
        Main.getInstance().getTopHologramManager().setReloadInterval(Main.getInstance().getConfig().getInt("Tasks.ReloadInterval"));
        Main.getInstance().getTopHologramManager().restartTimeLeft();
        Main.getInstance().getTopHologramManager().loadTopHolograms();
        Main.getInstance().getHologramTask().start();
        Main.getInstance().getConfigGUI().reloadAllGUI();
        Main.getInstance().getWalkableBlocksGUI().reloadAllGUI();
    }

}
