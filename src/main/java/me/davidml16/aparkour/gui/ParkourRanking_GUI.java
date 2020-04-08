package me.davidml16.aparkour.gui;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import me.davidml16.aparkour.data.Parkour;
import me.davidml16.aparkour.data.WalkableBlock;
import me.davidml16.aparkour.managers.ColorManager;
import me.davidml16.aparkour.managers.PluginManager;
import me.davidml16.aparkour.utils.ItemBuilder;
import me.davidml16.aparkour.utils.Sounds;
import me.davidml16.aparkour.utils.WalkableBlocksUtil;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.davidml16.aparkour.Main;

public class ParkourRanking_GUI implements Listener {

	private List<UUID> opened;
	private Inventory gui;
	private List<Integer> borders;

	private Main main;

	public ParkourRanking_GUI(Main main) {
		this.main = main;
		this.opened = new ArrayList<UUID>();
		this.borders = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44);
		this.main.getServer().getPluginManager().registerEvents(this, this.main);
	}

	public List<UUID> getOpened() {
		return opened;
	}

	public Inventory getGUI() {
		return gui;
	}
	
	public void loadGUI() {
		gui = Bukkit.createInventory(null, 45, main.getLanguageHandler().getMessage("GUIs.Top.title"));

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
		for (Parkour parkour : main.getParkourHandler().getParkours().values()) {
			List<String> lore = new ArrayList<String>();
			lore.add(ColorManager.translate("  &eParkour: &a" + parkour.getName() + "  "));
			lore.add(" ");

			HashMap<String, Integer> times = main.getDatabaseHandler().getParkourBestTimes(parkour.getId(), 10);

			int it = 1;
			for (Entry<String, Integer> entry : times.entrySet()) {
				try {
					lore.add(ColorManager.translate("   &e" + it + ". &a"
							+ main.getDatabaseHandler().getPlayerName(entry.getKey().toString()) + "&7 - &6"
							+ main.getTimerManager().timeAsString(entry.getValue()) + "  "));
					it++;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			for (int i = it; i <= 10; i++) {
				if (i < 10)
					lore.add(ColorManager.translate("   &e" + i + ". &c" + main.getLanguageHandler().getMessage("Times.NoBestTime") + "  "));
				else
					lore.add(ColorManager.translate(" &0.&e" + i + ". &c" + main.getLanguageHandler().getMessage("Times.NoBestTime") + "  "));
			}
			lore.add(" ");

			gui.addItem(new ItemBuilder(Material.ITEM_FRAME, 1).setName(ColorManager.translate("&e")).setLore(lore).toItemStack());
		}
	}

	public void open(Player p) {
		p.updateInventory();
		p.openInventory(this.gui);

		Sounds.playSound(p, p.getLocation(), Sounds.MySound.CLICK, 100, 3);
		opened.add(p.getUniqueId());
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInventoryClickEvent(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();

		if (e.getCurrentItem() == null) return;

		if (opened.contains(p.getUniqueId())) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void InventoryCloseEvent(InventoryCloseEvent e) {
		Player p = (Player) e.getPlayer();
		opened.remove(p.getUniqueId());
	}

}
