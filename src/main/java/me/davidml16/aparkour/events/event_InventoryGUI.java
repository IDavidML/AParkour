package me.davidml16.aparkour.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import me.davidml16.aparkour.Main;

public class event_InventoryGUI implements Listener {

	@EventHandler
	public void onInventoryClickEvent(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		
		if(e.getCurrentItem() == null) return;
		
		if (Main.getInstance().getStatsGUI().getOpened().contains(p.getUniqueId())) {
			if (p.getOpenInventory().getTopInventory().equals(Main.getInstance().getStatsGUI().getGuis().get(p.getUniqueId()))) {
				e.setCancelled(true);
				return;
			}
			return;
		} else if (Main.getInstance().getRankingsGUI().getOpened().contains(p.getUniqueId())) {
			e.setCancelled(true);	
			return;
		}
	}

	@EventHandler
	public void InventoryCloseEvent(InventoryCloseEvent e) {
		Player p = (Player) e.getPlayer();
		if (Main.getInstance().getStatsGUI().getOpened().contains(p.getUniqueId())) {
			if (p.getOpenInventory().getTopInventory().equals(Main.getInstance().getStatsGUI().getGuis().get(p.getUniqueId()))) {
				Main.getInstance().getStatsGUI().getOpened().remove(p.getUniqueId());
				return;
			}
			return;
		} else if (Main.getInstance().getRankingsGUI().getOpened().contains(p.getUniqueId())) {
			Main.getInstance().getRankingsGUI().getOpened().remove(p.getUniqueId());
			return;
		}
	}

}
