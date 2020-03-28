package me.davidml16.aparkour.events;

import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.api.events.ParkourEndEvent;
import me.davidml16.aparkour.data.Parkour;
import me.davidml16.aparkour.data.Profile;
import me.davidml16.aparkour.utils.RandomFirework;
import me.davidml16.aparkour.utils.SoundUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class Event_PlateEnd implements Listener {

	@EventHandler
	public void Plate(PlayerInteractEvent e) {
		final Player p = e.getPlayer();
		Action action = e.getAction();

		if (action == Action.PHYSICAL) {
			if (e.getClickedBlock().getType() == Material.GOLD_PLATE) {
				if (Main.getInstance().getParkourHandler()
						.getParkourByLocation(e.getClickedBlock().getLocation()) == null) {
					return;
				}

				Parkour parkour = Main.getInstance().getParkourHandler()
						.getParkourByLocation(e.getClickedBlock().getLocation());
				if (e.getClickedBlock().getLocation().equals(parkour.getEnd())) {
					e.setCancelled(true);

					if (parkour != Main.getInstance().getPlayerDataHandler().getData(p).getParkour()) {
						return;
					}

					if (Main.getInstance().getTimerManager().hasPlayerTimer(p)) {
						int total = (Main.getInstance().getTimerManager().getTimer().get(p.getUniqueId()));

						if (Main.getInstance().getConfig().getBoolean("RestartItem.Enabled")) {
							Main.getInstance().getPlayerDataHandler().restorePlayerInventory(p);
						}

						Main.getInstance().getRewardHandler().giveParkourRewards(p, parkour.getId(), false);

						Main.getInstance().getPlayerDataHandler().getData(p).setParkour(null);

						SoundUtil.playEnd(p);

						Profile data = Main.getInstance().getPlayerDataHandler().getData(p);

						String End = Main.getInstance().getLanguageHandler().getMessage("ENDMESSAGE_NORMAL", false);
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', End).replaceAll("%endTime%",
								Main.getInstance().getTimerManager().timeAsString(total)));

						if (data.getBestTimes().get(parkour.getId()) == 0
								&& data.getLastTimes().get(parkour.getId()) == 0) {
							Main.getInstance().getLanguageHandler().sendMessage(p, "ENDMESSAGE_FIRSTTIME", false);
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

							String Record = Main.getInstance().getLanguageHandler().getMessage("ENDMESSAGE_RECORD",
									false);

							data.setBestTime(total, parkour.getId());

							eplayer.sendMessage(ChatColor.translateAlternateColorCodes('&', Record).replaceAll(
									"%recordTime%", Main.getInstance().getTimerManager().timeAsString(bestTotal)));
						}

						data.save();

						Main.getInstance().getStatsHologramManager().reloadStatsHologram(p, parkour.getId());

						Bukkit.getPluginManager().callEvent(new ParkourEndEvent(p, parkour));
					}
				}
			}
		}
	}

}
