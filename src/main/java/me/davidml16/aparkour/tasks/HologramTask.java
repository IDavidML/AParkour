package me.davidml16.aparkour.tasks;

import org.bukkit.Bukkit;

import me.davidml16.aparkour.Main;

public class HologramTask {
	
	private int id;

	private Main main;
	public HologramTask(Main main) {
		this.main = main;
	}

	class Task implements Runnable {
		@Override
		public void run() {
			main.getTopHologramManager().reloadTopHolograms();
		}
	}
	
	public int getId() {
		return id;
	}

	@SuppressWarnings("deprecation")
	public void start() {
		id = Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(main, new Task(), 0L, 20);
	}
	
	public void stop() {
		Bukkit.getServer().getScheduler().cancelTask(id);
	}
	
}
