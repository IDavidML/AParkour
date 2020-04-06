package me.davidml16.aparkour.data;

import java.util.*;

import org.bukkit.Location;

import me.davidml16.aparkour.Main;
import org.bukkit.Material;

public class Parkour {

    private Main main;

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

    private boolean startTitleEnabled;
    private boolean endTitleEnabled;
    private boolean checkpointTitleEnabled;

    public Parkour(Main main, String id, String name, Location spawn, Location start, Location end, Location statsHologram, Location topHologram) {
        this.main = main;
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
        this.startTitleEnabled = false;
        this.endTitleEnabled = false;
        this.checkpointTitleEnabled = false;
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

    public boolean isStartTitleEnabled() {
        return startTitleEnabled;
    }

    public void setStartTitleEnabled(boolean startTitleEnabled) {
        this.startTitleEnabled = startTitleEnabled;
    }

    public boolean isEndTitleEnabled() {
        return endTitleEnabled;
    }

    public void setEndTitleEnabled(boolean endTitleEnabled) {
        this.endTitleEnabled = endTitleEnabled;
    }

    public boolean isCheckpointTitleEnabled() {
        return checkpointTitleEnabled;
    }

    public void setCheckpointTitleEnabled(boolean checkpointTitleEnabled) {
        this.checkpointTitleEnabled = checkpointTitleEnabled;
    }

    public void saveParkour() {
        main.getParkourHandler().getConfig(id).set("parkour.walkableBlocks", main.getParkourHandler().getWalkableBlocksString(walkableBlocks));

        main.getParkourHandler().getConfig(id).set("parkour.rewards", new ArrayList<>());
        if (main.getParkourHandler().getConfig(id).contains("parkour.rewards")) {
            for (Reward reward : rewards) {
                main.getParkourHandler().getConfig(id).set("parkour.rewards." + reward.getId() + ".firstTime", reward.isFirstTime());
                main.getParkourHandler().getConfig(id).set("parkour.rewards." + reward.getId() + ".permission", reward.getPermission());
                main.getParkourHandler().getConfig(id).set("parkour.rewards." + reward.getId() + ".command", reward.getCommand());
                main.getParkourHandler().getConfig(id).set("parkour.rewards." + reward.getId() + ".chance", reward.getChance());
            }
        }

        main.getParkourHandler().getConfig(id).set("parkour.checkpoints", new ArrayList<>());
        if (main.getParkourHandler().getConfig(id).contains("parkour.checkpoints")) {
            int iterator = 0;
            for (Plate checkpoint : checkpoints) {
                main.getParkourHandler().getConfig(id).set("parkour.checkpoints." + iterator, checkpoint.getLocation());
                iterator++;
            }
        }

        if (!main.getParkourHandler().getConfig(id).contains("parkour.permissionRequired")) {
            main.getParkourHandler().getConfig(id).set("parkour.permissionRequired.enabled", false);
            main.getParkourHandler().getConfig(id).set("parkour.permissionRequired.permission", "aparkour.permission." + id);
            main.getParkourHandler().getConfig(id).set("parkour.permissionRequired.enabled", "&cYou dont have permission to start this parkour!");
        }

        if (!main.getParkourHandler().getConfig(id).contains("parkour.titles.start")) {
            main.getParkourHandler().getConfig(id).set("parkour.titles.start.enabled", false);
        }
        if (!main.getParkourHandler().getConfig(id).contains("parkour.titles.end")) {
            main.getParkourHandler().getConfig(id).set("parkour.titles.end.enabled", false);
        }
        if (!main.getParkourHandler().getConfig(id).contains("parkour.titles.checkpoint")) {
            main.getParkourHandler().getConfig(id).set("parkour.titles.checkpoint.enabled", false);
        }

        main.getParkourHandler().saveConfig(id);
    }

}
