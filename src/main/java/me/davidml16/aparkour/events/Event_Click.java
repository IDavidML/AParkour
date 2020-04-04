package me.davidml16.aparkour.events;

import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.api.events.ParkourCheckpointEvent;
import me.davidml16.aparkour.api.events.ParkourReturnEvent;
import me.davidml16.aparkour.data.Parkour;
import me.davidml16.aparkour.data.Profile;
import me.davidml16.aparkour.utils.ParkourItems;
import me.davidml16.aparkour.utils.SoundUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

public class Event_Click implements Listener {

    @EventHandler
    public void onClicker(PlayerInteractEvent e) {
        Player p = e.getPlayer();

        if (Main.getInstance().getTimerManager().hasPlayerTimer(p)) {

            ItemStack item = p.getItemInHand();

            if (item != null) {

                if (item.equals(Main.getInstance().getParkourItems().getRestartItem())) {
                    if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        e.setCancelled(true);

                        Parkour parkour = Main.getInstance().getPlayerDataHandler().getData(p).getParkour();

                        p.setFlying(false);
                        p.teleport(parkour.getSpawn(), PlayerTeleportEvent.TeleportCause.UNKNOWN);

                        String message = Main.getInstance().getLanguageHandler().getMessage("Messages.Return");
                        if(message.length() > 0)
                            p.sendMessage(message);

                        Profile data = Main.getInstance().getPlayerDataHandler().getData(p);
                        data.setParkour(null);
                        data.setLastCheckpoint(-1);

                        Main.getInstance().getTimerManager().cancelTimer(p);
                        if (Main.getInstance().isParkourItemsEnabled()) {
                            Main.getInstance().getPlayerDataHandler().restorePlayerInventory(p);
                        }

                        SoundUtil.playReturn(p);

                        Bukkit.getPluginManager().callEvent(new ParkourReturnEvent(p, parkour));

                        p.setNoDamageTicks(40);
                    }
                } else if (item.equals(Main.getInstance().getParkourItems().getCheckpointItem())) {
                    if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        e.setCancelled(true);

                        Profile data = Main.getInstance().getPlayerDataHandler().getData(p);
                        Parkour parkour = Main.getInstance().getPlayerDataHandler().getData(p).getParkour();

                        if (data.getLastCheckpoint() < 0) {
                            p.teleport(parkour.getSpawn(), PlayerTeleportEvent.TeleportCause.UNKNOWN);

                            String message = Main.getInstance().getLanguageHandler().getMessage("Messages.Return");
                            if(message.length() > 0)
                                p.sendMessage(message);

                            data.setParkour(null);
                            data.setLastCheckpoint(-1);

                            Main.getInstance().getTimerManager().cancelTimer(p);
                            if (Main.getInstance().isParkourItemsEnabled()) {
                                Main.getInstance().getPlayerDataHandler().restorePlayerInventory(p);
                            }
                        } else if (data.getLastCheckpoint() >= 0) {
                            p.teleport(data.getLastCheckpointLocation(), PlayerTeleportEvent.TeleportCause.UNKNOWN);

                            String message = Main.getInstance().getLanguageHandler().getMessage("Messages.ReturnCheckpoint");
                            if(message.length() > 0)
                                p.sendMessage(message.replaceAll("%checkpoint%", Integer.toString(data.getLastCheckpoint() + 1)));
                        }

                        SoundUtil.playReturn(p);

                        p.setNoDamageTicks(40);

                        Bukkit.getPluginManager().callEvent(new ParkourReturnEvent(p, parkour));
                    }
                }
            }
        }
    }
}