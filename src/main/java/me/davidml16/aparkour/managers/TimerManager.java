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

	public String millisToString(String format, long millis) {
		MillisecondConverter converter = new MillisecondConverter(millis);
		return format
				.replaceAll("%hours%", String.valueOf(converter.getHours()))
				.replaceAll("%minutes%", String.valueOf(converter.getMinutes()))
				.replaceAll("%seconds%", String.valueOf(converter.getSeconds()))
				.replaceAll("%milliseconds%", String.format("%03d", converter.getMilliseconds()));
	}

	public HashMap<String, String> getLastTimes(Player p) {
		HashMap<String, String> times = new HashMap<String, String>();
		for (String parkour : main.getParkourHandler().getParkours().keySet()) {
			long total = main.getPlayerDataHandler().getData(p).getLastTimes().get(parkour);
			times.put(parkour, millisToString(main.getLanguageHandler().getMessage("Timer.Formats.PlayerTime"), total));
		}

		return times;
	}

	public HashMap<String, String> getBestTimes(Player p) {
		HashMap<String, String> times = new HashMap<String, String>();
		for (String parkour : main.getParkourHandler().getParkours().keySet()) {
			long total = main.getPlayerDataHandler().getData(p).getBestTimes().get(parkour);
			times.put(parkour, millisToString(main.getLanguageHandler().getMessage("Timer.Formats.PlayerTime"), total));
		}

		return times;
	}

}
