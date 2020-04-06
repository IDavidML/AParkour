package me.davidml16.aparkour.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.data.Parkour;
import me.davidml16.aparkour.managers.ColorManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class LocationUtil {

    private Main main;
    public LocationUtil(Main main) {
        this.main = main;
    }

    public void setPosition(Player p, String id, String type) {
        double x = p.getLocation().getBlockX();
        double y = p.getLocation().getBlockY();
        double z = p.getLocation().getBlockZ();
        int pitch = Math.round(p.getLocation().getPitch());
        int yaw = Math.round(p.getLocation().getYaw());

        if (type.equalsIgnoreCase("spawn")) {
            x += 0.5;
            z += 0.5;
        } else {
            pitch = 0;
            yaw = 0;
        }

        Location location = new Location(p.getWorld(), x, y, z, yaw, pitch);

       main.getParkourHandler().getConfig(id).set("parkour." + type, location);
       main.getParkourHandler().saveConfig(id);

        p.sendMessage(ColorManager.translate(main.getLanguageHandler().getPrefix()
                + " &aSuccesfully set the " + type + " location of parkour &e" + id));
    }

    public void setHologram(Player p, String id, String type) {
        double x = p.getLocation().getBlockX();
        double y = p.getLocation().getBlockY();
        double z = p.getLocation().getBlockZ();
        int pitch = Math.round(p.getLocation().getPitch());
        int yaw = Math.round(p.getLocation().getYaw());

        Location location = new Location(p.getWorld(), x, y, z, yaw, pitch);

       main.getParkourHandler().getConfig(id).set("parkour.holograms." + type, location);
       main.getParkourHandler().saveConfig(id);

        p.sendMessage(ColorManager.translate(main.getLanguageHandler().getPrefix()
                + " &aSuccesfully set the " + type + " location of parkour &e" + id));
    }

    public Location getPosition(Player p) {
        double x = p.getLocation().getBlockX();
        double y = p.getLocation().getBlockY();
        double z = p.getLocation().getBlockZ();

        return new Location(p.getWorld(), x, y, z, 0, 0);
    }

}
