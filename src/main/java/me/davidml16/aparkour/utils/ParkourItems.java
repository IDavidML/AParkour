package me.davidml16.aparkour.utils;

import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.managers.ColorManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ParkourItems {

    private ItemStack returnItem;
    private ItemStack checkpointItem;

    public void loadReturnItem() {
        int id = Main.getInstance().getConfig().getInt("Items.Restart.ID");
        String name = ColorManager.translate(Main.getInstance().getConfig().getString("Items.Restart.Name"));
        String lore = ColorManager.translate(Main.getInstance().getConfig().getString("Items.Restart.Lore"));
        returnItem = new ItemBuilder(Material.getMaterial(id), 1).setName(name).setLore(lore).toItemStack();
    }

    public void loadCheckpointItem() {
        int id = Main.getInstance().getConfig().getInt("Items.Checkpoint.ID");
        String name = ColorManager.translate(Main.getInstance().getConfig().getString("Items.Checkpoint.Name"));
        String lore = ColorManager.translate(Main.getInstance().getConfig().getString("Items.Checkpoint.Lore"));
        checkpointItem = new ItemBuilder(Material.getMaterial(id), 1).setName(name).setLore(lore).toItemStack();
    }

    public ItemStack getRestartItem() {
        return returnItem;
    }

    public ItemStack getCheckpointItem() {
        return checkpointItem;
    }

}
