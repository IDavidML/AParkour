package me.davidml16.aparkour.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.data.ParkourData;
import me.davidml16.aparkour.utils.ActionBar;
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

				ParkourData parkour = Main.getInstance().getParkourHandler().getParkourByPlayer(p);
				parkour.getPlayers().remove(p.getUniqueId());

				p.setFlying(false);
				p.teleport(parkour.getSpawn());
				if (Main.getInstance().getConfig().getBoolean("RestartItem.Enabled")) {
					Main.getInstance().getPlayerDataHandler().restorePlayerInventory(p);
				}
				if (Main.getInstance().getTimerManager().isActionBarEnabled()) {
					ActionBar.sendActionbar(p, " ");
				}
				SoundUtil.playFly(p);

				p.setNoDamageTicks(20);
			}
		}
	}
}
