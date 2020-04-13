package me.davidml16.aparkour.managers;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import me.davidml16.aparkour.data.Parkour;
import me.davidml16.aparkour.utils.MillisecondConverter;
import org.bukkit.entity.Player;

import me.davidml16.aparkour.Main;

public class TimerManager {
	
	private boolean actionBarEnabled;

	private Main main;

	public TimerManager(Main main) {
		this.main = main;
		actionBarEnabled = main.getConfig().getBoolean("ActionBarTimer.Enabled");
	}

	public boolean isActionBarEnabled() {
		return actionBarEnabled;
	}

	public boolean hasPlayerTimer(Player p) {
		return main.getSessionHandler().getSession(p) != null;
	}

	public String millisToString(long millis) {
		MillisecondConverter converter = new MillisecondConverter(millis);
		return String.format("%01dm %01ds %03dms", converter.getMinutes(), converter.getSeconds(), converter.getMilliseconds());
	}

	public String secondsToString(long millis) {
		MillisecondConverter converter = new MillisecondConverter(millis);
		return String.format("%01dm %01ds", converter.getMinutes(), converter.getSeconds());
	}

	public HashMap<String, String> getLastTimes(Player p) {
		HashMap<String, String> times = new HashMap<String, String>();
		for (Parkour parkour : main.getParkourHandler().getParkours().values()) {
			long total = main.getPlayerDataHandler().getData(p).getLastTimes().get(parkour.getId());
			times.put(parkour.getId(), millisToString(total));
		}

		return times;
	}

	public HashMap<String, String> getBestTimes(Player p) {
		HashMap<String, String> times = new HashMap<String, String>();
		for (Parkour parkour : main.getParkourHandler().getParkours().values()) {
			long total = main.getPlayerDataHandler().getData(p).getBestTimes().get(parkour.getId());
			times.put(parkour.getId(), millisToString(total));
		}

		return times;
	}

}
