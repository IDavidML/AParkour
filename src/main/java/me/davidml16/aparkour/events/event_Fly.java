package me.davidml16.aparkour.events;

import me.davidml16.aparkour.api.events.ParkourReturnEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.data.Parkour;
import me.davidml16.aparkour.utils.SoundUtil;

public class event_Fly implements Listener {

	@EventHandler
	public void Fly(PlayerToggleFlightEvent e) {
		Player p = e.getPlayer();

		if (Main.getInstance().getConfig().getBoolean("ReturnOnFly.Enabled")) {

			String fly = Main.getInstance().getLanguageHandler().getMessage("MESSAGES_FLY", false);
			if (Main.getInstance().getTimerManager().hasPlayerTimer(p)) {
				p.sendMessage(fly);

				Main.getInstance().getTimerManager().cancelTimer(p);

				Parkour parkour = Main.getInstance().getParkourHandler().getParkourByPlayer(p);
				parkour.getPlayers().remove(p.getUniqueId());

				p.setFlying(false);
				p.teleport(parkour.getSpawn());

				if (Main.getInstance().getConfig().getBoolean("RestartItem.Enabled")) {
					Main.getInstance().getPlayerDataHandler().restorePlayerInventory(p);
				}

				SoundUtil.playFly(p);

				Bukkit.getPluginManager().callEvent(new ParkourReturnEvent(p, parkour));

				p.setNoDamageTicks(20);
			}
		}
	}
}
