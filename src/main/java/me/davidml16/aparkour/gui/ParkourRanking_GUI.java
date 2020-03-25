package me.davidml16.aparkour.gui;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import me.davidml16.aparkour.data.Parkour;
import me.davidml16.aparkour.managers.ColorManager;
import me.davidml16.aparkour.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.davidml16.aparkour.Main;

public class ParkourRanking_GUI {

	private List<UUID> opened;
	private Inventory gui;
	private List<Integer> borders;

	public ParkourRanking_GUI() {
		this.opened = new ArrayList<UUID>();
		this.borders = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44);
	}

	public List<UUID> getOpened() {
		return opened;
	}

	public Inventory getGUI() {
		return gui;
	}
	
	public void loadGUI() {
		gui = Bukkit.createInventory(null, 45, Main.getInstance().getLanguageHandler().getMessage("GUI_TOP_TITLE", false));

		ItemStack edge = new ItemBuilder(Material.STAINED_GLASS_PANE, 1).setDurability((short) 7).setName("").toItemStack();

		for (Integer i : borders) {
			gui.setItem(i, edge);
		}

		loadParkoursFrames();
	}

	public void reloadGUI() {
		for (int i = 10; i <= 16; i++)
			gui.setItem(i, null);
		for (int i = 19; i <= 25; i++)
			gui.setItem(i, null);
		for (int i = 28; i <= 34; i++)
			gui.setItem(i, null);

		loadParkoursFrames();

		for (HumanEntity p : gui.getViewers()) {
			p.getOpenInventory().getTopInventory().setContents(gui.getContents());
		}
	}

	private void loadParkoursFrames() {
		for (Parkour parkour : Main.getInstance().getParkourHandler().getParkours().values()) {
			List<String> lore = new ArrayList<String>();
			lore.add(ColorManager.translate("  &eParkour: &a" + parkour.getName() + "  "));
			lore.add(" ");

			HashMap<String, Integer> times = new HashMap<String, Integer>();
			try {
				times = Main.getInstance().getDatabaseHandler().getParkourBestTimes(parkour.getId(), 10);
			} catch (SQLException e) {
				e.printStackTrace();
			}

			int it = 1;
			for (Entry<String, Integer> entry : times.entrySet()) {
				try {
					lore.add(ColorManager.translate("   &e" + it + ". &a"
							+ Main.getInstance().getDatabaseHandler().getPlayerName(entry.getKey().toString()) + "&7 - &6"
							+ Main.getInstance().getTimerManager().timeAsString(entry.getValue()) + "  "));
					it++;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			for (int i = it; i <= 10; i++) {
				if (i < 10)
					lore.add(ColorManager.translate("   &e" + i + ". &cN/A  "));
				else
					lore.add(ColorManager.translate(" &0.&e" + i + ". &cN/A  "));
			}
			lore.add(" ");

			gui.addItem(new ItemBuilder(Material.ITEM_FRAME, 1).setName(ColorManager.translate("&e")).setLore(lore).toItemStack());
		}
	}

	public void open(Player p) {
		p.updateInventory();

		p.openInventory(this.gui);
		opened.add(p.getUniqueId());
	}

}
