package me.davidml16.aparkour.gui;

import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.conversation.CheckpointMenu;
import me.davidml16.aparkour.conversation.RewardMenu;
import me.davidml16.aparkour.data.Parkour;
import me.davidml16.aparkour.data.Plate;
import me.davidml16.aparkour.data.Reward;
import me.davidml16.aparkour.data.WalkableBlock;
import me.davidml16.aparkour.managers.ColorManager;
import me.davidml16.aparkour.utils.ItemBuilder;
import me.davidml16.aparkour.utils.Sounds;
import me.davidml16.aparkour.utils.WalkableBlocksUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

public class Checkpoints_GUI implements Listener {

    private HashMap<UUID, String> opened;
    private HashMap<String, Inventory> guis;
    private List<Integer> borders;

    public Checkpoints_GUI() {
        this.opened = new HashMap<UUID, String>();
        this.guis = new HashMap<String, Inventory>();
        this.borders = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 37, 38, 40, 42, 43, 44);
        Main.getInstance().getServer().getPluginManager().registerEvents(this, Main.getInstance());
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
        if (guis.containsKey(id)) return;

        Inventory gui = Bukkit.createInventory(null, 45, Main.getInstance().getLanguageHandler().getMessage("GUIs.Checkpoints.title").replaceAll("%parkour%", id));

        ItemStack edge = new ItemBuilder(Material.STAINED_GLASS_PANE, 1).setDurability((short) 7).setName("").toItemStack();
        ItemStack newReward = new ItemBuilder(Material.DOUBLE_PLANT, 1).setName(ColorManager.translate("&aEdit checkpoints")).toItemStack();
        ItemStack back = new ItemBuilder(Material.ARROW, 1).setName(ColorManager.translate("&aBack to config")).toItemStack();

        for (Integer i : borders) {
            gui.setItem(i, edge);
        }

        List<Plate> checkpoints;
        if (Main.getInstance().getParkourHandler().getParkours().containsKey(id))
            checkpoints = Main.getInstance().getParkourHandler().getParkourById(id).getCheckpoints();
        else
            checkpoints = Main.getInstance().getParkourHandler().getCheckpoints(id);

        if(checkpoints.size() > 0) {
            int iterator = 1;
            for (Plate checkpoint : checkpoints) {
                Location loc = checkpoint.getLocation();
                gui.addItem(new ItemBuilder(Material.BEACON, 1)
                        .setName(ColorManager.translate("&aCheckpoint &e#" + iterator))
                        .setLore(
                                "",
                                ColorManager.translate(" &7X: &6" + loc.getBlockX() + " "),
                                ColorManager.translate(" &7Y: &6" + loc.getBlockY() + " "),
                                ColorManager.translate(" &7Z: &6" + loc.getBlockZ() + " "),
                                "",
                                ColorManager.translate("&eClick to remove! ")).toItemStack());
                iterator++;
            }
        } else {
            gui.setItem(22, new ItemBuilder(Material.STAINED_GLASS_PANE, 1).setDurability((short) 14).setName(ColorManager.translate("&cAny checkpoints selected")).setLore(
                    "",
                    ColorManager.translate(" &7You dont have any "),
                    ColorManager.translate(" &7checkpoints selected. "),
                    ""
            ).toItemStack());
        }

        gui.setItem(39, newReward);
        gui.setItem(41, back);

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

        List<Plate> checkpoints = Main.getInstance().getParkourHandler().getParkourById(id).getCheckpoints();
        if(checkpoints.size() > 0) {
            int iterator = 1;
            for (Plate checkpoint : checkpoints) {
                Location loc = checkpoint.getLocation();
                gui.addItem(new ItemBuilder(Material.BEACON, 1)
                        .setName(ColorManager.translate("&aCheckpoint &e#" + iterator))
                        .setLore(
                                "",
                                ColorManager.translate(" &7X: &6" + loc.getBlockX() + " "),
                                ColorManager.translate(" &7Y: &6" + loc.getBlockY() + " "),
                                ColorManager.translate(" &7Z: &6" + loc.getBlockZ() + " "),
                                "",
                                ColorManager.translate("&eClick to remove! ")).toItemStack());
                iterator++;
            }
        } else {
            gui.setItem(22, new ItemBuilder(Material.STAINED_GLASS_PANE, 1).setDurability((short) 14).setName(ColorManager.translate("&cAny checkpoints selected")).setLore(
                    "",
                    ColorManager.translate(" &7You dont have any "),
                    ColorManager.translate(" &7checkpoints selected. "),
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

        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
            public void run() {
                opened.put(p.getUniqueId(), id);
            }
        }, 1L);
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        if (e.getCurrentItem() == null) return;

        if (opened.containsKey(p.getUniqueId())) {
            e.setCancelled(true);
            int slot = e.getRawSlot();
            Parkour parkour = Main.getInstance().getParkourHandler().getParkourById(opened.get(p.getUniqueId()));
            if (slot == 39) {
                p.closeInventory();
                new CheckpointMenu().getConversation(p, parkour).begin();
                Sounds.playSound(p, p.getLocation(), Sounds.MySound.ANVIL_USE, 100, 3);
            } else if (slot == 41) {
                if (parkour.getRewards().size() < 21) {
                    p.closeInventory();
                    Main.getInstance().getConfigGUI().open(p, parkour.getId());
                }
            } else if ((slot >= 10 && slot <= 16) || (slot >= 19 && slot <= 25) || (slot >= 28 && slot <= 34)) {
                if (e.getCurrentItem().getType() == Material.AIR) return;

                if (parkour.getCheckpoints().size() == 0) return;

                Integer checkpointID = Integer.parseInt(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).replaceAll("Checkpoint #", ""));
                Main.getInstance().getParkourHandler().removeCheckpointHolograms(parkour);
                parkour.getCheckpoints().get(checkpointID - 1).getHologram().delete();
                parkour.getCheckpoints().remove(checkpointID - 1);
                parkour.getCheckpointLocations().remove(checkpointID - 1);
                Main.getInstance().getParkourHandler().loadCheckpointHolograms(parkour);
                p.sendMessage(ColorManager.translate(Main.getInstance().getLanguageHandler().getPrefix()
                        + " &aYou removed checkpoint &e#" + checkpointID + " &afrom checkpoints of parkour &e" + parkour.getId()));
                reloadGUI(parkour.getId());
                Sounds.playSound(p, p.getLocation(), Sounds.MySound.CLICK, 10, 2);
            }
        }
    }

    @EventHandler
    public void InventoryCloseEvent(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        if (opened.containsKey(p.getUniqueId())) {
            Main.getInstance().getParkourHandler().getParkours().get(opened.get(p.getUniqueId())).saveParkour();
            opened.remove(p.getUniqueId());
        }
    }

}
