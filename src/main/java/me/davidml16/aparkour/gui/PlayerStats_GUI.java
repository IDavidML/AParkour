package me.davidml16.aparkour.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import me.davidml16.aparkour.data.WalkableBlock;
import me.davidml16.aparkour.managers.ColorManager;
import me.davidml16.aparkour.managers.PluginManager;
import me.davidml16.aparkour.utils.ItemBuilder;
import me.davidml16.aparkour.utils.Sounds;
import me.davidml16.aparkour.utils.WalkableBlocksUtil;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.data.Parkour;

public class PlayerStats_GUI implements Listener {

	private List<UUID> opened;
	private HashMap<UUID, Inventory> guis;
	private List<Integer> borders;

	public PlayerStats_GUI() {
		this.opened = new ArrayList<UUID>();
		this.guis = new HashMap<UUID, Inventory>();
		this.borders = Arrays.asList(0, 1, 2, 3, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44);
		Main.getInstance().getServer().getPluginManager().registerEvents(this, Main.getInstance());
	}

	public List<UUID> getOpened() {
		return opened;
	}

	public HashMap<UUID, Inventory> getGuis() {
		return guis;
	}

	public void open(Player p) {
		p.updateInventory();

		Inventory gui = Bukkit.createInventory(null, 45, Main.getInstance().getLanguageHandler().getMessage("GUIs.Stats.title"));

		ItemStack edge = new ItemBuilder(Material.STAINED_GLASS_PANE, 1).setDurability((short) 7).setName("").toItemStack();
		ItemStack book = new ItemBuilder(Material.BOOK, 1).setName(ColorManager.translate("&a&l" + p.getName() + "'s statistics")).toItemStack();

		for (Integer i : borders) {
			gui.setItem(i, edge);
		}

		gui.setItem(4, book);

		for (Parkour parkour : Main.getInstance().getParkourHandler().getParkours().values()) {
			List<String> lore = new ArrayList<String>();
			lore.add(ColorManager.translate("  &eParkour: &a" + parkour.getName() + "  "));
			lore.add(" ");

			if(Main.getInstance().getPlayerDataHandler().getData(p).getLastTimes().get(parkour.getId()) > 0)
				lore.add(ColorManager.translate("  &eLast Time: &6" + Main.getInstance().getTimerManager().timeAsString(Main.getInstance().getPlayerDataHandler().getData(p).getLastTimes().get(parkour.getId())) + "  "));
			else
				lore.add(ColorManager.translate("  &eLast Time: &c" + Main.getInstance().getLanguageHandler().getMessage("Times.NoBestTime") + "  "));

			lore.add(" ");

			if(Main.getInstance().getPlayerDataHandler().getData(p).getBestTimes().get(parkour.getId()) > 0)
				lore.add(ColorManager.translate("  &eBest Time: &6" + Main.getInstance().getTimerManager().timeAsString(Main.getInstance().getPlayerDataHandler().getData(p).getBestTimes().get(parkour.getId())) + "  "));
			else
				lore.add(ColorManager.translate("  &eBest Time: &c" + Main.getInstance().getLanguageHandler().getMessage("Times.NoBestTime") + "  "));

			lore.add(" ");

			gui.addItem(new ItemBuilder(Material.ITEM_FRAME, 1).setName(ColorManager.translate("&e")).setLore(lore).toItemStack());
		}

		guis.put(p.getUniqueId(), gui);
		opened.add(p.getUniqueId());
		p.openInventory(gui);
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
