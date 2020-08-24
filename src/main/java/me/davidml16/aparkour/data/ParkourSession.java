package me.davidml16.aparkour.data;

import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.utils.ActionBar;
import me.davidml16.aparkour.utils.MillisecondConverter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

public class ParkourSession {

    private Main main;

    private Player player;
    private Parkour parkour;

    private Integer lastCheckpoint;
    private Location lastCheckpointLocation;

    private long startTime;

    private int taskId = 0;

    public ParkourSession(Main main, Player player, Parkour parkour) {
        this.main = main;
        this.player = player;
        this.parkour = parkour;
        this.lastCheckpoint = -1;
        this.lastCheckpointLocation = null;
        this.parkour.getPlaying().add(player.getUniqueId());
        startTimer();
    }

    public Player getPlayer() {
        return player;
    }

    public Parkour getParkour() {
        return parkour;
    }

    public Integer getLastCheckpoint() {
        return lastCheckpoint;
    }

    public void setLastCheckpoint(Integer lastCheckpoint) {
        this.lastCheckpoint = lastCheckpoint;
    }

    public Location getLastCheckpointLocation() {
        return lastCheckpointLocation;
    }

    public void setLastCheckpointLocation(Location lastCheckpointLocation) { this.lastCheckpointLocation = lastCheckpointLocation; }

    public long getStartTime() { return startTime; }

    public long getLiveTime() {
        return System.currentTimeMillis() - this.startTime;
    }

    public void startTimer() {
        this.startTime = System.currentTimeMillis();
        long best = 0;
        try {
            best = main.getPlayerDataHandler().getData(player).getBestTimes().get(parkour.getId());
        } catch (NullPointerException e) {
            best = 0;
        }

        if (main.getTimerManager().isActionBarEnabled()) {
            sendTimer(best);
        }

        long finalBest = best;
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                if ((int) TimeUnit.MILLISECONDS.toHours(getLiveTime()) < 1) {
                    sendTimer(finalBest);
                } else if ((int) TimeUnit.MILLISECONDS.toHours(getLiveTime()) >= 24) {
                    Bukkit.getScheduler().runTask(main, () -> {
                        cancelTimer();

                        player.setFlying(false);
                        player.teleport(parkour.getSpawn());

                        main.getParkourHandler().resetPlayer(player);

                        main.getSoundUtil().playReturn(player);
                    });
                }
            }
        }.runTaskTimerAsynchronously(main, 0, 1);

        taskId = task.getTaskId();
    }

    public void cancelTimer() {
        if (taskId > 0) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = 0;
        }
    }

    public void sendTimer(long best) {
        String message = main.getLanguageHandler().getMessage("Timer.ActionBar");
        if(message.length() > 0) {
            ActionBar.sendActionBar(player, message
                    .replaceAll("%currentTime%", main.getTimerManager().millisToString(main.getLanguageHandler().getMessage("Timer.Formats.ParkourTimer"), getLiveTime()))
                    .replaceAll("%bestTime%", best > 0 ? main.getTimerManager().millisToString(main.getLanguageHandler().getMessage("Timer.Formats.ParkourTimer"), best) : main.getLanguageHandler().getMessage("Times.NoBestTime")));
        }
    }

    @Override
    public String toString() {
        return "ParkourSession{" +
                "player=" + player +
                ", parkour=" + parkour +
                ", lastCheckpoint=" + lastCheckpoint +
                ", lastCheckpointLocation=" + lastCheckpointLocation +
                ", startTime=" + startTime +
                '}';
    }
}
