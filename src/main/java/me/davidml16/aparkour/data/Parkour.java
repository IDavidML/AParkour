package me.davidml16.aparkour.data;

import java.util.*;

import org.bukkit.Location;

import me.davidml16.aparkour.Main;
import org.bukkit.Material;

public class Parkour {

    private String id;
    private String name;
    private Location spawn;

    private Plate start;
    private Plate end;

    private Location statsHologram;
    private Location topHologram;

    private List<WalkableBlock> walkableBlocks;
    private List<Reward> rewards;
    private List<Plate> checkpoints;
    private List<Location> checkpointLocations;

    private boolean permissionRequired;
    private String permission;
    private String permissionMessage;

    public Parkour(String id, String name, Location spawn, Location start, Location end, Location statsHologram, Location topHologram) {
        this.id = id;
        this.name = name;
        this.spawn = spawn;
        this.start = new Plate(start);
        this.end = new Plate(end);
        this.statsHologram = statsHologram;
        this.topHologram = topHologram;
        this.walkableBlocks = new ArrayList<WalkableBlock>();
        this.rewards = new ArrayList<Reward>();
        this.checkpoints = new ArrayList<Plate>();
        this.checkpointLocations = new ArrayList<Location>();
        this.permissionRequired = false;
        this.permission = "";
        this.permissionMessage = "";
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Location getSpawn() {
        return spawn;
    }

    public Plate getStart() {
        return start;
    }

    public Plate getEnd() {
        return end;
    }

    public Location getStatsHologram() {
        return statsHologram;
    }

    public Location getTopHologram() {
        return topHologram;
    }

    public List<WalkableBlock> getWalkableBlocks() {
        return walkableBlocks;
    }

    public void setWalkableBlocks(List<WalkableBlock> walkableBlocks) {
        this.walkableBlocks = walkableBlocks;
    }

    public List<Reward> getRewards() {
        return rewards;
    }

    public void setRewards(List<Reward> rewards) {
        this.rewards = rewards;
    }

    public List<Plate> getCheckpoints() {
        return checkpoints;
    }

    public void setCheckpoints(List<Plate> checkpoints) {
        this.checkpoints = checkpoints;
    }

    public List<Location> getCheckpointLocations() {
        return checkpointLocations;
    }

    public void setCheckpointLocations(List<Location> checkpointLocations) {
        this.checkpointLocations = checkpointLocations;
    }

    public boolean isPermissionRequired() {
        return permissionRequired;
    }

    public void setPermissionRequired(boolean permissionRequired) {
        this.permissionRequired = permissionRequired;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getPermissionMessage() {
        return permissionMessage;
    }

    public void setPermissionMessage(String permissionMessage) {
        this.permissionMessage = permissionMessage;
    }

    public void saveParkour() {
        Main.getInstance().getParkourHandler().getConfig(id).set("parkour.walkableBlocks", Main.getInstance().getParkourHandler().getWalkableBlocksString(walkableBlocks));

        Main.getInstance().getParkourHandler().getConfig(id).set("parkour.rewards", new ArrayList<>());
        if (Main.getInstance().getParkourHandler().getConfig(id).contains("parkour.rewards")) {
            for (Reward reward : rewards) {
                Main.getInstance().getParkourHandler().getConfig(id).set("parkour.rewards." + reward.getId() + ".firstTime", reward.isFirstTime());
                Main.getInstance().getParkourHandler().getConfig(id).set("parkour.rewards." + reward.getId() + ".permission", reward.getPermission());
                Main.getInstance().getParkourHandler().getConfig(id).set("parkour.rewards." + reward.getId() + ".command", reward.getCommand());
            }
        }

        Main.getInstance().getParkourHandler().getConfig(id).set("parkour.checkpoints", new ArrayList<>());
        if (Main.getInstance().getParkourHandler().getConfig(id).contains("parkour.checkpoints")) {
            int iterator = 0;
            for (Plate checkpoint : checkpoints) {
                Main.getInstance().getParkourHandler().getConfig(id).set("parkour.checkpoints." + iterator, checkpoint.getLocation());
                iterator++;
            }
        }

        if (!Main.getInstance().getParkourHandler().getConfig(id).contains("parkour.permissionRequired")) {
            Main.getInstance().getParkourHandler().getConfig(id).set("parkour.permissionRequired.enabled", false);
            Main.getInstance().getParkourHandler().getConfig(id).set("parkour.permissionRequired.permission", "aparkour.permission." + id);
            Main.getInstance().getParkourHandler().getConfig(id).set("parkour.permissionRequired.enabled", "&cYou dont have permission to start this parkour!");
        }

        Main.getInstance().getParkourHandler().saveConfig(id);
    }

}
