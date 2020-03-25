package me.davidml16.aparkour.events;

import me.davidml16.aparkour.api.events.ParkourStartEvent;
import me.davidml16.aparkour.data.Parkour;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.utils.RestartItemUtil;
import me.davidml16.aparkour.utils.SoundUtil;

public class Event_PlateStart implements Listener {

	@EventHandler
	public void Plate(PlayerInteractEvent e) {
		final Player p = e.getPlayer();
		Action action = e.getAction();

		if (action == Action.PHYSICAL) {
			if (e.getClickedBlock().getType() == Material.IRON_PLATE) {
				if (Main.getInstance().getParkourHandler()
						.getParkourByLocation(e.getClickedBlock().getLocation()) == null) {
					return;
				}

				Parkour parkour = Main.getInstance().getParkourHandler()
						.getParkourByLocation(e.getClickedBlock().getLocation());

				String Started = Main.getInstance().getLanguageHandler().getMessage("MESSAGES_STARTED", false);
				if (e.getClickedBlock().getLocation().equals(parkour.getStart())) {
					e.setCancelled(true);
					if (!Main.getInstance().getTimerManager().hasPlayerTimer(p)) {
						e.getPlayer().sendMessage(Started);
						e.getPlayer().setFlying(false);
						SoundUtil.playStart(p);

						if (Main.getInstance().getConfig().getBoolean("RestartItem.Enabled")) {
							Main.getInstance().getPlayerDataHandler().savePlayerInventory(p);
							RestartItemUtil.giveRestartItem(p);
						}

						Main.getInstance().getPlayerDataHandler().getData(p).setParkour(parkour);

						Main.getInstance().getTimerManager().startTimer(p, parkour);

						Bukkit.getPluginManager().callEvent(new ParkourStartEvent(p, parkour));
					}
				}
			}
		}
	}

}
