package me.davidml16.aparkour.data;

import java.util.*;

import me.davidml16.aparkour.utils.ItemBuilder;
import org.bukkit.Location;

import me.davidml16.aparkour.Main;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

public class Parkour {

    private Main main;

    private String id;
    private String name;
    private Location spawn;

    private ItemStack icon;

    private Plate start;
    private Plate end;

    private Location statsHologram;
    private Location topHologram;

    private List<WalkableBlock> walkableBlocks;
    private List<Reward> rewards;
    private List<Plate> checkpoints;
    private List<Location> checkpointLocations;

    private Collection<UUID> playing;

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
        this.icon = new ItemBuilder(Material.getMaterial(389), 1).toItemStack();
        this.playing = new HashSet<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) { this.name = name; }

    public Location getSpawn() {
        return spawn;
    }

    public Plate getStart() {
        return start;
    }

    public Plate getEnd() {
        return end;
    }

    public Location getStatsHologram() { return statsHologram; }

    public void setStatsHologram(Location statsHologram) { this.statsHologram = statsHologram; }

    public Location getTopHologram() { return topHologram; }

    public void setTopHologram(Location topHologram) { this.topHologram = topHologram; }

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

    public ItemStack getIcon() {
        return icon;
    }

    public void setIcon(ItemStack icon) {
        this.icon = icon;
    }

    public Collection<UUID> getPlaying() { return playing; }

    public void setPlaying(Collection<UUID> playing) { this.playing = playing; }

    public void saveParkour() {
        FileConfiguration config = main.getParkourHandler().getConfig(id);
        config.set("parkour.walkableBlocks", main.getParkourHandler().getWalkableBlocksString(walkableBlocks));

        config.set("parkour.rewards", new ArrayList<>());
        if (config.contains("parkour.rewards")) {
            for (Reward reward : rewards) {
                config.set("parkour.rewards." + reward.getId() + ".firstTime", reward.isFirstTime());
                config.set("parkour.rewards." + reward.getId() + ".permission", reward.getPermission());
                config.set("parkour.rewards." + reward.getId() + ".command", reward.getCommand());
                config.set("parkour.rewards." + reward.getId() + ".chance", reward.getChance());
            }
        }

        config.set("parkour.checkpoints", new ArrayList<>());
        if (config.contains("parkour.checkpoints")) {
            int iterator = 0;
            for (Plate checkpoint : checkpoints) {
                config.set("parkour.checkpoints." + iterator, checkpoint.getLocation());
                iterator++;
            }
        }

        if (!config.contains("parkour.permissionRequired")) {
            config.set("parkour.permissionRequired.enabled", false);
            config.set("parkour.permissionRequired.permission", "aparkour.permission." + id);
            config.set("parkour.permissionRequired.enabled", "&cYou dont have permission to start this parkour!");
        }

        if (!config.contains("parkour.titles.start")) {
            config.set("parkour.titles.start.enabled", false);
        }
        if (!config.contains("parkour.titles.end")) {
            config.set("parkour.titles.end.enabled", false);
        }
        if (!config.contains("parkour.titles.checkpoint")) {
            config.set("parkour.titles.checkpoint.enabled", false);
        }

        main.getParkourHandler().saveConfig(id);
    }

    @Override
    public String toString() {
        return "Parkour{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", spawn=" + spawn +
                ", start=" + start +
                ", end=" + end +
                ", statsHologram=" + statsHologram +
                ", topHologram=" + topHologram +
                ", walkableBlocks=" + walkableBlocks +
                ", rewards=" + rewards +
                ", checkpoints=" + checkpoints +
                ", checkpointLocations=" + checkpointLocations +
                ", permissionRequired=" + permissionRequired +
                ", permission='" + permission + '\'' +
                ", permissionMessage='" + permissionMessage + '\'' +
                ", startTitleEnabled=" + startTitleEnabled +
                ", endTitleEnabled=" + endTitleEnabled +
                ", checkpointTitleEnabled=" + checkpointTitleEnabled +
                '}';
    }
}
