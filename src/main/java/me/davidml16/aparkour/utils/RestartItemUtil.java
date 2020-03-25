package me.davidml16.aparkour.utils;

import java.util.ArrayList;

import me.davidml16.aparkour.managers.ColorManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.davidml16.aparkour.Main;

public class RestartItemUtil {

	private static ItemStack returnItem;

	public static void loadReturnItem() {
		int id = Main.getInstance().getConfig().getInt("RestartItem.ID");
		String name = ColorManager.translate(Main.getInstance().getConfig().getString("RestartItem.Name"));
		String lore = ColorManager.translate(Main.getInstance().getConfig().getString("RestartItem.Lore"));
		returnItem = new ItemBuilder(Material.getMaterial(id), 1).setName(name).setLore(lore).toItemStack();
	}
	
	public static ItemStack getRestartItem() {

		return returnItem;
	}

	public static void giveRestartItem(Player p) {
		p.getInventory().setItem(4, returnItem);
	}
}
