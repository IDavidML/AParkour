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
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

public class Holograms_GUI implements Listener {

    private HashMap<UUID, String> opened;
    private HashMap<String, Inventory> guis;

    private Main main;

    public Holograms_GUI(Main main) {
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

        Inventory gui = Bukkit.createInventory(null, 45, main.getLanguageHandler().getMessage("GUIs.Holograms.title").replaceAll("%parkour%", id));
        ItemStack edge = new ItemBuilder(Material.STAINED_GLASS_PANE, 1).setDurability((short) 7).setName("").toItemStack();
        ItemStack back = new ItemBuilder(Material.ARROW, 1).setName(ColorManager.translate("&aBack to config")).toItemStack();

        FileConfiguration config = main.getParkourHandler().getConfig(id);

        if(config.contains("parkour.holograms.stats")) {
            gui.setItem(10, new ItemBuilder(Material.INK_SACK, 1).setDurability((short) 10).setName(ColorManager.translate("&a&l[+]")).toItemStack());
            gui.setItem(19, new ItemBuilder(Material.ARMOR_STAND, 1).setName(ColorManager.translate("&aStats hologram location")).setLore("", ColorManager.translate("&eClick to relocate location!")).toItemStack());
        } else {
            gui.setItem(10, new ItemBuilder(Material.INK_SACK, 1).setDurability((short) 8).setName(ColorManager.translate("&c&l[-]")).toItemStack());
            gui.setItem(19, new ItemBuilder(Material.ARMOR_STAND, 1).setName(ColorManager.translate("&cStats hologram location")).setLore("", ColorManager.translate("&eClick to set location!")).toItemStack());
        }

        if(config.contains("parkour.holograms.top")) {
            gui.setItem(11, new ItemBuilder(Material.INK_SACK, 1).setDurability((short) 10).setName(ColorManager.translate("&a&l[+]")).toItemStack());
            gui.setItem(20, new ItemBuilder(Material.ARMOR_STAND, 1).setName(ColorManager.translate("&aTop hologram location")).setLore("", ColorManager.translate("&eClick to relocate location!")).toItemStack());
        } else {
            gui.setItem(11, new ItemBuilder(Material.INK_SACK, 1).setDurability((short) 8).setName(ColorManager.translate("&c&l[-]")).toItemStack());
            gui.setItem(20, new ItemBuilder(Material.ARMOR_STAND, 1).setName(ColorManager.translate("&cTop hologram location")).setLore("", ColorManager.translate("&eClick to set location!")).toItemStack());
        }

        if(config.getBoolean("parkour.plateHolograms.start.enabled")) {
            gui.setItem(23, new ItemBuilder(Material.ARMOR_STAND, 1).setName(ColorManager.translate("&aStart plate hologram")).setLore("", ColorManager.translate("&eClick to disable!")).toItemStack());
        } else {
            gui.setItem(23, new ItemBuilder(Material.ARMOR_STAND, 1).setName(ColorManager.translate("&cStart plate hologram")).setLore("", ColorManager.translate("&eClick to enable!")).toItemStack());
        }

        gui.setItem(14, new ItemBuilder(Material.NAME_TAG, 1).setName(ColorManager.translate("&aChange start hologram height"))
                .setLore(
                        "",
                        ColorManager.translate(" &7Offset: &6" + config.getDouble("parkour.plateHolograms.start.distanceBelowPlate") + " blocks "),
                        "",
                        ColorManager.translate("&eLeft-Click to add 0.1 "),
                        ColorManager.translate("&eRight-Click to subtract 0.1 ")
                ).toItemStack());

        if(config.getBoolean("parkour.plateHolograms.end.enabled")) {
            gui.setItem(24, new ItemBuilder(Material.ARMOR_STAND, 1).setName(ColorManager.translate("&aEnd plate hologram")).setLore("", ColorManager.translate("&eClick to disable!")).toItemStack());
        } else {
            gui.setItem(24, new ItemBuilder(Material.ARMOR_STAND, 1).setName(ColorManager.translate("&cEnd plate hologram")).setLore("", ColorManager.translate("&eClick to enable!")).toItemStack());
        }

        gui.setItem(15, new ItemBuilder(Material.NAME_TAG, 1).setName(ColorManager.translate("&aChange end hologram height"))
                .setLore(
                        "",
                        ColorManager.translate(" &7Offset: &6" + config.getDouble("parkour.plateHolograms.end.distanceBelowPlate") + " blocks "),
                        "",
                        ColorManager.translate("&eLeft-Click to add 0.1 "),
                        ColorManager.translate("&eRight-Click to subtract 0.1 ")
                ).toItemStack());

        if(config.getBoolean("parkour.plateHolograms.checkpoints.enabled")) {
            gui.setItem(25, new ItemBuilder(Material.ARMOR_STAND, 1).setName(ColorManager.translate("&aCheckpoint plate holograms")).setLore("", ColorManager.translate("&eClick to disable!")).toItemStack());
        } else {
            gui.setItem(25, new ItemBuilder(Material.ARMOR_STAND, 1).setName(ColorManager.translate("&cCheckpoint plate holograms")).setLore("", ColorManager.translate("&eClick to enable!")).toItemStack());
        }

        gui.setItem(16, new ItemBuilder(Material.NAME_TAG, 1).setName(ColorManager.translate("&aChange checkpoint hologram height"))
                .setLore(
                        "",
                        ColorManager.translate(" &7Offset: &6" + config.getDouble("parkour.plateHolograms.checkpoints.distanceBelowPlate") + " blocks "),
                        "",
                        ColorManager.translate("&eLeft-Click to add 0.1 "),
                        ColorManager.translate("&eRight-Click to subtract 0.1 ")
                ).toItemStack());

        gui.setItem(12, new ItemBuilder(Material.STAINED_GLASS_PANE, 1).setDurability((short) 14).setName(ColorManager.translate("&cComing soon")).toItemStack());
        gui.setItem(21, new ItemBuilder(Material.STAINED_GLASS_PANE, 1).setDurability((short) 14).setName(ColorManager.translate("&cComing soon")).toItemStack());

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

        Parkour parkour = main.getParkourHandler().getParkourById(id);

        if(config.contains("parkour.holograms.stats")) {
            gui.setItem(10, new ItemBuilder(Material.INK_SACK, 1).setDurability((short) 10).setName(ColorManager.translate("&a&l[+]")).toItemStack());
            gui.setItem(19, new ItemBuilder(Material.ARMOR_STAND, 1).setName(ColorManager.translate("&aStats hologram location")).setLore("", ColorManager.translate("&eClick to relocate location!")).toItemStack());
        } else {
            gui.setItem(10, new ItemBuilder(Material.INK_SACK, 1).setDurability((short) 8).setName(ColorManager.translate("&c&l[-]")).toItemStack());
            gui.setItem(19, new ItemBuilder(Material.ARMOR_STAND, 1).setName(ColorManager.translate("&cStats hologram location")).setLore("", ColorManager.translate("&eClick to set location!")).toItemStack());
        }

        if(config.contains("parkour.holograms.top")) {
            gui.setItem(11, new ItemBuilder(Material.INK_SACK, 1).setDurability((short) 10).setName(ColorManager.translate("&a&l[+]")).toItemStack());
            gui.setItem(20, new ItemBuilder(Material.ARMOR_STAND, 1).setName(ColorManager.translate("&aTop hologram location")).setLore("", ColorManager.translate("&eClick to relocate location!")).toItemStack());
        } else {
            gui.setItem(11, new ItemBuilder(Material.INK_SACK, 1).setDurability((short) 8).setName(ColorManager.translate("&c&l[-]")).toItemStack());
            gui.setItem(20, new ItemBuilder(Material.ARMOR_STAND, 1).setName(ColorManager.translate("&cTop hologram location")).setLore("", ColorManager.translate("&eClick to set location!")).toItemStack());
        }

        if(config.getBoolean("parkour.plateHolograms.start.enabled")) {
            gui.setItem(23, new ItemBuilder(Material.ARMOR_STAND, 1).setName(ColorManager.translate("&aStart plate hologram")).setLore("", ColorManager.translate("&eClick to disable!")).toItemStack());
        } else {
            gui.setItem(23, new ItemBuilder(Material.ARMOR_STAND, 1).setName(ColorManager.translate("&cStart plate hologram")).setLore("", ColorManager.translate("&eClick to enable!")).toItemStack());
        }

        gui.setItem(14, new ItemBuilder(Material.NAME_TAG, 1).setName(ColorManager.translate("&aChange start hologram height"))
                .setLore(
                        "",
                        ColorManager.translate(" &7Offset: &6" + config.getDouble("parkour.plateHolograms.start.distanceBelowPlate") + " blocks "),
                        "",
                        ColorManager.translate("&eLeft-Click to add 0.1 "),
                        ColorManager.translate("&eRight-Click to subtract 0.1 ")
                ).toItemStack());

        if(config.getBoolean("parkour.plateHolograms.end.enabled")) {
            gui.setItem(24, new ItemBuilder(Material.ARMOR_STAND, 1).setName(ColorManager.translate("&aEnd plate hologram")).setLore("", ColorManager.translate("&eClick to disable!")).toItemStack());
        } else {
            gui.setItem(24, new ItemBuilder(Material.ARMOR_STAND, 1).setName(ColorManager.translate("&cEnd plate hologram")).setLore("", ColorManager.translate("&eClick to enable!")).toItemStack());
        }

        gui.setItem(15, new ItemBuilder(Material.NAME_TAG, 1).setName(ColorManager.translate("&aChange end hologram height"))
                .setLore(
                        "",
                        ColorManager.translate(" &7Offset: &6" + config.getDouble("parkour.plateHolograms.end.distanceBelowPlate") + " blocks "),
                        "",
                        ColorManager.translate("&eLeft-Click to add 0.1 "),
                        ColorManager.translate("&eRight-Click to subtract 0.1 ")
                ).toItemStack());

        if(config.getBoolean("parkour.plateHolograms.checkpoints.enabled")) {
            gui.setItem(25, new ItemBuilder(Material.ARMOR_STAND, 1).setName(ColorManager.translate("&aCheckpoint plate holograms")).setLore("", ColorManager.translate("&eClick to disable!")).toItemStack());
        } else {
            gui.setItem(25, new ItemBuilder(Material.ARMOR_STAND, 1).setName(ColorManager.translate("&cCheckpoint plate holograms")).setLore("", ColorManager.translate("&eClick to enable!")).toItemStack());
        }

        gui.setItem(16, new ItemBuilder(Material.NAME_TAG, 1).setName(ColorManager.translate("&aChange checkpoint hologram height"))
                .setLore(
                        "",
                        ColorManager.translate(" &7Offset: &6" + config.getDouble("parkour.plateHolograms.checkpoints.distanceBelowPlate") + " blocks "),
                        "",
                        ColorManager.translate("&eLeft-Click to add 0.1 "),
                        ColorManager.translate("&eRight-Click to subtract 0.1 ")
                ).toItemStack());

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
            if (slot == 19 || slot == 20) {
                changeParkourConfig(p, slot);
            } else if (slot >= 14 && slot <= 16) {
                if(e.getClick().isLeftClick())
                    changeHologramHeight(p, slot, "add");
                else if(e.getClick().isRightClick())
                    changeHologramHeight(p, slot, "subtract");
            } else if (slot >= 23 && slot <= 25) {
                changeHologramConfig(p, slot);
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

    private void changeParkourConfig(Player p, int slot) {
        String id = opened.get(p.getUniqueId());
        switch (slot) {
            case 19:
                main.getLocationUtil().setHologram(p, id, "stats");
                break;
            case 20:
                main.getLocationUtil().setHologram(p, id, "top");
                break;
            default:
                break;
        }
        Sounds.playSound(p, p.getLocation(), Sounds.MySound.CLICK, 100, 3);
        reloadGUI(id);
    }

    private void changeHologramHeight(Player p, int slot, String type) {
        String id = opened.get(p.getUniqueId());
        Parkour parkour = main.getParkourHandler().getParkourById(id);
        FileConfiguration config = main.getParkourHandler().getConfig(id);
        main.getParkourHandler().removeHologram(id);
        double newHeight;
        switch (slot) {
            case 14:
                newHeight = config.getDouble("parkour.plateHolograms.start.distanceBelowPlate");
                if(type.equalsIgnoreCase("add"))
                    newHeight = newHeight + 0.1;
                else if(type.equalsIgnoreCase("subtract"))
                    newHeight = newHeight - 0.1;
                newHeight = Math.round(newHeight * 10) / 10.0;
                config.set("parkour.plateHolograms.start.distanceBelowPlate", newHeight);
                parkour.getStart().setHologramDistance(newHeight);
                break;
            case 15:
                newHeight = config.getDouble("parkour.plateHolograms.end.distanceBelowPlate");
                if(type.equalsIgnoreCase("add"))
                    newHeight = newHeight + 0.1;
                else if(type.equalsIgnoreCase("subtract"))
                    newHeight = newHeight - 0.1;
                newHeight = Math.round(newHeight * 10) / 10.0;
                config.set("parkour.plateHolograms.end.distanceBelowPlate", newHeight);
                parkour.getStart().setHologramDistance(newHeight);
                break;
            case 16:
                newHeight = config.getDouble("parkour.plateHolograms.checkpoints.distanceBelowPlate");
                if(type.equalsIgnoreCase("add"))
                    newHeight = newHeight + 0.1;
                else if(type.equalsIgnoreCase("subtract"))
                    newHeight = newHeight - 0.1;
                newHeight = Math.round(newHeight * 10) / 10.0;
                config.set("parkour.plateHolograms.checkpoints.distanceBelowPlate", newHeight);
                parkour.getStart().setHologramDistance(newHeight);
                break;
            default:
                break;
        }
        main.getParkourHandler().saveConfig(id);
        main.getParkourHandler().loadHolograms(parkour);
        Sounds.playSound(p, p.getLocation(), Sounds.MySound.CLICK, 100, 3);
        reloadGUI(id);
    }

    private void changeHologramConfig(Player p, int slot) {
        String id = opened.get(p.getUniqueId());
        Parkour parkour = main.getParkourHandler().getParkourById(id);
        FileConfiguration config = main.getParkourHandler().getConfig(id);
        main.getParkourHandler().removeHologram(id);
        switch (slot) {
            case 23:
                if(parkour.getStart().isHologramEnabled()) {
                    parkour.getStart().setHologramEnabled(false);
                    config.set("parkour.plateHolograms.start.enabled", false);
                    p.sendMessage(ColorManager.translate(main.getLanguageHandler().getPrefix()
                            + " &cYou disabled start plate hologram for parkour &e" + id));
                } else {
                    parkour.getStart().setHologramEnabled(true);
                    config.set("parkour.plateHolograms.start.enabled", true);
                    p.sendMessage(ColorManager.translate(main.getLanguageHandler().getPrefix()
                            + " &aYou enabled start plate hologram for parkour &e" + id));
                }
                break;
            case 24:
                if(parkour.getEnd().isHologramEnabled()) {
                    parkour.getEnd().setHologramEnabled(false);
                    config.set("parkour.plateHolograms.end.enabled", false);
                    p.sendMessage(ColorManager.translate(main.getLanguageHandler().getPrefix()
                            + " &cYou disabled end plate hologram for parkour &e" + id));
                } else {
                    parkour.getEnd().setHologramEnabled(true);
                    config.set("parkour.plateHolograms.end.enabled", true);
                    p.sendMessage(ColorManager.translate(main.getLanguageHandler().getPrefix()
                            + " &aYou enabled end plate hologram for parkour &e" + id));
                }
                break;
            case 25:
                if(config.getBoolean("parkour.plateHolograms.checkpoints.enabled")) {
                    for(Plate checkpoint : parkour.getCheckpoints())
                        checkpoint.setHologramEnabled(false);
                    config.set("parkour.plateHolograms.checkpoints.enabled", false);
                    p.sendMessage(ColorManager.translate(main.getLanguageHandler().getPrefix()
                            + " &cYou disabled checkpoint plate holograms for parkour &e" + id));
                } else {
                    for(Plate checkpoint : parkour.getCheckpoints())
                        checkpoint.setHologramEnabled(true);
                    config.set("parkour.plateHolograms.checkpoints.enabled", true);
                    p.sendMessage(ColorManager.translate(main.getLanguageHandler().getPrefix()
                            + " &aYou enabled checkpoint plate holograms for parkour &e" + id));
                }
                break;
            default:
                break;
        }
        main.getParkourHandler().saveConfig(id);
        main.getParkourHandler().loadHolograms(parkour);
        Sounds.playSound(p, p.getLocation(), Sounds.MySound.CLICK, 100, 3);
        reloadGUI(id);
    }

}