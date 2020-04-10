package me.davidml16.aparkour.PlaceholderAPI;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.api.ParkourAPI;
import me.davidml16.aparkour.data.Parkour;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class PlaceholderHook extends PlaceholderExpansion {

    private Main main;
    private ParkourAPI api;
    public PlaceholderHook(Main main) {
        this.main = main;
        this.api = new ParkourAPI(main);
    }

    @Override
    public boolean canRegister() {
        return Bukkit.getPluginManager().getPlugin("AParkour") != null;
    }

    @Override
    public boolean register() {
        if (!canRegister()) {
            return false;
        }

        return super.register();
    }

    @Override
    public String getIdentifier() {
        return "aparkour";
    }

    @Override
    public String getAuthor() {
        return "DavidML16";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, String identifier) {
        if (player == null) return "";

        if (identifier.equals("ct")) {
            return "" + api.getCurrentTime(player.getPlayer());
        }

        if (identifier.equals("ctf")) {
            return "" + api.getCurrentTimeFormatted(player.getPlayer());
        }

        for (Parkour parkour : api.getParkours().values()) {
            if (identifier.equals("lt_" + parkour.getId())) {
                int time = api.getLastTime(player.getPlayer(), parkour.getId());
                if (time == 0) return "N/A";
                return "" + time;
            } else if (identifier.equals("ltf_" + parkour.getId())) {
                int time = api.getLastTime(player.getPlayer(), parkour.getId());
                if (time == 0) return "N/A";
                return "" + api.getLastTimeFormatted(player.getPlayer(), parkour.getId());
            } else if (identifier.equals("bt_" + parkour.getId())) {
                int time = api.getBestTime(player.getPlayer(), parkour.getId());
                if (time == 0) return "N/A";
                return "" + time;
            } else if (identifier.equals("btf_" + parkour.getId())) {
                int time = api.getBestTime(player.getPlayer(), parkour.getId());
                if (time == 0) return "N/A";
                return "" + api.getBestTimeFormatted(player.getPlayer(), parkour.getId());
            }
        }

        return null;
    }
}