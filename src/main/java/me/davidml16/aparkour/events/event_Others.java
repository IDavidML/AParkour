package me.davidml16.aparkour.events;

import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.data.ParkourData;

public class event_Others implements Listener {

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
	public void onDrop(final PlayerDropItemEvent e) {
		if (Main.getInstance().getTimerManager().hasPlayerTimer(e.getPlayer())) {
			e.setCancelled(true);
			Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
				public void run() {
					e.getPlayer().updateInventory();
				}
			}, 1L);
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
		if(e.getEntity() instanceof Player) {
			if (Main.getInstance().getTimerManager().hasPlayerTimer((Player) e.getEntity())) {
				e.setCancelled(true);
			}
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

		if (Main.getInstance().getStatsGUI().getOpened().contains(p.getUniqueId()))
			Main.getInstance().getStatsGUI().getOpened().remove(p.getUniqueId());
		
		if (Main.getInstance().getStatsGUI().getGuis().containsKey(p.getUniqueId()))
			Main.getInstance().getStatsGUI().getGuis().remove(p.getUniqueId());

		if (Main.getInstance().getTimerManager().hasPlayerTimer(e.getPlayer())) {
			Main.getInstance().getTimerManager().cancelTimer(e.getPlayer());

			ParkourData parkour = Main.getInstance().getParkourHandler().getParkourByPlayer(p);
			parkour.getPlayers().remove(p.getUniqueId());

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

			ParkourData parkour = Main.getInstance().getParkourHandler().getParkourByPlayer(p);
			parkour.getPlayers().remove(p.getUniqueId());

			if (Main.getInstance().getConfig().getBoolean("RestartItem.Enabled")) {
				Main.getInstance().getPlayerDataHandler().restorePlayerInventory(p);
			}
		}
	}
}
