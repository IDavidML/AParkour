package me.davidml16.aparkour.data;

public class Pair {

    private String parkour;
    private int page;

    public Pair(String parkour, int page) {
        this.parkour = parkour;
        this.page = page;
    }

    public String getParkour() {
        return parkour;
    }

    public int getPage() {
        return page;
    }

}
