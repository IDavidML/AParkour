package me.davidml16.aparkour.events;

import me.davidml16.aparkour.api.events.ParkourCheckpointEvent;
import me.davidml16.aparkour.api.events.ParkourReturnEvent;
import me.davidml16.aparkour.data.Profile;
import me.davidml16.aparkour.utils.ParkourItems;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.data.Parkour;
import me.davidml16.aparkour.utils.SoundUtil;

public class Event_Fall implements Listener {

	@EventHandler
	public void Fall(PlayerMoveEvent e) {
		Player p = e.getPlayer();

		if (Main.getInstance().getConfig().getBoolean("ReturnOnFall.Enabled")) {

			if ((Main.getInstance().getTimerManager().hasPlayerTimer(p))
					&& (p.getFallDistance() >= Main.getInstance().getConfig().getInt("ReturnOnFall.BlocksDistance"))
					&& (!p.isFlying())) {

				Parkour parkour = Main.getInstance().getPlayerDataHandler().getData(p).getParkour();
				Profile data = Main.getInstance().getPlayerDataHandler().getData(p);

				p.setFlying(false);

				if(data.getLastCheckpoint() < 0) {
					p.teleport(parkour.getSpawn());

					p.sendMessage(Main.getInstance().getLanguageHandler().getMessage("Messages.Return"));

					data.setParkour(null);
					data.setLastCheckpoint(-1);

					Main.getInstance().getTimerManager().cancelTimer(p);
					if (Main.getInstance().isParkourItemsEnabled()) {
						Main.getInstance().getPlayerDataHandler().restorePlayerInventory(p);
					}

					Bukkit.getPluginManager().callEvent(new ParkourReturnEvent(p, parkour));
				} else if (data.getLastCheckpoint() >= 0) {

					p.teleport(data.getLastCheckpointLocation());
					p.sendMessage(Main.getInstance().getLanguageHandler().getMessage("Messages.ReturnCheckpoint")
							.replaceAll("%checkpoint%", Integer.toString(data.getLastCheckpoint() + 1)));

					Bukkit.getPluginManager().callEvent(new ParkourCheckpointEvent(p, parkour));
				}

				SoundUtil.playFall(p);

				p.setNoDamageTicks(40);
			}
		}
	}
}
