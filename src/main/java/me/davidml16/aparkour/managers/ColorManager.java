package me.davidml16.aparkour.managers;

import org.bukkit.ChatColor;

public class ColorManager {
	public static String translate(String msg) {
		return ChatColor.translateAlternateColorCodes('&', msg);
	}
}
