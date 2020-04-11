package me.davidml16.aparkour.events;

import me.davidml16.aparkour.api.events.ParkourCheckpointEvent;
import me.davidml16.aparkour.api.events.ParkourReturnEvent;
import me.davidml16.aparkour.data.ParkourSession;
import me.davidml16.aparkour.data.Profile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.data.Parkour;
import org.bukkit.event.player.PlayerTeleportEvent;

public class Event_Fall implements Listener {

	private Main main;
	public Event_Fall(Main main) {
		this.main = main;
	}

	@EventHandler
	public void Fall(PlayerMoveEvent e) {
		Player p = e.getPlayer();

		if (main.getConfig().getBoolean("ReturnOnFall.Enabled")) {

			if ((main.getTimerManager().hasPlayerTimer(p))
					&& (p.getFallDistance() >= main.getConfig().getInt("ReturnOnFall.BlocksDistance"))
					&& (!p.isFlying())) {

				ParkourSession session = main.getSessionHandler().getSession(p);

				if(session.getLastCheckpoint() < 0) {
					p.teleport(session.getParkour().getSpawn());

					String message = main.getLanguageHandler().getMessage("Messages.Return");
					if(message.length() > 0)
						p.sendMessage(message);

					main.getParkourHandler().resetPlayer(p);

					Bukkit.getPluginManager().callEvent(new ParkourReturnEvent(p, session.getParkour()));
				} else if (session.getLastCheckpoint() >= 0) {
					p.teleport(session.getLastCheckpointLocation());

					String message = main.getLanguageHandler().getMessage("Messages.ReturnCheckpoint");
					if(message.length() > 0)
						p.sendMessage(message.replaceAll("%checkpoint%", Integer.toString(session.getLastCheckpoint() + 1)));

					Bukkit.getPluginManager().callEvent(new ParkourCheckpointEvent(p, session.getParkour()));
				}

				main.getSoundUtil().playFall(p);

				p.setNoDamageTicks(40);
			}
		}
	}
}
