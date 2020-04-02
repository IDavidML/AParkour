package me.davidml16.aparkour.gui;

import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.conversation.RewardMenu;
import me.davidml16.aparkour.data.Parkour;
import me.davidml16.aparkour.data.Reward;
import me.davidml16.aparkour.data.WalkableBlock;
import me.davidml16.aparkour.managers.ColorManager;
import me.davidml16.aparkour.utils.ItemBuilder;
import me.davidml16.aparkour.utils.Sounds;
import me.davidml16.aparkour.utils.WalkableBlocksUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
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

public class Rewards_GUI implements Listener {

    private HashMap<UUID, String> opened;
    private HashMap<String, Inventory> guis;
    private List<Integer> borders;

    public Rewards_GUI() {
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

        Inventory gui = Bukkit.createInventory(null, 45, Main.getInstance().getLanguageHandler().getMessage("GUIs.Rewards.title").replaceAll("%parkour%", id));

        ItemStack edge = new ItemBuilder(Material.STAINED_GLASS_PANE, 1).setDurability((short) 7).setName("").toItemStack();
        ItemStack newReward = new ItemBuilder(Material.DOUBLE_PLANT, 1).setName(ColorManager.translate("&aCreate new reward")).toItemStack();
        ItemStack back = new ItemBuilder(Material.ARROW, 1).setName(ColorManager.translate("&aBack to config")).toItemStack();

        for (Integer i : borders) {
            gui.setItem(i, edge);
        }

        List<Reward> rewards;
        if (Main.getInstance().getParkourHandler().getParkours().containsKey(id))
            rewards = Main.getInstance().getParkourHandler().getParkourById(id).getRewards();
        else
            rewards = Main.getInstance().getParkourHandler().getRewards(id);

        if(rewards.size() > 0) {
            for (Reward reward : rewards) {
                gui.addItem(new ItemBuilder(Material.GOLD_NUGGET, 1)
                        .setName(ColorManager.translate("&a" + reward.getId()))
                        .setLore(
                                "",
                                ColorManager.translate(" &7First time: &6" + reward.isFirstTime() + " "),
                                ColorManager.translate(" &7Permission: &6" + reward.getPermission() + " "),
                                ColorManager.translate(" &7Command: &6" + reward.getCommand() + " "),
                                "",
                                ColorManager.translate("&eClick to remove! ")).toItemStack());
            }
        } else {
            gui.setItem(22, new ItemBuilder(Material.STAINED_GLASS_PANE, 1).setDurability((short) 14).setName(ColorManager.translate("&cAny rewards selected")).setLore(
                    "",
                    ColorManager.translate(" &7You dont have any "),
                    ColorManager.translate(" &7reward selected. "),
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

        List<Reward> rewards = Main.getInstance().getParkourHandler().getParkourById(id).getRewards();
        if(rewards.size() > 0) {
            for (Reward reward : rewards) {
                gui.addItem(new ItemBuilder(Material.GOLD_NUGGET, 1)
                        .setName(ColorManager.translate("&a" + reward.getId()))
                        .setLore(
                                "",
                                ColorManager.translate(" &7First time: &6" + reward.isFirstTime() + " "),
                                ColorManager.translate(" &7Permission: &6" + reward.getPermission() + " "),
                                ColorManager.translate(" &7Command: &6" + reward.getCommand() + " "),
                                "",
                                ColorManager.translate("&eClick to remove! ")).toItemStack());
            }
        } else {
            gui.setItem(22, new ItemBuilder(Material.STAINED_GLASS_PANE, 1).setDurability((short) 14).setName(ColorManager.translate("&cAny rewards selected")).setLore(
                    "",
                    ColorManager.translate(" &7You dont have any "),
                    ColorManager.translate(" &7reward selected. "),
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
            String id = opened.get(p.getUniqueId());
            if (slot == 39) {
                p.closeInventory();
                new RewardMenu().getConversation(p, id).begin();
                Sounds.playSound(p, p.getLocation(), Sounds.MySound.ANVIL_USE, 100, 3);
            } else if (slot == 41) {
                if (Main.getInstance().getParkourHandler().getParkourById(id).getRewards().size() < 21) {
                    p.closeInventory();
                    Main.getInstance().getConfigGUI().open(p, id);
                }
            } else if ((slot >= 10 && slot <= 16) || (slot >= 19 && slot <= 25) || (slot >= 28 && slot <= 34)) {
                if (e.getCurrentItem().getType() == Material.AIR) return;

                if (Main.getInstance().getParkourHandler().getParkourById(id).getRewards().size() == 0) return;

                String rewardID = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
                Parkour parkour = Main.getInstance().getParkourHandler().getParkourById(id);
                for(Reward reward : parkour.getRewards()) {
                    if(reward.getId().equalsIgnoreCase(rewardID)) {
                        parkour.getRewards().remove(reward);
                        p.sendMessage(ColorManager.translate(Main.getInstance().getLanguageHandler().getPrefix()
                                + " &aYou removed reward &e" + rewardID + " &afrom rewards of parkour &e" + id));
                        reloadGUI(id);
                        Sounds.playSound(p, p.getLocation(), Sounds.MySound.CLICK, 10, 2);
                        break;
                    }
                }
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
