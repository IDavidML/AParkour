package me.davidml16.aparkour.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.data.ParkourData;

public class playerStats_GUI {

	private List<UUID> opened;
	private HashMap<UUID, Inventory> guis;
	private List<Integer> borders;

	public playerStats_GUI() {
		this.opened = new ArrayList<UUID>();
		this.guis = new HashMap<UUID, Inventory>();
		this.borders = Arrays.asList(0, 1, 2, 3, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44);
	}

	public List<UUID> getOpened() {
		return opened;
	}

	public HashMap<UUID, Inventory> getGuis() {
		return guis;
	}

	public void open(Player p) {
		p.updateInventory();

		Inventory gui = Bukkit.createInventory(null, 45, Main.getInstance().getLanguageHandler().getMessage("GUI_STATS_TITLE", false));

		ItemStack edge = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);
		ItemMeta edgeM = edge.getItemMeta();
		edgeM.setDisplayName(" ");
		edge.setItemMeta(edgeM);

		ItemStack book = new ItemStack(Material.BOOK, 1);
		ItemMeta bookM = book.getItemMeta();
		bookM.setDisplayName("&a&l" + p.getName() + "'s statistics");
		book.setItemMeta(bookM);

		for (Integer i : borders) {
			gui.setItem(i, edge);
		}

		gui.setItem(4, book);

		for (ParkourData parkour : Main.getInstance().getParkourHandler().getParkours().values()) {
			ItemStack stats = new ItemStack(Material.ITEM_FRAME, 1);
			ItemMeta statsM = stats.getItemMeta();
			statsM.setDisplayName("&e");

			List<String> lore = new ArrayList<String>();
			lore.add("  &eParkour: &a" + parkour.getName() + "  ");
			lore.add(" ");
			
			if(Main.getInstance().getPlayerDataHandler().getData(p).getLastTimes().get(parkour.getId()) > 0)
				lore.add("  &eLast Time: &6" + Main.getInstance().getTimerManager().timeAsString(Main.getInstance().getPlayerDataHandler().getData(p).getLastTimes().get(parkour.getId())) + "  ");
			else
				lore.add("  &eLast Time: &cN/A  ");
			
			lore.add(" ");
			
			if(Main.getInstance().getPlayerDataHandler().getData(p).getBestTimes().get(parkour.getId()) > 0)
				lore.add("  &eBest Time: &6" + Main.getInstance().getTimerManager().timeAsString(Main.getInstance().getPlayerDataHandler().getData(p).getBestTimes().get(parkour.getId())) + "  ");
			else
				lore.add("  &eBest Time: &cN/A  ");
			
			lore.add(" ");
			statsM.setLore(lore);
			stats.setItemMeta(statsM);

			gui.addItem(stats);
		}

		guis.put(p.getUniqueId(), gui);
		opened.add(p.getUniqueId());
		p.openInventory(gui);
	}

}
