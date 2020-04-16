package me.davidml16.aparkour.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import me.davidml16.aparkour.data.LeaderboardEntry;
import me.davidml16.aparkour.managers.ColorManager;
import me.davidml16.aparkour.utils.ItemBuilder;
import me.davidml16.aparkour.utils.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.data.Parkour;

public class PlayerStats_GUI implements Listener {

	private List<UUID> opened;
	private HashMap<UUID, Inventory> guis;
	private List<Integer> borders;

	private Main main;

	public PlayerStats_GUI(Main main) {
		this.main = main;
		this.opened = new ArrayList<UUID>();
		this.guis = new HashMap<UUID, Inventory>();
		this.borders = Arrays.asList(0, 1, 2, 3, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44);
		this.main.getServer().getPluginManager().registerEvents(this, this.main);
	}

	public List<UUID> getOpened() {
		return opened;
	}

	public HashMap<UUID, Inventory> getGuis() {
		return guis;
	}

	public void open(Player p) {
		p.updateInventory();

		Inventory gui = Bukkit.createInventory(null, 45, main.getLanguageHandler().getMessage("GUIs.Stats.title"));

		ItemStack edge = new ItemBuilder(Material.STAINED_GLASS_PANE, 1).setDurability((short) 7).setName("").toItemStack();
		ItemStack book = new ItemBuilder(Material.BOOK, 1).setName(ColorManager.translate("&a&l" + p.getName() + "'s statistics")).toItemStack();

		for (Integer i : borders) {
			gui.setItem(i, edge);
		}

		gui.setItem(4, book);

		for (Parkour parkour : main.getParkourHandler().getParkours().values()) {
			List<String> lore = new ArrayList<String>();
			lore.add(" ");

			lore.add(ColorManager.translate("  &fYour Last Time  "));
			if(main.getPlayerDataHandler().getData(p).getLastTimes().get(parkour.getId()) > 0)
				lore.add(ColorManager.translate("    &e" + main.getTimerManager().millisToString(main.getPlayerDataHandler().getData(p).getLastTimes().get(parkour.getId())) + "  "));
			else
				lore.add(ColorManager.translate("    &eNone  "));

			lore.add(" ");

			lore.add(ColorManager.translate("  &fYour Record Time  "));
			if(main.getPlayerDataHandler().getData(p).getBestTimes().get(parkour.getId()) > 0)
				lore.add(ColorManager.translate("    &e" + main.getTimerManager().millisToString(main.getPlayerDataHandler().getData(p).getBestTimes().get(parkour.getId())) + "  "));
			else
				lore.add(ColorManager.translate("    &eNone  "));

			List<LeaderboardEntry> leaderboard = main.getLeaderboardHandler().getLeaderboard(parkour.getId());

			if(leaderboard.size() > 0) {
				lore.add("");
				lore.add(ColorManager.translate("  &fWorld records  "));
				int i = 0;
				for (LeaderboardEntry entry : leaderboard) {
					lore.add(ColorManager.translate("    &7" + entry.getName() + " &f- &c" + main.getTimerManager().millisToString(entry.getTime()) + "  "));
					if(i == 2) break;
					i++;
				}
			}

			lore.add("");

			gui.addItem(new ItemBuilder(parkour.getIcon()).setName(ColorManager.translate("&a&l" + parkour.getName())).setLore(lore).toItemStack());
		}

		guis.put(p.getUniqueId(), gui);
		opened.add(p.getUniqueId());
		p.openInventory(gui);
		Sounds.playSound(p, p.getLocation(), Sounds.MySound.CLICK, 100, 3);
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
