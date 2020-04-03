package me.davidml16.aparkour.events;

import me.davidml16.aparkour.api.events.ParkourStartEvent;
import me.davidml16.aparkour.data.Parkour;
import me.davidml16.aparkour.data.Profile;
import me.davidml16.aparkour.managers.ColorManager;
import me.davidml16.aparkour.utils.Sounds;
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

import java.util.ArrayList;
import java.util.List;

public class Event_PlateStart implements Listener {

	private List<Player> cooldown = new ArrayList<Player>();

	@EventHandler
	public void Plate(PlayerInteractEvent e) {
		final Player p = e.getPlayer();
		Action action = e.getAction();

		if (action == Action.PHYSICAL) {
			if (e.getClickedBlock().getType() == Material.IRON_PLATE) {

				Parkour parkour = Main.getInstance().getParkourHandler().getParkourByLocation(e.getClickedBlock().getLocation());

				if (parkour == null) return;

				e.setCancelled(true);

				if(!parkour.getCheckpointLocations().contains(e.getClickedBlock().getLocation())) {
					if (e.getClickedBlock().getLocation().equals(parkour.getStart().getLocation())) {
						if(parkour.isPermissionRequired()) {
							if (!Main.getInstance().getPlayerDataHandler().playerHasPermission(p, parkour.getPermission())) {
								if (!cooldown.contains(p)) {
									cooldown.add(p);
									p.sendMessage(ColorManager.translate(Main.getInstance().getLanguageHandler().getPrefix() + " " + parkour.getPermissionMessage()));
									Sounds.playSound(p, p.getLocation(), Sounds.MySound.NOTE_PLING, 10, 0);
									Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> cooldown.remove(p), 40);
									return;
								}
								return;
							}
						}

						if (!Main.getInstance().getTimerManager().hasPlayerTimer(p)) {
							p.sendMessage(Main.getInstance().getLanguageHandler().getMessage("Messages.Started"));
							p.setFlying(false);
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

}
