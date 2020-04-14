package me.davidml16.aparkour.data;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import org.bukkit.Location;

public class Plate {

    private Location location;

    private Hologram hologram;
    private boolean hologramEnabled;
    private double hologramDistance;

    public Plate(Location location) {
        this.location = location;
        this.hologram = null;
        this.hologramEnabled = false;
        this.hologramDistance = 2.5;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Hologram getHologram() {
        return hologram;
    }

    public void setHologram(Hologram hologram) {
        this.hologram = hologram;
    }

    public boolean isHologramEnabled() {
        return hologramEnabled;
    }

    public void setHologramEnabled(boolean hologramEnabled) {
        this.hologramEnabled = hologramEnabled;
    }

    public double getHologramDistance() {
        return hologramDistance;
    }

    public void setHologramDistance(double hologramDistance) {
        this.hologramDistance = hologramDistance;
    }

    @Override
    public String toString() {
        return "Plate{" +
                "location=" + location +
                ", hologram=" + hologram +
                ", hologramEnabled=" + hologramEnabled +
                ", hologramDistance=" + hologramDistance +
                '}';
    }
}
