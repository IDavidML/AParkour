package me.davidml16.aparkour.gui;

import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.data.Parkour;
import me.davidml16.aparkour.data.Plate;
import me.davidml16.aparkour.managers.ColorManager;
import me.davidml16.aparkour.utils.ItemBuilder;
import me.davidml16.aparkour.utils.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class Titles_GUI implements Listener {

    private HashMap<UUID, String> opened;
    private HashMap<String, Inventory> guis;

    private Main main;

    public Titles_GUI(Main main) {
        this.main = main;
        this.opened = new HashMap<UUID, String>();
        this.guis = new HashMap<String, Inventory>();
        this.main.getServer().getPluginManager().registerEvents(this, this.main);
    }

    public HashMap<UUID, String> getOpened() {
        return opened;
    }

    public HashMap<String, Inventory> getGuis() {
        return guis;
    }

    public void loadGUI() {
        for (File file : Objects.requireNonNull(new File(main.getDataFolder(), "parkours").listFiles())) {
            loadGUI(file.getName().toLowerCase().replace(".yml", ""));
        }
    }

    public void loadGUI(String id) {
        if(guis.containsKey(id)) return;

        Inventory gui = Bukkit.createInventory(null, 45, main.getLanguageHandler().getMessage("GUIs.Titles.title").replaceAll("%parkour%", id));
        ItemStack edge = new ItemBuilder(Material.STAINED_GLASS_PANE, 1).setDurability((short) 7).setName("").toItemStack();
        ItemStack back = new ItemBuilder(Material.ARROW, 1).setName(ColorManager.translate("&aBack to config")).toItemStack();

        FileConfiguration config = main.getParkourHandler().getConfig(id);

        if(config.getBoolean("parkour.titles.start.enabled")) {
            gui.setItem(11, new ItemBuilder(Material.INK_SACK, 1).setDurability((short) 10).setName(ColorManager.translate("&a&l[+]")).toItemStack());
            gui.setItem(20, new ItemBuilder(Material.SIGN, 1).setName(ColorManager.translate("&aStart title")).setLore("", ColorManager.translate("&eClick to disable!")).toItemStack());
        } else {
            gui.setItem(11, new ItemBuilder(Material.INK_SACK, 1).setDurability((short) 8).setName(ColorManager.translate("&c&l[-]")).toItemStack());
            gui.setItem(20, new ItemBuilder(Material.SIGN, 1).setName(ColorManager.translate("&cStart title")).setLore("", ColorManager.translate("&eClick to enable!")).toItemStack());
        }

        if(config.getBoolean("parkour.titles.end.enabled")) {
            gui.setItem(13, new ItemBuilder(Material.INK_SACK, 1).setDurability((short) 10).setName(ColorManager.translate("&a&l[+]")).toItemStack());
            gui.setItem(22, new ItemBuilder(Material.SIGN, 1).setName(ColorManager.translate("&aEnd title")).setLore("", ColorManager.translate("&eClick to disable!")).toItemStack());
        } else {
            gui.setItem(13, new ItemBuilder(Material.INK_SACK, 1).setDurability((short) 8).setName(ColorManager.translate("&c&l[-]")).toItemStack());
            gui.setItem(22, new ItemBuilder(Material.SIGN, 1).setName(ColorManager.translate("&cEnd title")).setLore("", ColorManager.translate("&eClick to enable!")).toItemStack());
        }

        if(config.getBoolean("parkour.titles.checkpoint.enabled")) {
            gui.setItem(15, new ItemBuilder(Material.INK_SACK, 1).setDurability((short) 10).setName(ColorManager.translate("&a&l[+]")).toItemStack());
            gui.setItem(24, new ItemBuilder(Material.SIGN, 1).setName(ColorManager.translate("&aCheckpoint title")).setLore("", ColorManager.translate("&eClick to disable!")).toItemStack());
        } else {
            gui.setItem(15, new ItemBuilder(Material.INK_SACK, 1).setDurability((short) 8).setName(ColorManager.translate("&c&l[-]")).toItemStack());
            gui.setItem(24, new ItemBuilder(Material.SIGN, 1).setName(ColorManager.translate("&cCheckpoint title")).setLore("", ColorManager.translate("&eClick to enable!")).toItemStack());
        }

        for (int i = 0; i < 45; i++) {
            if(gui.getItem(i) == null) {
                gui.setItem(i, edge);
            }
        }

        gui.setItem(40, back);

        guis.put(id, gui);
    }

    public void reloadAllGUI() {
        for(String id : main.getParkourHandler().getParkours().keySet()) {
            reloadGUI(id);
        }
    }

    public void reloadGUI(String id) {
        Inventory gui = guis.get(id);

        FileConfiguration config = main.getParkourHandler().getConfig(id);

        if(config.getBoolean("parkour.titles.start.enabled")) {
            gui.setItem(11, new ItemBuilder(Material.INK_SACK, 1).setDurability((short) 10).setName(ColorManager.translate("&a&l[+]")).toItemStack());
            gui.setItem(20, new ItemBuilder(Material.SIGN, 1).setName(ColorManager.translate("&aStart title")).setLore("", ColorManager.translate("&eClick to disable!")).toItemStack());
        } else {
            gui.setItem(11, new ItemBuilder(Material.INK_SACK, 1).setDurability((short) 8).setName(ColorManager.translate("&c&l[-]")).toItemStack());
            gui.setItem(20, new ItemBuilder(Material.SIGN, 1).setName(ColorManager.translate("&cStart title")).setLore("", ColorManager.translate("&eClick to enable!")).toItemStack());
        }

        if(config.getBoolean("parkour.titles.end.enabled")) {
            gui.setItem(13, new ItemBuilder(Material.INK_SACK, 1).setDurability((short) 10).setName(ColorManager.translate("&a&l[+]")).toItemStack());
            gui.setItem(22, new ItemBuilder(Material.SIGN, 1).setName(ColorManager.translate("&aEnd title")).setLore("", ColorManager.translate("&eClick to disable!")).toItemStack());
        } else {
            gui.setItem(13, new ItemBuilder(Material.INK_SACK, 1).setDurability((short) 8).setName(ColorManager.translate("&c&l[-]")).toItemStack());
            gui.setItem(22, new ItemBuilder(Material.SIGN, 1).setName(ColorManager.translate("&cEnd title")).setLore("", ColorManager.translate("&eClick to enable!")).toItemStack());
        }

        if(config.getBoolean("parkour.titles.checkpoint.enabled")) {
            gui.setItem(15, new ItemBuilder(Material.INK_SACK, 1).setDurability((short) 10).setName(ColorManager.translate("&a&l[+]")).toItemStack());
            gui.setItem(24, new ItemBuilder(Material.SIGN, 1).setName(ColorManager.translate("&aCheckpoint title")).setLore("", ColorManager.translate("&eClick to disable!")).toItemStack());
        } else {
            gui.setItem(15, new ItemBuilder(Material.INK_SACK, 1).setDurability((short) 8).setName(ColorManager.translate("&c&l[-]")).toItemStack());
            gui.setItem(24, new ItemBuilder(Material.SIGN, 1).setName(ColorManager.translate("&cCheckpoint title")).setLore("", ColorManager.translate("&eClick to enable!")).toItemStack());
        }

        for(HumanEntity pl : gui.getViewers()) {
            pl.getOpenInventory().getTopInventory().setContents(gui.getContents());
        }
    }

    public void open(Player p, String id) {
        p.updateInventory();
        p.openInventory(guis.get(id));

        Sounds.playSound(p, p.getLocation(), Sounds.MySound.CLICK, 100, 3);
        Bukkit.getScheduler().runTaskLaterAsynchronously(main, () -> opened.put(p.getUniqueId(), id), 1L);
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        if (e.getCurrentItem() == null) return;

        if (opened.containsKey(p.getUniqueId())) {
            e.setCancelled(true);
            int slot = e.getRawSlot();
            if (slot == 20 || slot == 22 || slot == 24) {
                changeTitleConfig(p, slot);
            } else if (slot == 40) {
                String id = opened.get(p.getUniqueId());
                p.closeInventory();
                main.getConfigGUI().open(p, id);
            }
        }
    }

    @EventHandler
    public void InventoryCloseEvent(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        opened.remove(p.getUniqueId());
    }

    private void changeTitleConfig(Player p, int slot) {
        String id = opened.get(p.getUniqueId());
        Parkour parkour = main.getParkourHandler().getParkourById(id);
        FileConfiguration config = main.getParkourHandler().getConfig(id);
        switch (slot) {
            case 20:
                if(parkour.isStartTitleEnabled()) {
                    parkour.setStartTitleEnabled(false);
                    config.set("parkour.titles.start.enabled", false);
                    p.sendMessage(ColorManager.translate(main.getLanguageHandler().getPrefix()
                            + " &cYou disabled start title for parkour &e" + id));
                } else {
                    parkour.setStartTitleEnabled(true);
                    config.set("parkour.titles.start.enabled", true);
                    p.sendMessage(ColorManager.translate(main.getLanguageHandler().getPrefix()
                            + " &aYou enabled start title for parkour &e" + id));
                }
                break;
            case 22:
                if(parkour.isEndTitleEnabled()) {
                    parkour.setEndTitleEnabled(false);
                    config.set("parkour.titles.end.enabled", false);
                    p.sendMessage(ColorManager.translate(main.getLanguageHandler().getPrefix()
                            + " &cYou disabled end title for parkour &e" + id));
                } else {
                    parkour.setEndTitleEnabled(true);
                    config.set("parkour.titles.end.enabled", true);
                    p.sendMessage(ColorManager.translate(main.getLanguageHandler().getPrefix()
                            + " &aYou enabled end title for parkour &e" + id));
                }
                break;
            case 24:
                if(parkour.isCheckpointTitleEnabled()) {
                    parkour.setCheckpointTitleEnabled(false);
                    config.set("parkour.titles.checkpoint.enabled", false);
                    p.sendMessage(ColorManager.translate(main.getLanguageHandler().getPrefix()
                            + " &cYou disabled end title for parkour &e" + id));
                } else {
                    parkour.setCheckpointTitleEnabled(true);
                    config.set("parkour.titles.checkpoint.enabled", true);
                    p.sendMessage(ColorManager.translate(main.getLanguageHandler().getPrefix()
                            + " &aYou enabled end title for parkour &e" + id));
                }
                break;
            default:
                break;
        }
        main.getParkourHandler().saveConfig(id);
        Sounds.playSound(p, p.getLocation(), Sounds.MySound.CLICK, 100, 3);
        reloadGUI(id);
    }

}