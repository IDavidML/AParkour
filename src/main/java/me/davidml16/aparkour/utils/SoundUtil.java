package me.davidml16.aparkour.utils;

import org.bukkit.entity.Player;

import me.davidml16.aparkour.Main;

public class SoundUtil {
	
	public static void playReturn(Player p) {
		if (Main.getInstance().getConfig().getBoolean("Sounds.Return.Enabled")) {
			String sound = Main.getInstance().getConfig().getString("Sounds.Return.Sound");
			float Volume = Main.getInstance().getConfig().getInt("Sounds.Return.Volume");
			float Pitch = Main.getInstance().getConfig().getInt("Sounds.Return.Pitch");
			try {
				Sounds.playSound(p, p.getLocation(), Sounds.MySound.valueOf(sound.toUpperCase()), Volume, Pitch);
			} catch (Exception exception) {
			}
		}
	}
	
	public static void playFall(Player p) {
		if (Main.getInstance().getConfig().getBoolean("Sounds.Fall.Enabled")) {
			String sound = Main.getInstance().getConfig().getString("Sounds.Fall.Sound");
			float Volume = Main.getInstance().getConfig().getInt("Sounds.Fall.Volume");
			float Pitch = Main.getInstance().getConfig().getInt("Sounds.Fall.Pitch");
			try {
				Sounds.playSound(p, p.getLocation(), Sounds.MySound.valueOf(sound.toUpperCase()), Volume, Pitch);
			} catch (Exception localException) {
			}
		}
	}
	

	public static void playFly(Player p) {
		if (Main.getInstance().getConfig().getBoolean("Sounds.Fly.Enabled")) {
			String sound = Main.getInstance().getConfig().getString("Sounds.Fly.Sound");
			float Volume = Main.getInstance().getConfig().getInt("Sounds.Fly.Volume");
			float Pitch = Main.getInstance().getConfig().getInt("Sounds.Fly.Pitch");
			try {
				Sounds.playSound(p, p.getLocation(), Sounds.MySound.valueOf(sound.toUpperCase()), Volume, Pitch);
			} catch (Exception localException) {
			}
		}
	}
	
	public static void playStart(Player p) {
		if (Main.getInstance().getConfig().getBoolean("Sounds.ParkourStart.Enabled")) {
			String sound = Main.getInstance().getConfig().getString("Sounds.ParkourStart.Sound");
			float Volume = Main.getInstance().getConfig().getInt("Sounds.ParkourStart.Volume");
			float Pitch = Main.getInstance().getConfig().getInt("Sounds.ParkourStart.Pitch");
			try {
				Sounds.playSound(p, p.getLocation(), Sounds.MySound.valueOf(sound.toUpperCase()), Volume, Pitch);
			} catch (Exception localException) {
			}
		}
	}

	public static void playEnd(Player p) {
		if (Main.getInstance().getConfig().getBoolean("Sounds.ParkourEnd.Enabled")) {
			String sound = Main.getInstance().getConfig().getString("Sounds.ParkourEnd.Sound");
			float Volume = Main.getInstance().getConfig().getInt("Sounds.ParkourEnd.Volume");
			float Pitch = Main.getInstance().getConfig().getInt("Sounds.ParkourEnd.Pitch");
			try {
				Sounds.playSound(p, p.getLocation(), Sounds.MySound.valueOf(sound.toUpperCase()), Volume, Pitch);
			} catch (Exception localException) {
			}
		}
	}

}
