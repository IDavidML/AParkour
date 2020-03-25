package me.davidml16.aparkour.api;

import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.data.Parkour;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.HashMap;

public class ParkourAPI {

    public int getCurrentTime(Player p) {
        if(Main.getInstance().getTimerManager().hasPlayerTimer(p)) {
            return Main.getInstance().getTimerManager().getTimer().get(p.getUniqueId());
        } else {
            return 0;
        }
    }

    public String getCurrentTimeFormatted(Player p) {
        if(Main.getInstance().getTimerManager().hasPlayerTimer(p)) {
            return Main.getInstance().getTimerManager().timeAsString(Main.getInstance().getTimerManager().getTimer().get(p.getUniqueId()));
        } else {
            return Main.getInstance().getTimerManager().timeAsString(0);
        }
    }

    public int getLastTime(Player p, String parkour) {
        if(Main.getInstance().getPlayerDataHandler().playerExists(p) && Main.getInstance().getPlayerDataHandler().getData(p).getLastTimes().containsKey(parkour)) {
            return Main.getInstance().getPlayerDataHandler().getData(p).getLastTimes().get(parkour);
        }
        return 0;
    }

    public String getLastTimeFormatted(Player p, String parkour) {
        if(Main.getInstance().getPlayerDataHandler().playerExists(p) && Main.getInstance().getPlayerDataHandler().getData(p).getLastTimes().containsKey(parkour)) {
            return Main.getInstance().getTimerManager().timeAsString(Main.getInstance().getPlayerDataHandler().getData(p).getLastTimes().get(parkour));
        }
        return Main.getInstance().getTimerManager().timeAsString(0);
    }

    public int getBestTime(Player p, String parkour) {
        if(Main.getInstance().getPlayerDataHandler().playerExists(p) && Main.getInstance().getPlayerDataHandler().getData(p).getLastTimes().containsKey(parkour)) {
            return Main.getInstance().getPlayerDataHandler().getData(p).getBestTimes().get(parkour);
        }
        return 0;
    }

    public String getBestTimeFormatted(Player p, String parkour) {
        if(Main.getInstance().getPlayerDataHandler().playerExists(p) && Main.getInstance().getPlayerDataHandler().getData(p).getLastTimes().containsKey(parkour)) {
            return Main.getInstance().getTimerManager().timeAsString(Main.getInstance().getPlayerDataHandler().getData(p).getBestTimes().get(parkour));
        }
        return Main.getInstance().getTimerManager().timeAsString(0);
    }

    public HashMap<String, Parkour> getParkours() {
        return Main.getInstance().getParkourHandler().getParkours();
    }

    public Parkour getParkourByLocation(Location loc) {
        for (Parkour parkour : Main.getInstance().getParkourHandler().getParkours().values()) {
            if (loc.equals(parkour.getStart()) || loc.equals(parkour.getEnd()))
                return Main.getInstance().getParkourHandler().getParkours().get(parkour.getId());
        }
        return null;
    }

    public Parkour getParkourByPlayer(Player p) {
        return Main.getInstance().getPlayerDataHandler().getData(p).getParkour();
    }

}
