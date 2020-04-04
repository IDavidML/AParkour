package me.davidml16.aparkour.events;

import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.data.Parkour;
import me.davidml16.aparkour.data.Profile;
import me.davidml16.aparkour.utils.Sounds;
import me.davidml16.aparkour.utils.TitleAPI;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class Event_PlateCheckpoint implements Listener {

	@EventHandler
	public void Plate(PlayerInteractEvent e) {
		final Player p = e.getPlayer();
		Action action = e.getAction();

		if (action == Action.PHYSICAL) {
			if (e.getClickedBlock().getType() == Material.IRON_PLATE) {

				Parkour parkour = Main.getInstance().getParkourHandler().getParkourByLocation(e.getClickedBlock().getLocation());

				if (parkour == null) return;

				e.setCancelled(true);

				if(parkour.getCheckpointLocations().contains(e.getClickedBlock().getLocation())) {
					if (Main.getInstance().getTimerManager().hasPlayerTimer(p)) {
						Profile data = Main.getInstance().getPlayerDataHandler().getData(p);
						if (data.getLastCheckpoint() < data.getParkour().getCheckpoints().size() - 1) {
							if (e.getClickedBlock().getLocation().equals(data.getParkour().getCheckpoints().get(data.getLastCheckpoint() + 1).getLocation())) {
								data.setLastCheckpoint(data.getLastCheckpoint() + 1);

								Location loc = parkour.getCheckpointLocations().get(data.getLastCheckpoint()).clone();
								loc.add(0.5D, 0D, 0.5D);
								loc.setPitch(p.getLocation().getPitch());
								loc.setYaw(p.getLocation().getYaw());
								data.setLastCheckpointLocation(loc);

								int time = (Main.getInstance().getTimerManager().getTimer().get(p.getUniqueId()));

								String message = Main.getInstance().getLanguageHandler().getMessage("Messages.Checkpoint");
								if(message.length() > 0)
									p.sendMessage(message
											.replaceAll("%checkpoint%", Integer.toString(data.getLastCheckpoint() + 1))
											.replaceAll("%time%", Main.getInstance().getTimerManager().timeAsString(time)));

								Sounds.playSound(p, p.getLocation(), Sounds.MySound.CLICK, 10, 2);

								TitleAPI.sendCheckpointTitle(p, parkour, data);
							}
						}
					}
				}
			}
		}
	}

}
