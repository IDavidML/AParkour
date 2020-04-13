package me.davidml16.aparkour.data;

public class LeaderboardEntry {

    private String name;
    private Long time;

    public LeaderboardEntry(String name, Long time) {
        this.name = name;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

}
