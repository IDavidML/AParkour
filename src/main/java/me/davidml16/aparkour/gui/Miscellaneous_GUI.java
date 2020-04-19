package me.davidml16.aparkour.gui;

import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.conversation.RenameMenu;
import me.davidml16.aparkour.data.Parkour;
import me.davidml16.aparkour.data.WalkableBlock;
import me.davidml16.aparkour.managers.ColorManager;
import me.davidml16.aparkour.utils.ItemBuilder;
import me.davidml16.aparkour.utils.Sounds;
import me.davidml16.aparkour.utils.WalkableBlocksUtil;
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
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Miscellaneous_GUI implements Listener {

    private HashMap<UUID, String> opened;
    private HashMap<String, Inventory> guis;

    private Main main;

    public Miscellaneous_GUI(Main main) {
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

        Inventory gui = Bukkit.createInventory(null, 45, main.getLanguageHandler().getMessage("GUIs.Misc.title").replaceAll("%parkour%", id));
        ItemStack edge = new ItemBuilder(Material.STAINED_GLASS_PANE, 1).setDurability((short) 7).setName("").toItemStack();
        ItemStack back = new ItemBuilder(Material.ARROW, 1).setName(ColorManager.translate("&aBack to config")).toItemStack();

        FileConfiguration config = main.getParkourHandler().getConfig(id);

        gui.setItem(11, new ItemBuilder(Material.NAME_TAG, 1).setName(ColorManager.translate("&aParkour icon"))
                .setLore(
                        "",
                        ColorManager.translate(" &7Click in a item of your "),
                        ColorManager.translate(" &7inventory to set it "),
                        ColorManager.translate(" &7to parkour icon. "),
                        "",
                        ColorManager.translate(" &7Click on the icon on gui "),
                        ColorManager.translate(" &7to set to the default icon. "),
                        ""
                )
                .toItemStack());

        int itemID = Integer.parseInt(config.getString("parkour.icon").split(":")[0]);
        byte itemData = Byte.parseByte(config.getString("parkour.icon").split(":")[1]);
        String name = Material.getMaterial(itemID).name().replaceAll("_", " ");

        if(itemID != 389) {
            gui.setItem(20, new ItemBuilder(Material.getMaterial(itemID), 1, itemData).setName(ColorManager.translate("&a" + name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase())).setLore("", ColorManager.translate("&eClick to remove!")).toItemStack());
        } else {
            gui.setItem(20, new ItemBuilder(Material.getMaterial(itemID), 1, itemData).setName(ColorManager.translate("&c" + name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase())).setLore("", ColorManager.translate("&eDefault parkour icon!")).toItemStack());
        }

        gui.setItem(15, new ItemBuilder(Material.NAME_TAG, 1).setName(ColorManager.translate("&aParkour name"))
                .setLore(
                        "",
                        ColorManager.translate(" &7Click on the anvil "),
                        ColorManager.translate(" &7to start rename menu "),
                        "",
                        ColorManager.translate(" &7Choose 1 to rename parkour "),
                        ColorManager.translate(" &7Choose 2 to save and exit menu. "),
                        ""
                )
                .toItemStack());
        gui.setItem(24,  new ItemBuilder(Material.ANVIL, 1)
                .setName(ColorManager.translate("&aRename parkour"))
                .setLore(
                        "",
                        ColorManager.translate("&eClick to rename parkour! ")
                ).toItemStack());

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

        gui.setItem(11, new ItemBuilder(Material.NAME_TAG, 1).setName(ColorManager.translate("&aParkour icon"))
                .setLore(
                        "",
                        ColorManager.translate(" &7Click in a item of your "),
                        ColorManager.translate(" &7inventory to set it "),
                        ColorManager.translate(" &7to parkour icon. "),
                        "",
                        ColorManager.translate(" &7Click on the icon on gui "),
                        ColorManager.translate(" &7to set to the default icon. "),
                        ""
                )
                .toItemStack());

        int itemID;
        byte itemData;
        String name;

        if(config.contains("parkour.icon")) {
            itemID = Integer.parseInt(config.getString("parkour.icon").split(":")[0]);
            itemData = Byte.parseByte(config.getString("parkour.icon").split(":")[1]);
            name = Material.getMaterial(itemID).name().replaceAll("_", " ");
        } else {
            itemID = 389;
            itemData = 0;
            name = Material.getMaterial(itemID).name().replaceAll("_", " ");
        }

        if(itemID != 389) {
            gui.setItem(20, new ItemBuilder(Material.getMaterial(itemID), 1, itemData).setName(ColorManager.translate("&a" + name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase())).setLore("", ColorManager.translate("&eClick to remove!")).toItemStack());
        } else {
            gui.setItem(20, new ItemBuilder(Material.getMaterial(itemID), 1, itemData).setName(ColorManager.translate("&c" + name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase())).setLore("", ColorManager.translate("&eDefault parkour icon!")).toItemStack());
        }

        gui.setItem(15, new ItemBuilder(Material.NAME_TAG, 1).setName(ColorManager.translate("&aParkour name"))
                .setLore(
                        "",
                        ColorManager.translate(" &7Click on the anvil "),
                        ColorManager.translate(" &7to start rename menu "),
                        "",
                        ColorManager.translate(" &7Choose 1 to rename parkour "),
                        ColorManager.translate(" &7Choose 2 to save and exit menu. "),
                        ""
                )
                .toItemStack());
        gui.setItem(24,  new ItemBuilder(Material.ANVIL, 1)
                .setName(ColorManager.translate("&aRename parkour"))
                .setLore(
                        "",
                        ColorManager.translate("&eClick to rename parkour! ")
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
            String id = opened.get(p.getUniqueId());

            if (slot == 20) {
                if (e.getCurrentItem().getType() != Material.ITEM_FRAME) {
                    FileConfiguration config = main.getParkourHandler().getConfig(id);
                    config.set("parkour.icon", "389:0");
                    p.sendMessage(ColorManager.translate(main.getLanguageHandler().getPrefix()
                            + " &cChanged icon of parkour &e" + id + " &cto default!"));
                    main.getParkourHandler().getParkourById(id).setIcon(new ItemBuilder(Material.getMaterial(389), 1).setDurability((short) 0).toItemStack());
                    main.getParkourHandler().saveConfig(id);
                    Sounds.playSound(p, p.getLocation(), Sounds.MySound.CLICK, 100, 3);
                    reloadGUI(id);
                } else {
                    p.sendMessage(ColorManager.translate(main.getLanguageHandler().getPrefix()
                            + " &cItem frame is the default icon of parkours!"));
                    Sounds.playSound(p, p.getLocation(), Sounds.MySound.NOTE_PLING, 10, 0);
                }
            } else if (slot == 24 && e.getCurrentItem().getType() == Material.ANVIL) {
                Parkour parkour = main.getParkourHandler().getParkourById(id);
                p.closeInventory();
                new RenameMenu(main).getConversation(p, parkour).begin();
                Sounds.playSound(p, p.getLocation(), Sounds.MySound.ANVIL_USE, 100, 3);
            } else if (slot == 40) {
                main.getConfigGUI().open(p, id);
            } else if (slot >= 45 && slot <= 80) {
                if (e.getCurrentItem().getType() == Material.AIR) return;

                FileConfiguration config = main.getParkourHandler().getConfig(id);

                int itemID = e.getCurrentItem().getTypeId();
                byte itemData = e.getCurrentItem().getData().getData();

                config.set("parkour.icon", itemID + ":" + itemData);
                main.getParkourHandler().getParkourById(id).setIcon(new ItemBuilder(Material.getMaterial(itemID), 1).setDurability(itemData).toItemStack());

                p.sendMessage(ColorManager.translate(main.getLanguageHandler().getPrefix()
                        + " &aChanged icon of parkour &e" + id + " &ato &e" + e.getCurrentItem().getType().name()));
                Sounds.playSound(p, p.getLocation(), Sounds.MySound.CLICK, 100, 3);
                reloadGUI(id);
            }
        }
    }

    @EventHandler
    public void InventoryCloseEvent(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        opened.remove(p.getUniqueId());
    }

}