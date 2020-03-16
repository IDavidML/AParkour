package me.davidml16.aparkour.tasks;

import org.bukkit.Bukkit;

import me.davidml16.aparkour.Main;

public class HologramTask {
	
	private int id;

	class Task implements Runnable {
		@Override
		public void run() {
			Main.getInstance().getTopHologramManager().reloadTopHolograms();
		}
	}
	
	public int getId() {
		return id;
	}

	@SuppressWarnings("deprecation")
	public void start() {
		id = Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(Main.getInstance(), new Task(), 0L, 20 * 1);
	}
	
	public void stop() {
		Bukkit.getServer().getScheduler().cancelTask(id);
	}
	
}
