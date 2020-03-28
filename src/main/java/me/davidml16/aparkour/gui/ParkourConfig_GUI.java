package me.davidml16.aparkour.gui;

import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.managers.ColorManager;
import me.davidml16.aparkour.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

public class ParkourConfig_GUI {

    private HashMap<UUID, String> opened;
    private HashMap<String, Inventory> guis;
    private List<Integer> borders;

    public ParkourConfig_GUI() {
        this.opened = new HashMap<UUID, String>();
        this.guis = new HashMap<String, Inventory>();
        this.borders = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 15, 17, 18, 24, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 41, 42, 43, 44);
    }

    public HashMap<UUID, String> getOpened() {
        return opened;
    }

    public HashMap<String, Inventory> getGuis() {
        return guis;
    }

    public void loadGUI() {
        for (File file : Objects.requireNonNull(new File(Main.getInstance().getDataFolder(), "parkours").listFiles())) {
            loadGUI(file.getName().toLowerCase().replace(".yml", ""));
        }
    }

    public void loadGUI(String id) {
        if(guis.containsKey(id)) return;

        Inventory gui = Bukkit.createInventory(null, 45, Main.getInstance().getLanguageHandler().getMessage("GUI_CONFIG_TITLE", false).replaceAll("%parkour%", id));
        ItemStack edge = new ItemBuilder(Material.STAINED_GLASS_PANE, 1).setDurability((short) 7).setName("").toItemStack();

        for (Integer i : borders) {
            gui.setItem(i, edge);
        }

        if(Main.getInstance().getParkourHandler().getConfig(id).contains("parkour.spawn")) {
            gui.setItem(10, new ItemBuilder(Material.INK_SACK, 1).setDurability((short) 10).setName(ColorManager.translate("&a&l[+]")).toItemStack());
            gui.setItem(19, new ItemBuilder(Material.BED, 1).setName(ColorManager.translate("&aSpawn location")).setLore("", ColorManager.translate("&eClick to relocate location!")).toItemStack());
        } else {
            gui.setItem(10, new ItemBuilder(Material.INK_SACK, 1).setDurability((short) 8).setName(ColorManager.translate("&c&l[-]")).toItemStack());
            gui.setItem(19, new ItemBuilder(Material.BED, 1).setName(ColorManager.translate("&cSpawn location")).setLore("", ColorManager.translate("&eClick to set location!")).toItemStack());
        }

        if(Main.getInstance().getParkourHandler().getConfig(id).contains("parkour.start")) {
            gui.setItem(11, new ItemBuilder(Material.INK_SACK, 1).setDurability((short) 10).setName(ColorManager.translate("&a&l[+]")).toItemStack());
            gui.setItem(20, new ItemBuilder(Material.IRON_PLATE, 1).setName(ColorManager.translate("&aStart plate location")).setLore("", ColorManager.translate("&eClick to relocate location!")).toItemStack());
        } else {
            gui.setItem(11, new ItemBuilder(Material.INK_SACK, 1).setDurability((short) 8).setName(ColorManager.translate("&c&l[-]")).toItemStack());
            gui.setItem(20, new ItemBuilder(Material.IRON_PLATE, 1).setName(ColorManager.translate("&cStart plate location")).setLore("", ColorManager.translate("&eClick to set location!")).toItemStack());
        }

        if(Main.getInstance().getParkourHandler().getConfig(id).contains("parkour.end")) {
            gui.setItem(12, new ItemBuilder(Material.INK_SACK, 1).setDurability((short) 10).setName(ColorManager.translate("&a&l[+]")).toItemStack());
            gui.setItem(21, new ItemBuilder(Material.GOLD_PLATE, 1).setName(ColorManager.translate("&aGold plate location")).setLore("", ColorManager.translate("&eClick to relocate location!")).toItemStack());
        } else {
            gui.setItem(12, new ItemBuilder(Material.INK_SACK, 1).setDurability((short) 8).setName(ColorManager.translate("&c&l[-]")).toItemStack());
            gui.setItem(21, new ItemBuilder(Material.GOLD_PLATE, 1).setName(ColorManager.translate("&cGold plate location")).setLore("", ColorManager.translate("&eClick to set location!")).toItemStack());
        }

        if(Main.getInstance().getParkourHandler().getConfig(id).contains("parkour.holograms.stats")) {
            gui.setItem(13, new ItemBuilder(Material.INK_SACK, 1).setDurability((short) 10).setName(ColorManager.translate("&a&l[+]")).toItemStack());
            gui.setItem(22, new ItemBuilder(Material.ARMOR_STAND, 1).setName(ColorManager.translate("&aStats hologram location")).setLore("", ColorManager.translate("&eClick to relocate location!")).toItemStack());
        } else {
            gui.setItem(13, new ItemBuilder(Material.INK_SACK, 1).setDurability((short) 8).setName(ColorManager.translate("&c&l[-]")).toItemStack());
            gui.setItem(22, new ItemBuilder(Material.ARMOR_STAND, 1).setName(ColorManager.translate("&cStats hologram location")).setLore("", ColorManager.translate("&eClick to set location!")).toItemStack());
        }

        if(Main.getInstance().getParkourHandler().getConfig(id).contains("parkour.holograms.top")) {
            gui.setItem(14, new ItemBuilder(Material.INK_SACK, 1).setDurability((short) 10).setName(ColorManager.translate("&a&l[+]")).toItemStack());
            gui.setItem(23, new ItemBuilder(Material.ARMOR_STAND, 1).setName(ColorManager.translate("&aTop hologram location")).setLore("", ColorManager.translate("&eClick to relocate location!")).toItemStack());
        } else {
            gui.setItem(14, new ItemBuilder(Material.INK_SACK, 1).setDurability((short) 8).setName(ColorManager.translate("&c&l[-]")).toItemStack());
            gui.setItem(23, new ItemBuilder(Material.ARMOR_STAND, 1).setName(ColorManager.translate("&cTop hologram location")).setLore("", ColorManager.translate("&eClick to set location!")).toItemStack());
        }

        if(Main.getInstance().getParkourHandler().getParkours().containsKey(id)) {
            gui.setItem(16, new ItemBuilder(Material.BOOK, 1)
                    .setName(ColorManager.translate("&aWalkable blocks tutorial"))
                    .setLore("", ColorManager.translate(" &7Open walkable blocks gui ")
                            , ColorManager.translate(" &7and click on a block "),
                            ColorManager.translate(" &7in your inventory to add it. "),
                            "", ColorManager.translate(" &7Click the added block in "),
                            ColorManager.translate(" &7the GUI to remove it. "), "").toItemStack());
            gui.setItem(25, new ItemBuilder(Material.PISTON_BASE, 1).setName(ColorManager.translate("&aWalkable blocks")).setLore("", ColorManager.translate("&eClick to config blocks!")).toItemStack());
        } else {
            ItemStack noSetup = new ItemBuilder(Material.STAINED_GLASS_PANE, 1).setDurability((short) 14).setName(ColorManager.translate("&cParkour setup not valid")).toItemStack();
            gui.setItem(16, noSetup);
            gui.setItem(25, noSetup);
        }

        gui.setItem(40, new ItemBuilder(Material.BARRIER, 1)
                .setName(ColorManager.translate("&cParkour setup tutorial"))
                .setLore(
                        "",
                        ColorManager.translate(" &7Requirements:"),
                        ColorManager.translate("  &7- Spawn location"),
                        ColorManager.translate("  &7- Start plate location"),
                        ColorManager.translate("  &7- End plate location"),
                        "",
                        ColorManager.translate(" &7Optional:"),
                        ColorManager.translate("  &7- Stats hologram location"),
                        ColorManager.translate("  &7- Top hologram location"),
                        "",
                        ColorManager.translate("&eClick reload parkours!"))
                .toItemStack());

        guis.put(id, gui);
    }

    public void reloadAllGUI() {
        for(String id : Main.getInstance().getParkourHandler().getParkours().keySet()) {
            reloadGUI(id);
        }
    }

    public void reloadGUI(String id) {
        Inventory gui = guis.get(id);

        if(Main.getInstance().getParkourHandler().getConfig(id).contains("parkour.spawn")) {
            gui.setItem(10, new ItemBuilder(Material.INK_SACK, 1).setDurability((short) 10).setName(ColorManager.translate("&a&l[+]")).toItemStack());
            gui.setItem(19, new ItemBuilder(Material.BED, 1).setName(ColorManager.translate("&aSpawn location")).setLore("", ColorManager.translate("&eClick to relocate location!")).toItemStack());
        } else {
            gui.setItem(10, new ItemBuilder(Material.INK_SACK, 1).setDurability((short) 8).setName(ColorManager.translate("&c&l[-]")).toItemStack());
            gui.setItem(19, new ItemBuilder(Material.BED, 1).setName(ColorManager.translate("&cSpawn location")).setLore("", ColorManager.translate("&eClick to set location!")).toItemStack());
        }

        if(Main.getInstance().getParkourHandler().getConfig(id).contains("parkour.start")) {
            gui.setItem(11, new ItemBuilder(Material.INK_SACK, 1).setDurability((short) 10).setName(ColorManager.translate("&a&l[+]")).toItemStack());
            gui.setItem(20, new ItemBuilder(Material.IRON_PLATE, 1).setName(ColorManager.translate("&aStart plate location")).setLore("", ColorManager.translate("&eClick to relocate location!")).toItemStack());
        } else {
            gui.setItem(11, new ItemBuilder(Material.INK_SACK, 1).setDurability((short) 8).setName(ColorManager.translate("&c&l[-]")).toItemStack());
            gui.setItem(20, new ItemBuilder(Material.IRON_PLATE, 1).setName(ColorManager.translate("&cStart plate location")).setLore("", ColorManager.translate("&eClick to set location!")).toItemStack());
        }

        if(Main.getInstance().getParkourHandler().getConfig(id).contains("parkour.end")) {
            gui.setItem(12, new ItemBuilder(Material.INK_SACK, 1).setDurability((short) 10).setName(ColorManager.translate("&a&l[+]")).toItemStack());
            gui.setItem(21, new ItemBuilder(Material.GOLD_PLATE, 1).setName(ColorManager.translate("&aGold plate location")).setLore("", ColorManager.translate("&eClick to relocate location!")).toItemStack());
        } else {
            gui.setItem(12, new ItemBuilder(Material.INK_SACK, 1).setDurability((short) 8).setName(ColorManager.translate("&c&l[-]")).toItemStack());
            gui.setItem(21, new ItemBuilder(Material.GOLD_PLATE, 1).setName(ColorManager.translate("&cGold plate location")).setLore("", ColorManager.translate("&eClick to set location!")).toItemStack());
        }

        if(Main.getInstance().getParkourHandler().getConfig(id).contains("parkour.holograms.stats")) {
            gui.setItem(13, new ItemBuilder(Material.INK_SACK, 1).setDurability((short) 10).setName(ColorManager.translate("&a&l[+]")).toItemStack());
            gui.setItem(22, new ItemBuilder(Material.ARMOR_STAND, 1).setName(ColorManager.translate("&aStats hologram location")).setLore("", ColorManager.translate("&eClick to relocate location!")).toItemStack());
        } else {
            gui.setItem(13, new ItemBuilder(Material.INK_SACK, 1).setDurability((short) 8).setName(ColorManager.translate("&c&l[-]")).toItemStack());
            gui.setItem(22, new ItemBuilder(Material.ARMOR_STAND, 1).setName(ColorManager.translate("&cStats hologram location")).setLore("", ColorManager.translate("&eClick to set location!")).toItemStack());
        }

        if(Main.getInstance().getParkourHandler().getConfig(id).contains("parkour.holograms.top")) {
            gui.setItem(14, new ItemBuilder(Material.INK_SACK, 1).setDurability((short) 10).setName(ColorManager.translate("&a&l[+]")).toItemStack());
            gui.setItem(23, new ItemBuilder(Material.ARMOR_STAND, 1).setName(ColorManager.translate("&aTop hologram location")).setLore("", ColorManager.translate("&eClick to relocate location!")).toItemStack());
        } else {
            gui.setItem(14, new ItemBuilder(Material.INK_SACK, 1).setDurability((short) 8).setName(ColorManager.translate("&c&l[-]")).toItemStack());
            gui.setItem(23, new ItemBuilder(Material.ARMOR_STAND, 1).setName(ColorManager.translate("&cTop hologram location")).setLore("", ColorManager.translate("&eClick to set location!")).toItemStack());
        }

        if(Main.getInstance().getParkourHandler().getParkours().containsKey(id)) {
            gui.setItem(16, new ItemBuilder(Material.BOOK, 1)
                    .setName(ColorManager.translate("&aWalkable blocks tutorial"))
                    .setLore("", ColorManager.translate(" &7Open walkable blocks gui ")
                            , ColorManager.translate(" &7and click on a block "),
                            ColorManager.translate(" &7in your inventory to add it. "),
                            "", ColorManager.translate(" &7Click the added block in "),
                            ColorManager.translate(" &7the GUI to remove it. "), "").toItemStack());
            gui.setItem(25, new ItemBuilder(Material.PISTON_BASE, 1).setName(ColorManager.translate("&aWalkable blocks")).setLore("", ColorManager.translate("&eClick to config blocks!")).toItemStack());
        }

        for(HumanEntity pl : gui.getViewers()) {
            pl.getOpenInventory().getTopInventory().setContents(gui.getContents());
        }
    }

    public void open(Player p, String id) {
        p.updateInventory();

        opened.put(p.getUniqueId(), id);
        p.openInventory(guis.get(id));
    }

}