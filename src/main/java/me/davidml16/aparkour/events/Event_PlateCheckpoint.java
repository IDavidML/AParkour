package me.davidml16.aparkour.events;

import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.api.events.ParkourCheckpointEvent;
import me.davidml16.aparkour.data.Parkour;
import me.davidml16.aparkour.data.ParkourSession;
import me.davidml16.aparkour.data.Profile;
import me.davidml16.aparkour.utils.Sounds;
import me.davidml16.aparkour.utils.TitleUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class Event_PlateCheckpoint implements Listener {

	private Main main;
	public Event_PlateCheckpoint(Main main) {
		this.main = main;
	}

	@EventHandler
	public void Plate(PlayerInteractEvent e) {
		final Player p = e.getPlayer();
		Action action = e.getAction();

		if (action == Action.PHYSICAL) {
			if (e.getClickedBlock().getType() == Material.IRON_PLATE) {

				Parkour parkour = main.getParkourHandler().getParkourByLocation(e.getClickedBlock().getLocation());

				if (parkour == null) return;

				e.setCancelled(true);

				if(parkour.getCheckpointLocations().contains(e.getClickedBlock().getLocation())) {
					if (main.getTimerManager().hasPlayerTimer(p)) {

						ParkourSession session = main.getSessionHandler().getSession(p);

						if (session.getLastCheckpoint() < session.getParkour().getCheckpoints().size() - 1) {
							if (e.getClickedBlock().getLocation().equals(session.getParkour().getCheckpoints().get(session.getLastCheckpoint() + 1).getLocation())) {
								session.setLastCheckpoint(session.getLastCheckpoint() + 1);

								Location loc = parkour.getCheckpointLocations().get(session.getLastCheckpoint()).clone();
								loc.add(0.5D, 0D, 0.5D);
								loc.setPitch(p.getLocation().getPitch());
								loc.setYaw(p.getLocation().getYaw());
								session.setLastCheckpointLocation(loc);

								int time = (main.getTimerManager().getTimer().get(p.getUniqueId()));

								String message = main.getLanguageHandler().getMessage("Messages.Checkpoint");
								if(message.length() > 0)
									p.sendMessage(message
											.replaceAll("%checkpoint%", Integer.toString(session.getLastCheckpoint() + 1))
											.replaceAll("%time%", main.getTimerManager().timeAsString(time)));

								Sounds.playSound(p, p.getLocation(), Sounds.MySound.CLICK, 10, 2);

								main.getTitleUtil().sendCheckpointTitle(p, parkour, session);

								Bukkit.getPluginManager().callEvent(new ParkourCheckpointEvent(p, parkour));
							}
						}
					}
				}
			}
		}
	}

}
