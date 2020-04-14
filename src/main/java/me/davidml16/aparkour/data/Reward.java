package me.davidml16.aparkour.data;

public class Reward {

    private String id;
    private String permission;
    private String command;
    private boolean firstTime;
    private int chance;

    public Reward(String id, String permission, String command, boolean firstTime, int chance) {
        this.id = id;
        this.permission = permission;
        this.command = command;
        this.firstTime = firstTime;
        this.chance = chance;
    }

    public String getId() {
        return id;
    }

    public String getPermission() {
        return permission;
    }

    public String getCommand() {
        return command;
    }

    public boolean isFirstTime() {
        return firstTime;
    }

    public int getChance() {
        return chance;
    }

    @Override
    public String toString() {
        return "Reward{" +
                "id='" + id + '\'' +
                ", permission='" + permission + '\'' +
                ", command='" + command + '\'' +
                ", firstTime=" + firstTime +
                ", chance=" + chance +
                '}';
    }
}
