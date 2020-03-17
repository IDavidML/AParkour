package me.davidml16.aparkour.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.data.ParkourData;
import me.davidml16.aparkour.utils.ActionBar;
import me.davidml16.aparkour.utils.SoundUtil;

public class event_Fall implements Listener {

	@EventHandler
	public void Fall(PlayerMoveEvent e) {
		Player p = e.getPlayer();

		if (Main.getInstance().getConfig().getBoolean("ReturnOnFall.Enabled")) {

			String Return = Main.getInstance().getLanguageHandler().getMessage("MESSAGES_RETURN", false);
			if ((Main.getInstance().getTimerManager().hasPlayerTimer(p))
					&& (p.getFallDistance() >= Main.getInstance().getConfig().getInt("ReturnOnFall.BlocksDistance"))
					&& (!p.isFlying())) {
				p.sendMessage(Return);

				Main.getInstance().getTimerManager().cancelTimer(p);

				ParkourData parkour = Main.getInstance().getParkourHandler().getParkourByPlayer(p);
				parkour.getPlayers().remove(p.getUniqueId());

				p.setFlying(false);
				p.teleport(parkour.getSpawn());

				if (Main.getInstance().getConfig().getBoolean("RestartItem.Enabled")) {
					Main.getInstance().getPlayerDataHandler().restorePlayerInventory(p);
				}

				SoundUtil.playFall(p);

				p.setNoDamageTicks(40);
			}
		}
	}
}
