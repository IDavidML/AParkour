package me.davidml16.aparkour.events;

import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.api.events.ParkourEndEvent;
import me.davidml16.aparkour.data.Parkour;
import me.davidml16.aparkour.data.Profile;
import me.davidml16.aparkour.managers.ColorManager;
import me.davidml16.aparkour.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;

public class Event_PlateEnd implements Listener {

	private List<Player> cooldown = new ArrayList<Player>();

	@EventHandler
	public void Plate(PlayerInteractEvent e) {
		final Player p = e.getPlayer();
		Action action = e.getAction();

		if (action == Action.PHYSICAL) {
			if (e.getClickedBlock().getType() == Material.GOLD_PLATE) {

				Parkour parkour = Main.getInstance().getParkourHandler().getParkourByLocation(e.getClickedBlock().getLocation());

				if (parkour == null) return;

				e.setCancelled(true);

				if (e.getClickedBlock().getLocation().equals(parkour.getEnd().getLocation())) {
					if (parkour != Main.getInstance().getPlayerDataHandler().getData(p).getParkour()) {
						return;
					}

					if (Main.getInstance().getTimerManager().hasPlayerTimer(p)) {

						Profile data = Main.getInstance().getPlayerDataHandler().getData(p);

						if(parkour.getCheckpoints().size() == 0 || data.getLastCheckpoint() == (parkour.getCheckpoints().size() - 1)) {

							int total = (Main.getInstance().getTimerManager().getTimer().get(p.getUniqueId()));

							if (Main.getInstance().isParkourItemsEnabled()) {
								Main.getInstance().getPlayerDataHandler().restorePlayerInventory(p);
							}

							Main.getInstance().getRewardHandler().giveParkourRewards(p, parkour.getId(), false);

							data.setParkour(null);
							data.setLastCheckpoint(-1);

							SoundUtil.playEnd(p);

							TitleAPI.sendEndTitle(p, parkour);

							String end = Main.getInstance().getLanguageHandler().getMessage("EndMessage.Normal");
							if(end.length() > 0)
								p.sendMessage(ChatColor.translateAlternateColorCodes('&', end)
										.replaceAll("%endTime%", Main.getInstance().getTimerManager().timeAsString(total)));

							if (data.getBestTimes().get(parkour.getId()) == 0 && data.getLastTimes().get(parkour.getId()) == 0) {
								String message = Main.getInstance().getLanguageHandler().getMessage("EndMessage.FirstTime");
								if(message.length() > 0)
									p.sendMessage(message);
								Main.getInstance().getRewardHandler().giveParkourRewards(p, parkour.getId(), true);
							}

							data.setLastTime(total, parkour.getId());
							if (data.getBestTimes().get(parkour.getId()) == 0) {
								data.setBestTime(total, parkour.getId());
							}

							Main.getInstance().getTimerManager().cancelTimer(p);

							if (Main.getInstance().getConfig().getBoolean("TpToParkourSpawn.Enabled")) {
								p.teleport(parkour.getSpawn());
							}

							if (Main.getInstance().getConfig().getBoolean("Firework.Enabled")) {
								RandomFirework.launchRandomFirework(p.getLocation());
							}

							if (data.isBestTime(total, parkour.getId())) {
								Player eplayer = e.getPlayer();
								int bestTotal = data.getBestTimes().get(parkour.getId()) - total;

								String record = Main.getInstance().getLanguageHandler().getMessage("EndMessage.Record");

								data.setBestTime(total, parkour.getId());

								if(record.length() > 0)
									eplayer.sendMessage(ChatColor.translateAlternateColorCodes('&', record)
											.replaceAll("%recordTime%", Main.getInstance().getTimerManager().timeAsString(bestTotal)));
							}

							data.save(parkour.getId());

							Main.getInstance().getStatsHologramManager().reloadStatsHologram(p, parkour.getId());

							Bukkit.getPluginManager().callEvent(new ParkourEndEvent(p, parkour));

						} else {
							if (!cooldown.contains(p)) {
								cooldown.add(p);
								String message = Main.getInstance().getLanguageHandler().getMessage("Messages.NeedCheckpoint");
								if(message.length() > 0)
									p.sendMessage(message);
								Sounds.playSound(p, p.getLocation(), Sounds.MySound.NOTE_PLING, 10, 0);
								Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> cooldown.remove(p), 40);
							}
						}

					}
				}
			}
		}
	}

}
