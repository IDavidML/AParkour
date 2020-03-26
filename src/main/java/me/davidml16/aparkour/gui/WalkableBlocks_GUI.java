package me.davidml16.aparkour.gui;

import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.data.WalkableBlock;
import me.davidml16.aparkour.managers.ColorManager;
import me.davidml16.aparkour.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class WalkableBlocks_GUI {

    private HashMap<UUID, String> opened;
    private HashMap<String, Inventory> guis;
    private List<Integer> borders;

    public WalkableBlocks_GUI() {
        this.opened = new HashMap<UUID, String>();
        this.guis = new HashMap<String, Inventory>();
        this.borders = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 37, 38, 39, 41, 42, 43, 44);
    }

    public HashMap<UUID, String> getOpened() {
        return opened;
    }

    public HashMap<String, Inventory> getGuis() {
        return guis;
    }

    public void loadGUI() {
        for (String id : Main.getInstance().getParkourHandler().getConfig().getConfigurationSection("parkours").getKeys(false)) {
            loadGUI(id);
        }
    }

    public void loadGUI(String id) {
        if (guis.containsKey(id)) return;

        Inventory gui = Bukkit.createInventory(null, 45, Main.getInstance().getLanguageHandler().getMessage("GUI_WB_TITLE", false).replaceAll("%parkour%", id));

        ItemStack edge = new ItemBuilder(Material.STAINED_GLASS_PANE, 1).setDurability((short) 7).setName("").toItemStack();
        ItemStack back = new ItemBuilder(Material.ARROW, 1).setName(ColorManager.translate("&aBack to config")).toItemStack();

        for (Integer i : borders) {
            gui.setItem(i, edge);
        }

        List<WalkableBlock> walkable;
        if (Main.getInstance().getParkourHandler().getParkours().containsKey(id))
            walkable = Main.getInstance().getParkourHandler().getParkourById(id).getWalkableBlocks();
        else
            walkable = Main.getInstance().getParkourHandler().getWalkableBlocks(id);

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
        for(String id : Main.getInstance().getParkourHandler().getParkours().keySet()) {
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

        List<WalkableBlock> walkable = Main.getInstance().getParkourHandler().getParkourById(id).getWalkableBlocks();
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

        opened.put(p.getUniqueId(), id);
        p.openInventory(guis.get(id));
    }

}
