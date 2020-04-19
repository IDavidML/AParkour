package me.davidml16.aparkour.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.api.ParkourAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class PlaceholderHook extends PlaceholderExpansion {

    private Main main;
    private ParkourAPI api;
    public PlaceholderHook(Main main) {
        this.main = main;
        this.api = main.getParkourAPI();
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
                if (api.getParkoursIDs().contains(identifiers[1])) {
                    long time = api.getLastTime(player.getPlayer(), identifiers[1]);
                    if (time == 0) return "N/A";
                    return "" + time;
                }
                break;
            case "ltf":
                if (api.getParkoursIDs().contains(identifiers[1])) {
                    long time = api.getLastTime(player.getPlayer(), identifiers[1]);
                    if (time == 0) return "N/A";
                    return "" + api.getLastTimeFormatted(player.getPlayer(), identifiers[1]);
                }
                break;
            case "bt":
                if (api.getParkoursIDs().contains(identifiers[1])) {
                    long time = api.getBestTime(player.getPlayer(), identifiers[1]);
                    if (time == 0) return "N/A";
                    return "" + time;
                }
                break;
            case "btf":
                if (api.getParkoursIDs().contains(identifiers[1])) {
                    long time = api.getBestTime(player.getPlayer(), identifiers[1]);
                    if (time == 0) return "N/A";
                    return "" + api.getBestTimeFormatted(player.getPlayer(), identifiers[1]);
                }
                break;
            case "top":
                switch (identifiers[1]) {
                    case "name":
                        if (api.getParkoursIDs().contains(identifiers[2])) {
                            if(Integer.parseInt(identifiers[3]) > 0 && Integer.parseInt(identifiers[3]) <= 10) {
                                if(Integer.parseInt(identifiers[3]) > api.getLeaderboard(identifiers[2]).size()) return "NONE";
                                return api.getLeaderboard(identifiers[2]).get(Integer.parseInt(identifiers[3]) - 1).getName();
                            }
                        }
                        break;
                    case "time":
                        if (api.getParkoursIDs().contains(identifiers[2])) {
                            if(Integer.parseInt(identifiers[3]) > 0 && Integer.parseInt(identifiers[3]) <= 10) {
                                if(Integer.parseInt(identifiers[3]) > api.getLeaderboard(identifiers[2]).size()) return "N/A";
                                long time = api.getLeaderboard(identifiers[2]).get(Integer.parseInt(identifiers[3]) - 1).getTime();
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