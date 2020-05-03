package me.davidml16.aparkour.api;

import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.data.LeaderboardEntry;
import me.davidml16.aparkour.data.Parkour;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ParkourAPI {

    private final Main main;
    public ParkourAPI() {
        this.main = Main.getInstance();
    }

    public boolean isInParkour(Player p) {
        return main.getTimerManager().hasPlayerTimer(p);
    }

    public void saveInventory(Player p) { main.getPlayerDataHandler().savePlayerInventory(p); }

    public void restoreInventory(Player p) { main.getPlayerDataHandler().restorePlayerInventory(p); }

    public int getCurrentTime(Player p) {
        if(main.getTimerManager().hasPlayerTimer(p)) {
            return (int) TimeUnit.MILLISECONDS.toSeconds(main.getSessionHandler().getSession(p).getLiveTime());
        } else {
            return 0;
        }
    }

    public String getCurrentTimeFormatted(Player p) {
        if(main.getTimerManager().hasPlayerTimer(p)) {
            return main.getTimerManager().millisToString(main.getLanguageHandler().getMessage("Timer.Formats.PlayerTime"), main.getSessionHandler().getSession(p).getLiveTime());
        } else {
            return main.getTimerManager().millisToString(main.getLanguageHandler().getMessage("Timer.Formats.PlayerTime"),0L);
        }
    }

    public long getLastTime(Player p, String parkour) {
        if(main.getPlayerDataHandler().playerExists(p) && main.getPlayerDataHandler().getData(p).getLastTimes().containsKey(parkour)) {
            return main.getPlayerDataHandler().getData(p).getLastTimes().get(parkour);
        }
        return 0;
    }

    public String getLastTimeFormatted(Player p, String parkour) {
        if(main.getPlayerDataHandler().playerExists(p) && main.getPlayerDataHandler().getData(p).getLastTimes().containsKey(parkour)) {
            return main.getTimerManager().millisToString(main.getLanguageHandler().getMessage("Timer.Formats.PlayerTime"),main.getPlayerDataHandler().getData(p).getLastTimes().get(parkour));
        }
        return main.getTimerManager().millisToString(main.getLanguageHandler().getMessage("Timer.Formats.PlayerTime"),0);
    }

    public long getBestTime(Player p, String parkour) {
        if(main.getPlayerDataHandler().playerExists(p) && main.getPlayerDataHandler().getData(p).getLastTimes().containsKey(parkour)) {
            return main.getPlayerDataHandler().getData(p).getBestTimes().get(parkour);
        }
        return 0;
    }

    public String getBestTimeFormatted(Player p, String parkour) {
        if(main.getPlayerDataHandler().playerExists(p) && main.getPlayerDataHandler().getData(p).getLastTimes().containsKey(parkour)) {
            return main.getTimerManager().millisToString(main.getLanguageHandler().getMessage("Timer.Formats.PlayerTime"),main.getPlayerDataHandler().getData(p).getBestTimes().get(parkour));
        }
        return main.getTimerManager().millisToString(main.getLanguageHandler().getMessage("Timer.Formats.PlayerTime"),0);
    }

    public List<String> getParkoursIDs() {
        return new ArrayList<>(main.getParkourHandler().getParkours().keySet());
    }

    public List<Parkour> getParkours() {
        return new ArrayList<>(main.getParkourHandler().getParkours().values());
    }

    public List<LeaderboardEntry> getLeaderboard(String parkour) {
        return main.getLeaderboardHandler().getLeaderboard(parkour);
    }

    public Parkour getParkourByLocation(Location loc) {
        for (Parkour parkour : main.getParkourHandler().getParkours().values()) {
            if (loc.equals(parkour.getStart().getLocation()) ||
                    loc.equals(parkour.getEnd().getLocation()) ||
                    parkour.getCheckpointLocations().contains(loc))
                return parkour;
        }
        return null;
    }

    public Parkour getParkourByPlayer(Player p) {
        return main.getSessionHandler().getSession(p).getParkour();
    }

}
