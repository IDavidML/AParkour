package me.davidml16.aparkour.gui;

import me.davidml16.aparkour.Main;
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
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.*;

public class WalkableBlocks_GUI implements Listener {

    private HashMap<UUID, String> opened;
    private HashMap<String, Inventory> guis;
    private List<Integer> borders;

    private Main main;

    public WalkableBlocks_GUI(Main main) {
        this.main = main;
        this.opened = new HashMap<UUID, String>();
        this.guis = new HashMap<String, Inventory>();
        this.borders = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 37, 38, 39, 41, 42, 43, 44);
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
        if (guis.containsKey(id)) return;

        Inventory gui = Bukkit.createInventory(null, 45, main.getLanguageHandler().getMessage("GUIs.WalkableBlocks.title").replaceAll("%parkour%", id));

        ItemStack edge = new ItemBuilder(Material.STAINED_GLASS_PANE, 1).setDurability((short) 7).setName("").toItemStack();
        ItemStack back = new ItemBuilder(Material.ARROW, 1).setName(ColorManager.translate("&aBack to config")).toItemStack();

        for (Integer i : borders) {
            gui.setItem(i, edge);
        }

        List<WalkableBlock> walkable;
        if (main.getParkourHandler().getParkours().containsKey(id))
            walkable = main.getParkourHandler().getParkourById(id).getWalkableBlocks();
        else
            walkable = main.getParkourHandler().getWalkableBlocks(id);

        if(walkable.size() > 0) {
            for (WalkableBlock block : walkable) {
                String name = Material.getMaterial(block.getId()).name().replaceAll("_", " ");
                byte data = block.getData();
                gui.addItem(new ItemBuilder(Material.getMaterial(block.getId()), 1, data).setName(ColorManager.translate("&a" + name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase())).setLore("", ColorManager.translate("&eClick to remove!")).toItemStack());
            }
        } else {
            gui.setItem(22, new ItemBuilder(Material.STAINED_GLASS_PANE, 1).setDurability((short) 14).setName(ColorManager.translate("&cAny walkable block selected")).setLore(
                    "",
                    ColorManager.translate(" &7You dont have any "),
                    ColorManager.translate(" &7walkable block selected. "),
                    "",
                    ColorManager.translate(" &7Now you can walk in any "),
                    ColorManager.translate(" &7block, you will not fail. "),
                    ""
            ).toItemStack());
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

        for (int i = 10; i <= 16; i++)
            gui.setItem(i, null);
        for (int i = 19; i <= 25; i++)
            gui.setItem(i, null);
        for (int i = 28; i <= 34; i++)
            gui.setItem(i, null);

        List<WalkableBlock> walkable = main.getParkourHandler().getParkourById(id).getWalkableBlocks();
        if(walkable.size() > 0) {
            for (WalkableBlock block : walkable) {
                String name = Material.getMaterial(block.getId()).name().replaceAll("_", " ");
                byte data = block.getData();
                gui.addItem(new ItemBuilder(Material.getMaterial(block.getId()), 1, data).setName(ColorManager.translate("&a" + name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase())).setLore("", ColorManager.translate("&eClick to remove!")).toItemStack());
            }
        } else {
            gui.setItem(22, new ItemBuilder(Material.STAINED_GLASS_PANE, 1).setDurability((short) 14).setName(ColorManager.translate("&cAny walkable block selected")).setLore(
                    "",
                    ColorManager.translate(" &7You dont have any "),
                    ColorManager.translate(" &7walkable block selected. "),
                    "",
                    ColorManager.translate(" &7Now you can walk in any "),
                    ColorManager.translate(" &7block, you will not fail. "),
                    ""
            ).toItemStack());
        }

        for (HumanEntity pl : gui.getViewers()) {
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
            Parkour parkour = main.getParkourHandler().getParkourById(id);
            if (slot == 40) {
                p.closeInventory();
                main.getConfigGUI().open(p, id);
            } else if (slot >= 45 && slot <= 80) {
                List<WalkableBlock> walkable = parkour.getWalkableBlocks();
                if (walkable.size() < 21) {

                    if (e.getCurrentItem().getType() == Material.AIR) return;

                    int itemId = e.getCurrentItem().getTypeId();
                    byte data = e.getCurrentItem().getData().getData();

                    if (e.getCurrentItem().getType().name().contains("PLATE")) {
                        Sounds.playSound(p, p.getLocation(), Sounds.MySound.NOTE_PLING, 10, 0);
                    } else {
                        if (WalkableBlocksUtil.noContainsWalkable(walkable, itemId, data)) {
                            p.sendMessage(ColorManager.translate(main.getLanguageHandler().getPrefix()
                                    + " &aYou added &e" + e.getCurrentItem().getType().name() + " &ato walkable blocks of parkour &e" + id));
                            WalkableBlock walkableBlock = new WalkableBlock(itemId, data);
                            walkable.add(walkableBlock);
                            parkour.setWalkableBlocks(walkable);
                            reloadGUI(id);
                            Sounds.playSound(p, p.getLocation(), Sounds.MySound.CLICK, 10, 2);
                        } else {
                            p.sendMessage(ColorManager.translate(main.getLanguageHandler().getPrefix()
                                    + " &cThe block &e" + e.getCurrentItem().getType().name() + " &calready exists in walkable blocks of parkour &e" + id));
                            Sounds.playSound(p, p.getLocation(), Sounds.MySound.NOTE_PLING, 10, 0);
                        }
                    }
                }
            } else if ((slot >= 10 && slot <= 16) || (slot >= 19 && slot <= 25) || (slot >= 28 && slot <= 34)) {
                if (e.getCurrentItem().getType() == Material.AIR) return;

                if (parkour.getWalkableBlocks().size() == 0) return;

                int itemId = e.getCurrentItem().getTypeId();
                byte data = e.getCurrentItem().getData().getData();

                p.sendMessage(ColorManager.translate(main.getLanguageHandler().getPrefix()
                        + " &aYou removed &e" + e.getCurrentItem().getType().name() + " &afrom walkable blocks of parkour &e" + id));
                List<WalkableBlock> walkable = parkour.getWalkableBlocks();
                WalkableBlock walkableBlock = WalkableBlocksUtil.getWalkableBlock(walkable, itemId, data);
                walkable.remove(walkableBlock);
                parkour.setWalkableBlocks(walkable);
                reloadGUI(id);
                Sounds.playSound(p, p.getLocation(), Sounds.MySound.CLICK, 10, 2);
            }
        }
    }

    @EventHandler
    public void InventoryCloseEvent(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        if (opened.containsKey(p.getUniqueId())) {
            main.getParkourHandler().getParkours().get(opened.get(p.getUniqueId())).saveParkour();
            opened.remove(p.getUniqueId());
        }
    }

}
