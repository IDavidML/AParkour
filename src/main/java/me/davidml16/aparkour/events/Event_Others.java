package me.davidml16.aparkour.events;

import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.data.Parkour;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import java.sql.SQLException;

public class Event_Others implements Listener {

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if (Main.getInstance().getTimerManager().hasPlayerTimer(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        if (Main.getInstance().getTimerManager().hasPlayerTimer(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent e) {
        if (Main.getInstance().getTimerManager().hasPlayerTimer((Player) e.getEntity())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        if (Main.getInstance().getTimerManager().hasPlayerTimer(e.getPlayer())) {
            e.setCancelled(true);
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> e.getPlayer().updateInventory(), 1L);
        }
    }

    @EventHandler
    public void inventoryClick(InventoryClickEvent e) {
        if (Main.getInstance().getTimerManager().hasPlayerTimer((Player) e.getWhoClicked())) {
            e.setCancelled(true);
            ((Player) e.getWhoClicked()).updateInventory();
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            if (Main.getInstance().getTimerManager().hasPlayerTimer((Player) e.getEntity())) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInteract(EntityInteractEvent e) {
        if (Main.getInstance().getParkourHandler().getParkourByLocation(e.getBlock().getLocation()) != null) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent e) {
        if (Main.getInstance().getTimerManager().hasPlayerTimer(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) throws SQLException {
        Player p = e.getPlayer();
        Main.getInstance().getPlayerDataHandler().loadPlayerData(p);
        Main.getInstance().getDatabaseHandler().updatePlayerName(p);
        Main.getInstance().getStatsHologramManager().loadStatsHolograms(p);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();

        Main.getInstance().getStatsHologramManager().removeStatsHolograms(p);
        Main.getInstance().getStatsGUI().getOpened().remove(p.getUniqueId());
        Main.getInstance().getStatsGUI().getGuis().remove(p.getUniqueId());
        Main.getInstance().getRankingsGUI().getOpened().remove(p.getUniqueId());
        Main.getInstance().getConfigGUI().getOpened().remove(p.getUniqueId());
        Main.getInstance().getWalkableBlocksGUI().getOpened().remove(p.getUniqueId());
        Main.getInstance().getRewardsGUI().getOpened().remove(p.getUniqueId());

        if (Main.getInstance().getTimerManager().hasPlayerTimer(e.getPlayer())) {
            Main.getInstance().getTimerManager().cancelTimer(e.getPlayer());

            Main.getInstance().getPlayerDataHandler().getData(p).setParkour(null);

            if (Main.getInstance().getConfig().getBoolean("RestartItem.Enabled")) {
                Main.getInstance().getPlayerDataHandler().restorePlayerInventory(p);
            }
        }

        Main.getInstance().getPlayerDataHandler().getData(p).save();
        Main.getInstance().getPlayerDataHandler().getPlayersData().remove(p.getUniqueId());
    }

    @EventHandler
    public void onWorld(PlayerChangedWorldEvent e) {
        Player p = e.getPlayer();
        Main.getInstance().getStatsHologramManager().reloadStatsHolograms(p);

        if (Main.getInstance().getTimerManager().hasPlayerTimer(e.getPlayer())) {
            Main.getInstance().getTimerManager().cancelTimer(e.getPlayer());

            Parkour parkour = Main.getInstance().getPlayerDataHandler().getData(p).getParkour();
            Main.getInstance().getPlayerDataHandler().getData(p).setParkour(null);

            if (Main.getInstance().getConfig().getBoolean("RestartItem.Enabled")) {
                Main.getInstance().getPlayerDataHandler().restorePlayerInventory(p);
            }
        }
    }
}
