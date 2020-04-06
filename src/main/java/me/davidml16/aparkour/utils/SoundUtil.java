package me.davidml16.aparkour.utils;

import org.bukkit.entity.Player;

import me.davidml16.aparkour.Main;

public class SoundUtil {

	private Main main;
	public SoundUtil(Main main) {
		this.main = main;
	}
	
	public void playReturn(Player p) {
		if (main.getConfig().getBoolean("Sounds.Return.Enabled")) {
			String sound = main.getConfig().getString("Sounds.Return.Sound");
			float Volume = main.getConfig().getInt("Sounds.Return.Volume");
			float Pitch = main.getConfig().getInt("Sounds.Return.Pitch");
			Sounds.playSound(p, p.getLocation(), Sounds.MySound.valueOf(sound.toUpperCase()), Volume, Pitch);
		}
	}
	
	public void playFall(Player p) {
		if (main.getConfig().getBoolean("Sounds.Fall.Enabled")) {
			String sound = main.getConfig().getString("Sounds.Fall.Sound");
			float Volume = main.getConfig().getInt("Sounds.Fall.Volume");
			float Pitch = main.getConfig().getInt("Sounds.Fall.Pitch");
			Sounds.playSound(p, p.getLocation(), Sounds.MySound.valueOf(sound.toUpperCase()), Volume, Pitch);
		}
	}
	

	public void playFly(Player p) {
		if (main.getConfig().getBoolean("Sounds.Fly.Enabled")) {
			String sound = main.getConfig().getString("Sounds.Fly.Sound");
			float Volume = main.getConfig().getInt("Sounds.Fly.Volume");
			float Pitch = main.getConfig().getInt("Sounds.Fly.Pitch");
			Sounds.playSound(p, p.getLocation(), Sounds.MySound.valueOf(sound.toUpperCase()), Volume, Pitch);
		}
	}
	
	public void playStart(Player p) {
		if (main.getConfig().getBoolean("Sounds.ParkourStart.Enabled")) {
			String sound = main.getConfig().getString("Sounds.ParkourStart.Sound");
			float Volume = main.getConfig().getInt("Sounds.ParkourStart.Volume");
			float Pitch = main.getConfig().getInt("Sounds.ParkourStart.Pitch");
			Sounds.playSound(p, p.getLocation(), Sounds.MySound.valueOf(sound.toUpperCase()), Volume, Pitch);
		}
	}

	public void playEnd(Player p) {
		if (main.getConfig().getBoolean("Sounds.ParkourEnd.Enabled")) {
			String sound = main.getConfig().getString("Sounds.ParkourEnd.Sound");
			float Volume = main.getConfig().getInt("Sounds.ParkourEnd.Volume");
			float Pitch = main.getConfig().getInt("Sounds.ParkourEnd.Pitch");
			Sounds.playSound(p, p.getLocation(), Sounds.MySound.valueOf(sound.toUpperCase()), Volume, Pitch);
		}
	}

}
