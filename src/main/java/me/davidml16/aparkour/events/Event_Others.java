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

    private Main main;
    public Event_Others(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if (main.getTimerManager().hasPlayerTimer(e.getPlayer())) {
            e.setCancelled(true);
        } else {
            if(main.getParkourHandler().getParkourByLocation(e.getBlock().getLocation()) != null) {
                Player p = e.getPlayer();
                if (!main.getPlayerDataHandler().playerHasPermission(p, "aparkour.admin")) {
                    e.setCancelled(true);
                } else {
                    if (!p.getGameMode().equals(GameMode.CREATIVE)) {
                        e.setCancelled(true);
                        p.sendMessage(ColorManager.translate(main.getLanguageHandler().getPrefix() + " &cYou need &eCREATIVE &cmode to break the plate"));
                        Sounds.playSound(p, p.getLocation(), Sounds.MySound.NOTE_PLING, 10, 0);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        if(main.getTimerManager().hasPlayerTimer(e.getPlayer())) {
            if (e.getCause().equals(PlayerTeleportEvent.TeleportCause.COMMAND)
                    || e.getCause().equals(PlayerTeleportEvent.TeleportCause.PLUGIN)
                    || e.getCause().equals(PlayerTeleportEvent.TeleportCause.ENDER_PEARL)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        if (main.getTimerManager().hasPlayerTimer(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent e) {
        if (main.getTimerManager().hasPlayerTimer((Player) e.getEntity())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        if (main.getTimerManager().hasPlayerTimer(e.getPlayer())) {
            e.setCancelled(true);
            Bukkit.getScheduler().runTaskLaterAsynchronously(main, () -> e.getPlayer().updateInventory(), 1L);
        }
    }

    @EventHandler
    public void inventoryClick(InventoryClickEvent e) {
        if (main.getTimerManager().hasPlayerTimer((Player) e.getWhoClicked())) {
            e.setCancelled(true);
            ((Player) e.getWhoClicked()).updateInventory();
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            if (main.getTimerManager().hasPlayerTimer((Player) e.getEntity())) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInteract(EntityInteractEvent e) {
        if (main.getParkourHandler().getParkourByLocation(e.getBlock().getLocation()) != null) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent e) {
        if (main.getTimerManager().hasPlayerTimer(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) throws SQLException {
        Player p = e.getPlayer();
        main.getPlayerDataHandler().loadPlayerData(p);
        main.getDatabaseHandler().updatePlayerName(p);
        main.getStatsHologramManager().loadStatsHolograms(p);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();

        main.getStatsHologramManager().removeStatsHolograms(p);
        main.getStatsGUI().getOpened().remove(p.getUniqueId());
        main.getStatsGUI().getGuis().remove(p.getUniqueId());
        main.getRankingsGUI().getOpened().remove(p.getUniqueId());
        main.getConfigGUI().getOpened().remove(p.getUniqueId());
        main.getWalkableBlocksGUI().getOpened().remove(p.getUniqueId());
        main.getRewardsGUI().getOpened().remove(p.getUniqueId());
        main.getCheckpointsGUI().getOpened().remove(p.getUniqueId());

        if (main.getTimerManager().hasPlayerTimer(e.getPlayer())) {
            main.getTimerManager().cancelTimer(e.getPlayer());

            main.getPlayerDataHandler().getData(p).setParkour(null);

            if (main.isParkourItemsEnabled()) {
                main.getPlayerDataHandler().restorePlayerInventory(p);
            }
        }

        main.getPlayerDataHandler().getData(p).save();
        main.getPlayerDataHandler().getPlayersData().remove(p.getUniqueId());
    }

    @EventHandler
    public void onWorld(PlayerChangedWorldEvent e) {
        Player p = e.getPlayer();
        main.getStatsHologramManager().reloadStatsHolograms(p);

        if (main.getTimerManager().hasPlayerTimer(e.getPlayer())) {
            main.getTimerManager().cancelTimer(e.getPlayer());

            Parkour parkour = main.getPlayerDataHandler().getData(p).getParkour();
            main.getPlayerDataHandler().getData(p).setParkour(null);

            if (main.isParkourItemsEnabled()) {
                main.getPlayerDataHandler().restorePlayerInventory(p);
            }
        }
    }
}
