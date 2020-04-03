package me.davidml16.aparkour.handlers;

import me.davidml16.aparkour.data.Parkour;
import me.davidml16.aparkour.data.Plate;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckpointsHandler {

    public void loadCheckpoints(Parkour parkour, FileConfiguration config) {
        List<Plate> checkpoints = new ArrayList<Plate>();
        List<Location> checkpointLocations = new ArrayList<Location>();
        if (config.contains("parkour.checkpoints")) {
            if (config.getConfigurationSection("parkour.checkpoints") != null) {
                for (String id : config.getConfigurationSection("parkour.checkpoints").getKeys(false)) {
                    Location loc = (Location) config.get("parkour.checkpoints." + Integer.parseInt(id));
                    checkpoints.add(new Plate(loc));
                    checkpointLocations.add(loc);
                }
                parkour.setCheckpoints(checkpoints);
                parkour.setCheckpointLocations(checkpointLocations);
            }
            System.out.println("Loaded for parkour " + parkour.getId() + " - " + checkpoints.size() + " checkpoints.");
        }
    }

}
