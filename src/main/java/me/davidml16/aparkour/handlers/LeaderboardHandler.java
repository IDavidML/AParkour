package me.davidml16.aparkour.handlers;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.data.LeaderboardEntry;
import me.davidml16.aparkour.data.Parkour;
import org.bukkit.Bukkit;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LeaderboardHandler {

    private Map<String, List<LeaderboardEntry>> leaderboards;

    private Map<UUID, String> playerNames;

    private Main main;

    public LeaderboardHandler(Main main) {
        this.main = main;
        this.leaderboards = new HashMap<String, List<LeaderboardEntry>>();
        this.playerNames = new HashMap<UUID, String>();
    }

    public List<LeaderboardEntry> getLeaderboard(String parkour) {
        return leaderboards.get(parkour);
    }

    public void addLeaderboard(String id, List<LeaderboardEntry> times) {
        leaderboards.put(id, times);
    }

    public Map<UUID, String> getPlayerNames() {
        return playerNames;
    }

    public void setPlayerNames(Map<UUID, String> playerNames) {
        this.playerNames = playerNames;
    }
}
