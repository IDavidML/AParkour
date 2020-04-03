package me.davidml16.aparkour.managers;

import me.davidml16.aparkour.data.Parkour;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class PlateManager {

    public void loadPlates(Parkour parkour) {
        Block start = parkour.getStart().getLocation().getWorld().getBlockAt(parkour.getStart().getLocation());
        if(start.getType() != Material.IRON_PLATE) {
            start.setType(Material.IRON_PLATE);
        }

        Block end = parkour.getEnd().getLocation().getWorld().getBlockAt(parkour.getEnd().getLocation());
        if(end.getType() != Material.GOLD_PLATE) {
            end.setType(Material.GOLD_PLATE);
        }

        for(Location checkpoint : parkour.getCheckpointLocations()) {
            Block cp = checkpoint.getWorld().getBlockAt(checkpoint);
            if(cp.getType() != Material.IRON_PLATE) {
                cp.setType(Material.IRON_PLATE);
            }
        }
    }

}
