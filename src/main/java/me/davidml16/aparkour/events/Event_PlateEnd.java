package me.davidml16.aparkour.events;

import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.api.events.ParkourEndEvent;
import me.davidml16.aparkour.data.Parkour;
import me.davidml16.aparkour.data.ParkourSession;
import me.davidml16.aparkour.data.Profile;
import me.davidml16.aparkour.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.ArrayList;
import java.util.List;

public class Event_PlateEnd implements Listener {

	private Main main;
	public Event_PlateEnd(Main main) {
		this.main = main;
	}

	private List<Player> cooldown = new ArrayList<Player>();

	@EventHandler
	public void Plate(PlayerInteractEvent e) {
		final Player p = e.getPlayer();
		Action action = e.getAction();

		if (action == Action.PHYSICAL) {
			if (e.getClickedBlock().getType() == Material.GOLD_PLATE) {

				Parkour parkour = main.getParkourHandler().getParkourByLocation(e.getClickedBlock().getLocation());

				if (parkour == null) return;

				e.setCancelled(true);

				ParkourSession session = main.getSessionHandler().getSession(p);

				if (e.getClickedBlock().getLocation().equals(parkour.getEnd().getLocation())) {
					if (session == null) return;
					if (parkour != session.getParkour()) return;

					if (main.getTimerManager().hasPlayerTimer(p)) {

						if(parkour.getCheckpoints().size() == 0 || session.getLastCheckpoint() == (parkour.getCheckpoints().size() - 1)) {

							Profile profile = main.getPlayerDataHandler().getData(p);

							int total = (main.getTimerManager().getTimer().get(p.getUniqueId()));

							main.getSoundUtil().playEnd(p);

							main.getTitleUtil().sendEndTitle(p, parkour);

							String end = main.getLanguageHandler().getMessage("EndMessage.Normal");
							if(end.length() > 0)
								p.sendMessage(ChatColor.translateAlternateColorCodes('&', end)
										.replaceAll("%endTime%", main.getTimerManager().timeAsString(total)));

							if (profile.getBestTimes().get(parkour.getId()) == 0 && profile.getLastTimes().get(parkour.getId()) == 0) {
								String message = main.getLanguageHandler().getMessage("EndMessage.FirstTime");
								if(message.length() > 0)
									p.sendMessage(message);
								main.getRewardHandler().giveParkourRewards(p, parkour.getId(), true);
							}

							profile.setLastTime(total, parkour.getId());
							if (profile.getBestTimes().get(parkour.getId()) == 0) {
								profile.setBestTime(total, parkour.getId());
							}

							main.getParkourHandler().resetPlayer(p);

							main.getRewardHandler().giveParkourRewards(p, parkour.getId(), false);

							if (main.getConfig().getBoolean("TpToParkourSpawn.Enabled")) {
								p.teleport(parkour.getSpawn());
							}

							if (main.getConfig().getBoolean("Firework.Enabled")) {
								RandomFirework.launchRandomFirework(p.getLocation());
							}

							if (profile.isBestTime(total, parkour.getId())) {
								Player eplayer = e.getPlayer();
								int bestTotal = profile.getBestTimes().get(parkour.getId()) - total;

								String record = main.getLanguageHandler().getMessage("EndMessage.Record");

								profile.setBestTime(total, parkour.getId());

								if(record.length() > 0)
									eplayer.sendMessage(ChatColor.translateAlternateColorCodes('&', record)
											.replaceAll("%recordTime%", main.getTimerManager().timeAsString(bestTotal)));
							}

							profile.save(parkour.getId());

							main.getStatsHologramManager().reloadStatsHologram(p, parkour.getId());

							Bukkit.getPluginManager().callEvent(new ParkourEndEvent(p, parkour));

						} else {
							if (!cooldown.contains(p)) {
								cooldown.add(p);
								String message = main.getLanguageHandler().getMessage("Messages.NeedCheckpoint");
								if(message.length() > 0)
									p.sendMessage(message);
								Sounds.playSound(p, p.getLocation(), Sounds.MySound.NOTE_PLING, 10, 0);
								Bukkit.getScheduler().runTaskLaterAsynchronously(main, () -> cooldown.remove(p), 40);
							}
						}

					}
				}
			}
		}
	}

}
