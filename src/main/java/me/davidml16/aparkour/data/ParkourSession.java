package me.davidml16.aparkour.data;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ParkourSession {

    private Player player;
    private Parkour parkour;

    private Integer lastCheckpoint;
    private Location lastCheckpointLocation;

    public ParkourSession(Player player, Parkour parkour) {
        this.player = player;
        this.parkour = parkour;
        this.lastCheckpoint = -1;
        this.lastCheckpointLocation = null;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Parkour getParkour() {
        return parkour;
    }

    public void setParkour(Parkour parkour) {
        this.parkour = parkour;
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

    public void setLastCheckpointLocation(Location lastCheckpointLocation) {
        this.lastCheckpointLocation = lastCheckpointLocation;
    }

}
