package me.davidml16.aparkour.events;

import me.davidml16.aparkour.api.events.ParkourReturnEvent;
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

				p.setFlying(false);
				p.teleport(parkour.getSpawn());

				if(Main.getInstance().getParkourHandler().isKickFromParkourOnFail()) {
					Main.getInstance().getPlayerDataHandler().getData(p).setParkour(null);
					p.sendMessage(Main.getInstance().getLanguageHandler().getMessage("MESSAGES_RETURN", false));
					Main.getInstance().getTimerManager().cancelTimer(p);
					if (Main.getInstance().getConfig().getBoolean("RestartItem.Enabled")) {
						Main.getInstance().getPlayerDataHandler().restorePlayerInventory(p);
					}
				}

				SoundUtil.playFall(p);

				Bukkit.getPluginManager().callEvent(new ParkourReturnEvent(p, parkour));

				p.setNoDamageTicks(40);
			}
		}
	}
}
