package me.davidml16.aparkour.handlers;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.data.LeaderboardEntry;
import me.davidml16.aparkour.data.Parkour;
import org.bukkit.Bukkit;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class LeaderboardHandler {

    private Map<String, Map<Integer, LeaderboardEntry>> leaderboards;

    private Main main;

    public LeaderboardHandler(Main main) {
        this.main = main;
        this.leaderboards = new HashMap<String, Map<Integer, LeaderboardEntry>>();
    }

    public Map<Integer, LeaderboardEntry> getLeaderboard(String parkour) {
        return leaderboards.get(parkour);
    }

    public void reloadLeaderboards() {
        for (Parkour parkour : main.getParkourHandler().getParkours().values()) {
            HashMap<String, Long> times = main.getDatabaseHandler().getParkourBestTimes(parkour.getId(), 10);

            Map<Integer, LeaderboardEntry> leaderboard = new HashMap<>();

            int it = 0;
            for (Map.Entry<String, Long> entry : times.entrySet()) {
                try {
                    leaderboard.put(it, new LeaderboardEntry( main.getDatabaseHandler().getPlayerName(entry.getKey()), entry.getValue()));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                it++;
            }
            for (int i = it; i < 10; i++) {
                leaderboard.put(i, new LeaderboardEntry("NONE", 0L));
            }

            leaderboards.put(parkour.getId(), leaderboard);
        }
    }

}
