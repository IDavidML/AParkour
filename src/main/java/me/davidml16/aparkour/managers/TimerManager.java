package me.davidml16.aparkour.managers;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.data.ParkourData;
import me.davidml16.aparkour.utils.ActionBar;
import me.davidml16.aparkour.utils.SoundUtil;

public class TimerManager {

	private HashMap<UUID, Integer> timer;
	private HashMap<UUID, BukkitRunnable> timerTask;
	
	private boolean actionBarEnabled;

	public TimerManager() {
		actionBarEnabled = Main.getInstance().getConfig().getBoolean("ActionBarTimer.Enabled");
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
	
	public void startTimer(Player p, ParkourData parkour) {
		if(!hasPlayerTimer(p)) {
			Main.getInstance().getTimerManager().getTimer().put(p.getUniqueId(), 0);
			if (Main.getInstance().getTimerManager().isActionBarEnabled()) {
				Main.getInstance().getTimerManager().sendTimer(p);
			}
			
			Main.getInstance().getTimerManager().getTimerTask().put(p.getUniqueId(), new BukkitRunnable() {
				public void run() {
					Main.getInstance().getTimerManager().getTimer().put(p.getUniqueId(), Main.getInstance().getTimerManager().getTimer().get(p.getUniqueId()) + 1);
					if (Main.getInstance().getTimerManager().getTimer().get(p.getUniqueId()) < 3600) {
						if (Main.getInstance().getConfig().getBoolean("ActionBarTimer.Enabled")) {
							Main.getInstance().getTimerManager().sendTimer(p);
						}
					} else if (Main.getInstance().getTimerManager().getTimer().get(p.getUniqueId()) >= 3600) {
						cancelTimer(p);
	
						p.setFlying(false);
						p.teleport(parkour.getSpawn());

						SoundUtil.playReturn(p);
	
						p.setNoDamageTicks(20);
					}
				}
			});
			timerTask.get(p.getUniqueId()).runTaskTimerAsynchronously(Main.getInstance(), 20L, 20L);
		}
	}

	public void cancelTimer(Player p) {
		if (hasPlayerTimer(p)) {
			timer.put(p.getUniqueId(), 0);
			timer.remove(p.getUniqueId());
			timerTask.get(p.getUniqueId()).cancel();
			timerTask.remove(p.getUniqueId());
		}
	}

	public void sendTimer(Player p) {
		int secs = timer.get(p.getUniqueId());
		int total = Main.getInstance().getPlayerDataHandler().getData(p).getBestTimes()
				.get(Main.getInstance().getParkourHandler().getParkourByPlayer(p).getId());

		String Message = Main.getInstance().getLanguageHandler().getMessage("TIMER_ACTIONBAR", false);
		String NoBestTime = Main.getInstance().getLanguageHandler().getMessage("TIMES_NOBESTTIME", false);
		if (total != 0) {
			ActionBar.sendActionbar(p, Message.replaceAll("%currentTime%", timeAsString(secs)).replaceAll("%bestTime%",
					timeAsString(total)));
		} else {
			ActionBar.sendActionbar(p,
					Message.replaceAll("%currentTime%", timeAsString(secs)).replaceAll("%bestTime%", NoBestTime));
		}
	}

	public String timeAsString(int total) {
		String Hours = Main.getInstance().getLanguageHandler().getMessage("TIMES_HOURS", false);
		String Hour = Main.getInstance().getLanguageHandler().getMessage("TIMES_HOUR", false);
		String Minutes = Main.getInstance().getLanguageHandler().getMessage("TIMES_MINUTES", false);
		String Minute = Main.getInstance().getLanguageHandler().getMessage("TIMES_MINUTE", false);
		String Seconds = Main.getInstance().getLanguageHandler().getMessage("TIMES_SECONDS", false);
		String Second = Main.getInstance().getLanguageHandler().getMessage("TIMES_SECOND", false);

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
		for (ParkourData parkour : Main.getInstance().getParkourHandler().getParkours().values()) {
			int total = Main.getInstance().getPlayerDataHandler().getData(p).getLastTimes().get(parkour.getId());
			times.put(parkour.getId(), timeAsString(total));
		}

		return times;
	}

	public HashMap<String, String> getBestTimes(Player p) {
		HashMap<String, String> times = new HashMap<String, String>();
		for (ParkourData parkour : Main.getInstance().getParkourHandler().getParkours().values()) {
			int total = Main.getInstance().getPlayerDataHandler().getData(p).getBestTimes().get(parkour.getId());
			times.put(parkour.getId(), timeAsString(total));
		}

		return times;
	}

}
