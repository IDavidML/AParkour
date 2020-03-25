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

    public static void setPosition(Player p, String id, String type) {
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

        Main.getInstance().getParkourHandler().getConfig().set("parkours." + id + "." + type, location);
        Main.getInstance().getParkourHandler().saveConfig();

        p.sendMessage(ColorManager.translate(Main.getInstance().getLanguageHandler().getPrefix()
                + "&aSuccesfully set the " + type + " location of parkour &e" + id));
    }

    public static void setHologram(Player p, String id, String type) {
        double x = p.getLocation().getBlockX();
        double y = p.getLocation().getBlockY();
        double z = p.getLocation().getBlockZ();
        int pitch = Math.round(p.getLocation().getPitch());
        int yaw = Math.round(p.getLocation().getYaw());

        Location location = new Location(p.getWorld(), x, y, z, yaw, pitch);

        Main.getInstance().getParkourHandler().getConfig().set("parkours." + id + ".holograms." + type, location);
        Main.getInstance().getParkourHandler().saveConfig();

        p.sendMessage(ColorManager.translate(Main.getInstance().getLanguageHandler().getPrefix()
                + "&aSuccesfully set the " + type + " location of parkour &e" + id));
    }

}
