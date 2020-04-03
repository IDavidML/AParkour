package me.davidml16.aparkour.events;

import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.api.events.ParkourStartEvent;
import me.davidml16.aparkour.data.Parkour;
import me.davidml16.aparkour.data.Profile;
import me.davidml16.aparkour.managers.ColorManager;
import me.davidml16.aparkour.utils.RestartItemUtil;
import me.davidml16.aparkour.utils.SoundUtil;
import me.davidml16.aparkour.utils.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;

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
								int time = (Main.getInstance().getTimerManager().getTimer().get(p.getUniqueId()));
								p.sendMessage(Main.getInstance().getLanguageHandler().getMessage("Messages.Checkpoint")
										.replaceAll("%checkpoint%", Integer.toString(data.getLastCheckpoint() + 1))
										.replaceAll("%time%", Main.getInstance().getTimerManager().timeAsString(time))
								);
								Sounds.playSound(p, p.getLocation(), Sounds.MySound.CLICK, 10, 2);
							}
						}
					}
				}
			}
		}
	}

}
