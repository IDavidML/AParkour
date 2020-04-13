package me.davidml16.aparkour.PlaceholderAPI;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.api.ParkourAPI;
import me.davidml16.aparkour.data.Parkour;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Arrays;

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

        String[] identifiers = identifier.split("_");
        switch (identifiers[0]) {
            case "lt":
                for (String parkour : api.getParkours().keySet()) {
                    if (identifiers[1].equals(parkour)) {
                        long time = api.getLastTime(player.getPlayer(), parkour);
                        if (time == 0) return "N/A";
                        return "" + time;
                    }
                }
                break;
            case "ltf":
                for (String parkour : api.getParkours().keySet()) {
                    if (identifiers[1].equals(parkour)) {
                        long time = api.getLastTime(player.getPlayer(), parkour);
                        if (time == 0) return "N/A";
                        return "" + api.getLastTimeFormatted(player.getPlayer(), parkour);
                    }
                }
                break;
            case "bt":
                for (String parkour : api.getParkours().keySet()) {
                    if (identifiers[1].equals(parkour)) {
                        long time = api.getBestTime(player.getPlayer(), parkour);
                        if (time == 0) return "N/A";
                        return "" + time;
                    }
                }
                break;
            case "btf":
                for (String parkour : api.getParkours().keySet()) {
                    if (identifiers[1].equals(parkour)) {
                        long time = api.getBestTime(player.getPlayer(), parkour);
                        if (time == 0) return "N/A";
                        return "" + api.getBestTimeFormatted(player.getPlayer(), parkour);
                    }
                }
                break;
            case "top":
                switch (identifiers[1]) {
                    case "name":
                        for (String parkour : api.getParkours().keySet()) {
                            if (identifiers[2].equals(parkour)) {
                                return api.getLeaderboard(parkour).get(Integer.parseInt(identifiers[3]) - 1).getName();
                            }
                        }
                        break;
                    case "time":
                        for (String parkour : api.getParkours().keySet()) {
                            if (identifiers[2].equals(parkour)) {
                                long time = api.getLeaderboard(parkour).get(Integer.parseInt(identifiers[3]) - 1).getTime();
                                if (time == 0) return "N/A";
                                return "" + main.getTimerManager().millisToString(time);
                            }
                        }
                        break;
                }
                break;
        }
        return null;
    }
}