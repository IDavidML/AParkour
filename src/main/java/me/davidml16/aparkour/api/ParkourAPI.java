package me.davidml16.aparkour.api;

import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.data.Parkour;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.HashMap;

public class ParkourAPI {

    private Main main;
    public ParkourAPI(Main main) {
        this.main = main;
    }

    public int getCurrentTime(Player p) {
        if(main.getTimerManager().hasPlayerTimer(p)) {
            return main.getTimerManager().getTimer().get(p.getUniqueId());
        } else {
            return 0;
        }
    }

    public String getCurrentTimeFormatted(Player p) {
        if(main.getTimerManager().hasPlayerTimer(p)) {
            return main.getTimerManager().timeAsString(main.getTimerManager().getTimer().get(p.getUniqueId()));
        } else {
            return main.getTimerManager().timeAsString(0);
        }
    }

    public int getLastTime(Player p, String parkour) {
        if(main.getPlayerDataHandler().playerExists(p) && main.getPlayerDataHandler().getData(p).getLastTimes().containsKey(parkour)) {
            return main.getPlayerDataHandler().getData(p).getLastTimes().get(parkour);
        }
        return 0;
    }

    public String getLastTimeFormatted(Player p, String parkour) {
        if(main.getPlayerDataHandler().playerExists(p) && main.getPlayerDataHandler().getData(p).getLastTimes().containsKey(parkour)) {
            return main.getTimerManager().timeAsString(main.getPlayerDataHandler().getData(p).getLastTimes().get(parkour));
        }
        return main.getTimerManager().timeAsString(0);
    }

    public int getBestTime(Player p, String parkour) {
        if(main.getPlayerDataHandler().playerExists(p) && main.getPlayerDataHandler().getData(p).getLastTimes().containsKey(parkour)) {
            return main.getPlayerDataHandler().getData(p).getBestTimes().get(parkour);
        }
        return 0;
    }

    public String getBestTimeFormatted(Player p, String parkour) {
        if(main.getPlayerDataHandler().playerExists(p) && main.getPlayerDataHandler().getData(p).getLastTimes().containsKey(parkour)) {
            return main.getTimerManager().timeAsString(main.getPlayerDataHandler().getData(p).getBestTimes().get(parkour));
        }
        return main.getTimerManager().timeAsString(0);
    }

    public HashMap<String, Parkour> getParkours() {
        return main.getParkourHandler().getParkours();
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
        return main.getPlayerDataHandler().getData(p).getParkour();
    }

}
