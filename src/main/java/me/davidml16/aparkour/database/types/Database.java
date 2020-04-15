package me.davidml16.aparkour.database.types;

import me.davidml16.aparkour.data.LeaderboardEntry;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface Database {

    void close();

    void open();

    void loadTables();

    boolean hasData(UUID uuid, String parkour) throws SQLException;

    void createData(UUID uuid, String parkour) throws SQLException;

    boolean hasName(Player p) throws SQLException;

    void updatePlayerName(Player p) throws SQLException;

    String getPlayerName(String uuid) throws SQLException;

    Long getLastTime(UUID uuid, String parkour) throws SQLException;

    Long getBestTime(UUID uuid, String parkour) throws SQLException;

    void setTimes(UUID uuid, Long lastTime, Long bestTime, String parkour) throws SQLException;

    CompletableFuture<Map<String, Long>> getPlayerLastTimes(UUID uuid);

    CompletableFuture<Map<String, Long>> getPlayerBestTimes(UUID uuid);

    CompletableFuture<List<LeaderboardEntry>> getParkourBestTimes(String id, int amount);

}
