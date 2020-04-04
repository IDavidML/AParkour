package me.davidml16.aparkour.events;

import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.data.Parkour;
import me.davidml16.aparkour.managers.ColorManager;
import me.davidml16.aparkour.utils.ParkourItems;
import me.davidml16.aparkour.utils.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

import java.sql.SQLException;

public class Event_Others implements Listener {

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if (Main.getInstance().getTimerManager().hasPlayerTimer(e.getPlayer())) {
            e.setCancelled(true);
        } else {
            if(Main.getInstance().getParkourHandler().getParkourByLocation(e.getBlock().getLocation()) != null) {
                Player p = e.getPlayer();
                if (!Main.getInstance().getPlayerDataHandler().playerHasPermission(p, "aparkour.admin")) {
                    e.setCancelled(true);
                } else {
                    if (!p.getGameMode().equals(GameMode.CREATIVE)) {
                        e.setCancelled(true);
                        p.sendMessage(ColorManager.translate(Main.getInstance().getLanguageHandler().getPrefix() + " &cYou need &eCREATIVE &cmode to break the plate"));
                        Sounds.playSound(p, p.getLocation(), Sounds.MySound.NOTE_PLING, 10, 0);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        if(Main.getInstance().getTimerManager().hasPlayerTimer(e.getPlayer())) {
            if (e.getCause().equals(PlayerTeleportEvent.TeleportCause.COMMAND)
                    || e.getCause().equals(PlayerTeleportEvent.TeleportCause.PLUGIN)
                    || e.getCause().equals(PlayerTeleportEvent.TeleportCause.ENDER_PEARL)) {
                e.setCancelled(true);
            }
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
            Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> e.getPlayer().updateInventory(), 1L);
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
        Main.getInstance().getCheckpointsGUI().getOpened().remove(p.getUniqueId());

        if (Main.getInstance().getTimerManager().hasPlayerTimer(e.getPlayer())) {
            Main.getInstance().getTimerManager().cancelTimer(e.getPlayer());

            Main.getInstance().getPlayerDataHandler().getData(p).setParkour(null);

            if (Main.getInstance().isParkourItemsEnabled()) {
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

            if (Main.getInstance().isParkourItemsEnabled()) {
                Main.getInstance().getPlayerDataHandler().restorePlayerInventory(p);
            }
        }
    }
}
