package me.davidml16.aparkour.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import me.davidml16.aparkour.data.LeaderboardEntry;
import me.davidml16.aparkour.data.Pair;
import me.davidml16.aparkour.data.Plate;
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

	private HashMap<UUID, Integer> opened;
	private List<Integer> borders;

	private Main main;

	public PlayerStats_GUI(Main main) {
		this.main = main;
		this.opened = new HashMap<UUID, Integer>();
		this.borders = Arrays.asList(0, 1, 2, 3, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44);
		this.main.getServer().getPluginManager().registerEvents(this, this.main);
	}

	public HashMap<UUID, Integer> getOpened() {
		return opened;
	}

	private void openPage(Player p, int page) {

		List<Parkour> parkours = new ArrayList<>(main.getParkourHandler().getParkours().values());

		if(page > 0 && parkours.size() < (page * 21) + 1) {
			openPage(p, page - 1);
			return;
		}

		Inventory gui = Bukkit.createInventory(null, 45, main.getLanguageHandler().getMessage("GUIs.Stats.title"));

		ItemStack edge = new ItemBuilder(Material.STAINED_GLASS_PANE, 1).setDurability((short) 7).setName("").toItemStack();
		ItemStack book = new ItemBuilder(Material.BOOK, 1).setName(ColorManager.translate("&a&l" + p.getName() + "'s statistics")).toItemStack();

		for (Integer i : borders) {
			gui.setItem(i, edge);
		}

		if (page > 0) {
			gui.setItem(18, new ItemBuilder(Material.ENDER_PEARL, 1).setName(ColorManager.translate("&aPrevious page")).toItemStack());
		} else {
			gui.setItem(18, new ItemBuilder(Material.STAINED_GLASS_PANE, 1).setDurability((short) 7).setName("").toItemStack());
		}

		if (parkours.size() > (page + 1) * 21) {
			gui.setItem(26, new ItemBuilder(Material.ENDER_PEARL, 1).setName(ColorManager.translate("&aNext page")).toItemStack());
		} else {
			gui.setItem(26, new ItemBuilder(Material.STAINED_GLASS_PANE, 1).setDurability((short) 7).setName("").toItemStack());
		}

		gui.setItem(4, book);

		if (parkours.size() > 21) parkours = parkours.subList(page * 21, ((page * 21) + 21) > parkours.size() ? parkours.size() : (page * 21) + 21);

		if(parkours.size() > 0) {
			for (Parkour parkour : parkours) {
				List<String> lore = new ArrayList<String>();
				lore.add(" ");

				lore.add(ColorManager.translate("  &fYour Last Time  "));
				if (main.getPlayerDataHandler().getData(p).getLastTimes().get(parkour.getId()) > 0)
					lore.add(ColorManager.translate("    &e" + main.getTimerManager().millisToString(main.getLanguageHandler().getMessage("Timer.Formats.PlayerTime"), main.getPlayerDataHandler().getData(p).getLastTimes().get(parkour.getId())) + "  "));
				else
					lore.add(ColorManager.translate("    &eNone  "));

				lore.add(" ");

				lore.add(ColorManager.translate("  &fYour Record Time  "));
				if (main.getPlayerDataHandler().getData(p).getBestTimes().get(parkour.getId()) > 0)
					lore.add(ColorManager.translate("    &e" + main.getTimerManager().millisToString(main.getLanguageHandler().getMessage("Timer.Formats.PlayerTime"), main.getPlayerDataHandler().getData(p).getBestTimes().get(parkour.getId())) + "  "));
				else
					lore.add(ColorManager.translate("    &eNone  "));

				List<LeaderboardEntry> leaderboard = main.getLeaderboardHandler().getLeaderboard(parkour.getId());

				if (leaderboard.size() > 0) {
					lore.add("");
					lore.add(ColorManager.translate("  &fWorld records  "));
					int i = 0;
					for (LeaderboardEntry entry : leaderboard) {
						lore.add(ColorManager.translate("    &7" + entry.getName() + " &f- &c" + main.getTimerManager().millisToString(main.getLanguageHandler().getMessage("Timer.Formats.PlayerTime"), entry.getTime()) + "  "));
						if (i == 2) break;
						i++;
					}
				}

				lore.add("");

				gui.addItem(new ItemBuilder(parkour.getIcon()).setName(ColorManager.translate("&a&l" + parkour.getName())).setLore(lore).toItemStack());
			}
		} else {
			gui.setItem(22, new ItemBuilder(Material.STAINED_GLASS_PANE, 1).setDurability((short) 14).setName(ColorManager.translate("&c")).toItemStack());
		}

		Bukkit.getScheduler().runTaskLater(main, () -> opened.put(p.getUniqueId(), page), 1L);

		p.openInventory(gui);

		Sounds.playSound(p, p.getLocation(), Sounds.MySound.CLICK, 100, 3);

	}

	public void open(Player p) {
		p.updateInventory();
		openPage(p, 0);
	}

	@EventHandler
	public void onInventoryClickEvent(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();

		if (e.getCurrentItem() == null) return;

		if (opened.containsKey(p.getUniqueId())) {
			e.setCancelled(true);
			int slot = e.getRawSlot();
			if (slot == 18 && e.getCurrentItem().getType() == Material.ENDER_PEARL) {
				openPage(p, opened.get(p.getUniqueId()) - 1);
			} else if (slot == 26 && e.getCurrentItem().getType() == Material.ENDER_PEARL) {
				openPage(p, opened.get(p.getUniqueId()) + 1);
			}
		}
	}

	@EventHandler
	public void InventoryCloseEvent(InventoryCloseEvent e) {
		Player p = (Player) e.getPlayer();
		opened.remove(p.getUniqueId());
	}

}
