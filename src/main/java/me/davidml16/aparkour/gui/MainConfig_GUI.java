package me.davidml16.aparkour.gui;

import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.conversation.RenameMenu;
import me.davidml16.aparkour.conversation.RewardMenu;
import me.davidml16.aparkour.data.Parkour;
import me.davidml16.aparkour.managers.ColorManager;
import me.davidml16.aparkour.managers.PluginManager;
import me.davidml16.aparkour.utils.ItemBuilder;
import me.davidml16.aparkour.utils.LocationUtil;
import me.davidml16.aparkour.utils.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
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
import java.util.*;

public class MainConfig_GUI implements Listener {

    private HashMap<UUID, String> opened;
    private HashMap<String, Inventory> guis;

    private Main main;

    public MainConfig_GUI(Main main) {
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

        Inventory gui = Bukkit.createInventory(null, 45, main.getLanguageHandler().getMessage("GUIs.Config.title").replaceAll("%parkour%", id));
        ItemStack edge = new ItemBuilder(Material.STAINED_GLASS_PANE, 1).setDurability((short) 7).setName("").toItemStack();

        FileConfiguration config = main.getParkourHandler().getConfig(id);

        if(config.contains("parkour.spawn")) {
            gui.setItem(10, new ItemBuilder(Material.INK_SACK, 1).setDurability((short) 10).setName(ColorManager.translate("&a&l[+]")).toItemStack());
            gui.setItem(19, new ItemBuilder(Material.BED, 1).setName(ColorManager.translate("&aSpawn location")).setLore("", ColorManager.translate("&eClick to relocate location!")).toItemStack());
        } else {
            gui.setItem(10, new ItemBuilder(Material.INK_SACK, 1).setDurability((short) 8).setName(ColorManager.translate("&c&l[-]")).toItemStack());
            gui.setItem(19, new ItemBuilder(Material.BED, 1).setName(ColorManager.translate("&cSpawn location")).setLore("", ColorManager.translate("&eClick to set location!")).toItemStack());
        }

        if(config.contains("parkour.start")) {
            gui.setItem(11, new ItemBuilder(Material.INK_SACK, 1).setDurability((short) 10).setName(ColorManager.translate("&a&l[+]")).toItemStack());
            gui.setItem(20, new ItemBuilder(Material.IRON_PLATE, 1).setName(ColorManager.translate("&aStart plate location")).setLore("", ColorManager.translate("&eClick to relocate location!")).toItemStack());
        } else {
            gui.setItem(11, new ItemBuilder(Material.INK_SACK, 1).setDurability((short) 8).setName(ColorManager.translate("&c&l[-]")).toItemStack());
            gui.setItem(20, new ItemBuilder(Material.IRON_PLATE, 1).setName(ColorManager.translate("&cStart plate location")).setLore("", ColorManager.translate("&eClick to set location!")).toItemStack());
        }

        if(config.contains("parkour.end")) {
            gui.setItem(12, new ItemBuilder(Material.INK_SACK, 1).setDurability((short) 10).setName(ColorManager.translate("&a&l[+]")).toItemStack());
            gui.setItem(21, new ItemBuilder(Material.GOLD_PLATE, 1).setName(ColorManager.translate("&aEnd plate location")).setLore("", ColorManager.translate("&eClick to relocate location!")).toItemStack());
        } else {
            gui.setItem(12, new ItemBuilder(Material.INK_SACK, 1).setDurability((short) 8).setName(ColorManager.translate("&c&l[-]")).toItemStack());
            gui.setItem(21, new ItemBuilder(Material.GOLD_PLATE, 1).setName(ColorManager.translate("&cEnd plate location")).setLore("", ColorManager.translate("&eClick to set location!")).toItemStack());
        }

        if(main.getParkourHandler().getParkours().containsKey(id)) {
            gui.setItem(14, new ItemBuilder(Material.PISTON_BASE, 1)
                    .setName(ColorManager.translate("&aWalkable blocks"))
                    .setLore(
                            "",
                            ColorManager.translate(" &7Open walkable blocks gui "),
                            ColorManager.translate(" &7and click on a block "),
                            ColorManager.translate(" &7in your inventory to add it. "),
                            "",
                            ColorManager.translate(" &7Click the added block in "),
                            ColorManager.translate(" &7the GUI to remove it. "),
                            "",
                            ColorManager.translate("&eClick to config blocks! ")
                    ).toItemStack());
            if(main.isHologramsEnabled()) {
                gui.setItem(15, new ItemBuilder(Material.ARMOR_STAND, 1)
                        .setName(ColorManager.translate("&aHolograms"))
                        .setLore(
                                "",
                                ColorManager.translate("&eClick to config holograms! ")
                        ).toItemStack());
            } else {
                gui.setItem(15, new ItemBuilder(Material.STAINED_GLASS_PANE, 1).setDurability((short) 14).setName(ColorManager.translate("&cHolograms disabled")).toItemStack());
            }
            gui.setItem(16, new ItemBuilder(Material.SIGN, 1)
                    .setName(ColorManager.translate("&aTitles"))
                    .setLore(
                            "",
                            ColorManager.translate("&eClick to config titles! ")
                    ).toItemStack());
            gui.setItem(23, new ItemBuilder(Material.BEACON, 1)
                    .setName(ColorManager.translate("&aCheckpoints"))
                    .setLore(
                            "",
                            ColorManager.translate(" &7Open checkpoints gui and "),
                            ColorManager.translate(" &7click on add checkpoints "),
                            ColorManager.translate(" &7to begin checkpoint setup. "),
                            "",
                            ColorManager.translate(" &7Click the checkpoint item "),
                            ColorManager.translate(" &7in the GUI to remove it. "),
                            "",
                            ColorManager.translate("&eClick to config checkpoints! ")
                    ).toItemStack());
            gui.setItem(24, new ItemBuilder(Material.GOLD_NUGGET, 1)
                    .setName(ColorManager.translate("&aRewards"))
                    .setLore(
                            "",
                            ColorManager.translate(" &7Open rewards gui and "),
                            ColorManager.translate(" &7click on new reward "),
                            ColorManager.translate(" &7to begin reward setup. "),
                            "",
                            ColorManager.translate(" &7Click the rewards item "),
                            ColorManager.translate(" &7in the GUI to remove it. "),
                            "",
                            ColorManager.translate("&eClick to config rewards! ")
                    ).toItemStack());
            gui.setItem(25, new ItemBuilder(Material.CHEST, 1)
                    .setName(ColorManager.translate("&aMiscellaneous"))
                    .setLore(
                            "",
                            ColorManager.translate(" &7- Parkour rename "),
                            ColorManager.translate(" &7- Parkour icon "),
                            "",
                            ColorManager.translate("&eClick to open miscellaneous! ")
                    ).toItemStack());
        } else {
            ItemStack noSetup = new ItemBuilder(Material.STAINED_GLASS_PANE, 1).setDurability((short) 14).setName(ColorManager.translate("&cParkour setup not valid")).toItemStack();
            gui.setItem(14, noSetup);
            gui.setItem(15, noSetup);
            gui.setItem(16, noSetup);
            gui.setItem(23, noSetup);
            gui.setItem(24, noSetup);
            gui.setItem(25, noSetup);
        }

        gui.setItem(40, new ItemBuilder(Material.BARRIER, 1)
                .setName(ColorManager.translate("&cParkour setup tutorial"))
                .setLore(
                        "",
                        ColorManager.translate(" &7Requirements: "),
                        ColorManager.translate("  &7- Spawn location "),
                        ColorManager.translate("  &7- Start plate location "),
                        ColorManager.translate("  &7- End plate location "),
                        "",
                        ColorManager.translate(" &7Optional: "),
                        ColorManager.translate("  &7- Stats hologram location "),
                        ColorManager.translate("  &7- Top hologram location "),
                        "",
                        ColorManager.translate("&eClick reload parkours! "))
                .toItemStack());

        for (int i = 0; i < 45; i++) {
            if(gui.getItem(i) == null) {
                gui.setItem(i, edge);
            }
        }

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

        if(config.contains("parkour.spawn")) {
            gui.setItem(10, new ItemBuilder(Material.INK_SACK, 1).setDurability((short) 10).setName(ColorManager.translate("&a&l[+]")).toItemStack());
            gui.setItem(19, new ItemBuilder(Material.BED, 1).setName(ColorManager.translate("&aSpawn location")).setLore("", ColorManager.translate("&eClick to relocate location!")).toItemStack());
        } else {
            gui.setItem(10, new ItemBuilder(Material.INK_SACK, 1).setDurability((short) 8).setName(ColorManager.translate("&c&l[-]")).toItemStack());
            gui.setItem(19, new ItemBuilder(Material.BED, 1).setName(ColorManager.translate("&cSpawn location")).setLore("", ColorManager.translate("&eClick to set location!")).toItemStack());
        }

        if(config.contains("parkour.start")) {
            gui.setItem(11, new ItemBuilder(Material.INK_SACK, 1).setDurability((short) 10).setName(ColorManager.translate("&a&l[+]")).toItemStack());
            gui.setItem(20, new ItemBuilder(Material.IRON_PLATE, 1).setName(ColorManager.translate("&aStart plate location")).setLore("", ColorManager.translate("&eClick to relocate location!")).toItemStack());
        } else {
            gui.setItem(11, new ItemBuilder(Material.INK_SACK, 1).setDurability((short) 8).setName(ColorManager.translate("&c&l[-]")).toItemStack());
            gui.setItem(20, new ItemBuilder(Material.IRON_PLATE, 1).setName(ColorManager.translate("&cStart plate location")).setLore("", ColorManager.translate("&eClick to set location!")).toItemStack());
        }

        if(config.contains("parkour.end")) {
            gui.setItem(12, new ItemBuilder(Material.INK_SACK, 1).setDurability((short) 10).setName(ColorManager.translate("&a&l[+]")).toItemStack());
            gui.setItem(21, new ItemBuilder(Material.GOLD_PLATE, 1).setName(ColorManager.translate("&aEnd plate location")).setLore("", ColorManager.translate("&eClick to relocate location!")).toItemStack());
        } else {
            gui.setItem(12, new ItemBuilder(Material.INK_SACK, 1).setDurability((short) 8).setName(ColorManager.translate("&c&l[-]")).toItemStack());
            gui.setItem(21, new ItemBuilder(Material.GOLD_PLATE, 1).setName(ColorManager.translate("&cEnd plate location")).setLore("", ColorManager.translate("&eClick to set location!")).toItemStack());
        }

        if(main.getParkourHandler().getParkours().containsKey(id)) {
            gui.setItem(14, new ItemBuilder(Material.PISTON_BASE, 1)
                    .setName(ColorManager.translate("&aWalkable blocks"))
                    .setLore(
                            "",
                            ColorManager.translate(" &7Open walkable blocks gui "),
                            ColorManager.translate(" &7and click on a block "),
                            ColorManager.translate(" &7in your inventory to add it. "),
                            "",
                            ColorManager.translate(" &7Click the added block in "),
                            ColorManager.translate(" &7the GUI to remove it. "),
                            "",
                            ColorManager.translate("&eClick to config blocks! ")
                    ).toItemStack());
            if(main.isHologramsEnabled()) {
                gui.setItem(15, new ItemBuilder(Material.ARMOR_STAND, 1)
                        .setName(ColorManager.translate("&aHolograms"))
                        .setLore(
                                "",
                                ColorManager.translate("&eClick to config holograms! ")
                        ).toItemStack());
            } else {
                gui.setItem(15, new ItemBuilder(Material.STAINED_GLASS_PANE, 1).setDurability((short) 14).setName(ColorManager.translate("&cHolograms disabled")).toItemStack());
            }
            gui.setItem(16, new ItemBuilder(Material.SIGN, 1)
                    .setName(ColorManager.translate("&aTitles"))
                    .setLore(
                            "",
                            ColorManager.translate("&eClick to config titles! ")
                    ).toItemStack());
            gui.setItem(23, new ItemBuilder(Material.BEACON, 1)
                    .setName(ColorManager.translate("&aCheckpoints"))
                    .setLore(
                            "",
                            ColorManager.translate(" &7Open checkpoints gui and "),
                            ColorManager.translate(" &7click on add checkpoints "),
                            ColorManager.translate(" &7to begin checkpoint setup. "),
                            "",
                            ColorManager.translate(" &7Click the checkpoint item "),
                            ColorManager.translate(" &7in the GUI to remove it. "),
                            "",
                            ColorManager.translate("&eClick to config checkpoints! ")
                    ).toItemStack());
            gui.setItem(24, new ItemBuilder(Material.GOLD_NUGGET, 1)
                    .setName(ColorManager.translate("&aRewards"))
                    .setLore(
                            "",
                            ColorManager.translate(" &7Open rewards gui and "),
                            ColorManager.translate(" &7click on new reward "),
                            ColorManager.translate(" &7to begin reward setup. "),
                            "",
                            ColorManager.translate(" &7Click the rewards item "),
                            ColorManager.translate(" &7in the GUI to remove it. "),
                            "",
                            ColorManager.translate("&eClick to config rewards! ")
                    ).toItemStack());
            gui.setItem(25, new ItemBuilder(Material.CHEST, 1)
                    .setName(ColorManager.translate("&aMiscellaneous"))
                    .setLore(
                            "",
                            ColorManager.translate(" &7- Parkour rename "),
                            ColorManager.translate(" &7- Parkour icon "),
                            "",
                            ColorManager.translate("&eClick to open miscellaneous! ")
                    ).toItemStack());
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
            if ((slot >= 19 && slot <= 21)) {
                changeParkourConfig(p, slot);
            } else if (slot == 14 && e.getCurrentItem().getType() == Material.PISTON_BASE) {
                String id = opened.get(p.getUniqueId());
                opened.remove(p.getUniqueId());
                main.getWalkableBlocksGUI().open(p, id);
            } else if (slot == 15 && e.getCurrentItem().getType() == Material.ARMOR_STAND) {
                String id = opened.get(p.getUniqueId());
                main.getHologramsGUI().open(p, id);
            } else if (slot == 16 && e.getCurrentItem().getType() == Material.SIGN) {
                String id = opened.get(p.getUniqueId());
                main.getTitlesGUI().open(p, id);
            } else if (slot == 23 && e.getCurrentItem().getType() == Material.BEACON) {
                String id = opened.get(p.getUniqueId());
                main.getCheckpointsGUI().open(p, id);
            } else if (slot == 24 && e.getCurrentItem().getType() == Material.GOLD_NUGGET) {
                String id = opened.get(p.getUniqueId());
                main.getRewardsGUI().open(p, id);
            } else if (slot == 25 && e.getCurrentItem().getType() == Material.CHEST) {
                String id = opened.get(p.getUniqueId());
                main.getMiscellaneousGUI().open(p, id);
            } else if (slot == 40) {
                if (e.getCurrentItem().getType() == Material.BARRIER) {
                    main.getPluginManager().reloadAll();
                    Sounds.playSound(p, p.getLocation(), Sounds.MySound.ANVIL_USE, 100, 3);
                    String message = main.getLanguageHandler().getMessage("Commands.Reload");
                    if(message.length() > 0)
                        p.sendMessage(message);
                }
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
                main.getLocationUtil().setPosition(p, id, "spawn");
                break;
            case 20:
                main.getLocationUtil().setPosition(p, id, "start");
                break;
            case 21:
                main.getLocationUtil().setPosition(p, id, "end");
                break;
            default:
                break;
        }

        Sounds.playSound(p, p.getLocation(), Sounds.MySound.CLICK, 100, 3);

        reloadGUI(id);
    }

}