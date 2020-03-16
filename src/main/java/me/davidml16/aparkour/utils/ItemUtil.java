package me.davidml16.aparkour.utils;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.davidml16.aparkour.Main;

public class ItemUtil {
	
	public static ItemStack getRestartItem() {
		int id = Main.getInstance().getConfig().getInt("RestartItem.ID");
		@SuppressWarnings("deprecation")
		ItemStack restart = new ItemStack(Material.getMaterial(id), 1);
		restart.setAmount(1);
		ItemMeta restartmeta = restart.getItemMeta();
		restart.setItemMeta(restartmeta);
		String name = Main.getInstance().getConfig().getString("RestartItem.Name").replaceAll("&", "§");
		String loreitem = Main.getInstance().getConfig().getString("RestartItem.Lore").replaceAll("&", "§");
		restartmeta.setDisplayName(name);
		ArrayList<String> lore = new ArrayList<String>();
		lore.add(loreitem);
		restartmeta.setLore(lore);
		restart.setItemMeta(restartmeta);
		return restart;
	}

	public static void giveRestartItem(Player p) {
		p.getInventory().setItem(4, getRestartItem());
	}
}
