package me.davidml16.aparkour.managers;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import me.davidml16.aparkour.data.Parkour;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.utils.ActionBar;
import me.davidml16.aparkour.utils.SoundUtil;

public class TimerManager {

	private HashMap<UUID, Integer> timer;
	private HashMap<UUID, BukkitRunnable> timerTask;
	
	private boolean actionBarEnabled;

	private Main main;

	public TimerManager(Main main) {
		this.main = main;
		actionBarEnabled = main.getConfig().getBoolean("ActionBarTimer.Enabled");
		timer = new HashMap<UUID, Integer>();
		timerTask = new HashMap<UUID, BukkitRunnable>();
	}

	public HashMap<UUID, Integer> getTimer() {
		return timer;
	}

	public HashMap<UUID, BukkitRunnable> getTimerTask() {
		return timerTask;
	}
	
	public boolean isActionBarEnabled() {
		return actionBarEnabled;
	}

	public boolean hasPlayerTimer(Player p) {
		return timer.containsKey(p.getUniqueId());
	}
	
	public void startTimer(Player p, Parkour parkour) {
		if(!hasPlayerTimer(p)) {
			main.getTimerManager().getTimer().put(p.getUniqueId(), 0);
			if (main.getTimerManager().isActionBarEnabled()) {
				main.getTimerManager().sendTimer(p);
			}
			
			main.getTimerManager().getTimerTask().put(p.getUniqueId(), new BukkitRunnable() {
				public void run() {
					main.getTimerManager().getTimer().put(p.getUniqueId(), main.getTimerManager().getTimer().get(p.getUniqueId()) + 1);
					if (main.getTimerManager().getTimer().get(p.getUniqueId()) < 3600) {
						if (main.getConfig().getBoolean("ActionBarTimer.Enabled")) {
							main.getTimerManager().sendTimer(p);
						}
					} else if (main.getTimerManager().getTimer().get(p.getUniqueId()) >= 3600) {
						cancelTimer(p);
	
						p.setFlying(false);
						p.teleport(parkour.getSpawn());

						main.getSoundUtil().playReturn(p);
	
						p.setNoDamageTicks(20);
					}
				}
			});
			timerTask.get(p.getUniqueId()).runTaskTimerAsynchronously(main, 20L, 20L);
		}
	}

	public void cancelTimer(Player p) {
		if (hasPlayerTimer(p)) {
			timer.remove(p.getUniqueId());
			timer.remove(p.getUniqueId());
			timerTask.get(p.getUniqueId()).cancel();
			timerTask.remove(p.getUniqueId());
		}
	}

	private void sendTimer(Player p) {
		if (hasPlayerTimer(p)) {
			int secs = timer.get(p.getUniqueId());
			int total = main.getPlayerDataHandler().getData(p).getBestTimes()
					.get(main.getPlayerDataHandler().getData(p).getParkour().getId());

			String message = main.getLanguageHandler().getMessage("Timer.ActionBar");
			if(message.length() > 0) {
				String NoBestTime = main.getLanguageHandler().getMessage("Times.NoBestTime");
				if (total != 0) {
					ActionBar.sendActionbar(p, message.replaceAll("%currentTime%", timeAsString(secs)).replaceAll("%bestTime%",
							timeAsString(total)));
				} else {
					ActionBar.sendActionbar(p,
							message.replaceAll("%currentTime%", timeAsString(secs)).replaceAll("%bestTime%", NoBestTime));
				}
			}
		}
	}

	public String timeAsString(int total) {
		String Hours = main.getLanguageHandler().getMessage("Times.Hours");
		String Hour = main.getLanguageHandler().getMessage("Times.Hour");
		String Minutes = main.getLanguageHandler().getMessage("Times.Minutes");
		String Minute = main.getLanguageHandler().getMessage("Times.Minute");
		String Seconds = main.getLanguageHandler().getMessage("Times.Seconds");
		String Second = main.getLanguageHandler().getMessage("Times.Second");

		long millis = total * 1000;
		String output = "";
		long days = TimeUnit.MILLISECONDS.toDays(millis);
		millis -= TimeUnit.DAYS.toMillis(days);
		long hours = TimeUnit.MILLISECONDS.toHours(millis);
		millis -= TimeUnit.HOURS.toMillis(hours);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
		millis -= TimeUnit.MINUTES.toMillis(minutes);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
		if (days > 1L) {
			output = output + days + " days, ";
		} else if (days == 1L) {
			output = output + days + " day, ";
		}
		if (hours > 1L) {
			output = output + hours + " " + Hours + ", ";
		} else if (hours == 1L) {
			output = output + hours + " " + Hour + ", ";
		}
		if (minutes > 1L) {
			output = output + minutes + " " + Minutes + ", ";
		} else if (minutes == 1L) {
			output = output + minutes + " " + Minute + ", ";
		}
		if (seconds > 1L) {
			output = output + seconds + " " + Seconds;
		} else if (seconds == 1L) {
			output = output + seconds + " " + Second;
		} else if (seconds == 0L) {
			output = output + "0 " + Seconds;
		}
		return output;
	}

	public HashMap<String, String> getLastTimes(Player p) {
		HashMap<String, String> times = new HashMap<String, String>();
		for (Parkour parkour : main.getParkourHandler().getParkours().values()) {
			int total = main.getPlayerDataHandler().getData(p).getLastTimes().get(parkour.getId());
			times.put(parkour.getId(), timeAsString(total));
		}

		return times;
	}

	public HashMap<String, String> getBestTimes(Player p) {
		HashMap<String, String> times = new HashMap<String, String>();
		for (Parkour parkour : main.getParkourHandler().getParkours().values()) {
			int total = main.getPlayerDataHandler().getData(p).getBestTimes().get(parkour.getId());
			times.put(parkour.getId(), timeAsString(total));
		}

		return times;
	}

}
