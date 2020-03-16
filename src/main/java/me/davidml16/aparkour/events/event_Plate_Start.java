package me.davidml16.aparkour.events;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.data.ParkourData;
import me.davidml16.aparkour.utils.ItemUtil;
import me.davidml16.aparkour.utils.SoundUtil;

public class event_Plate_Start implements Listener {

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

				ParkourData parkour = Main.getInstance().getParkourHandler()
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
							ItemUtil.giveRestartItem(p);
						}

						parkour.getPlayers().add(p.getUniqueId());

						Main.getInstance().getTimerManager().startTimer(p, parkour);
					}
				}
			}
		}
	}

}
