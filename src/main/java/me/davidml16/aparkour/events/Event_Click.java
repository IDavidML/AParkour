package me.davidml16.aparkour.events;

import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.api.events.ParkourCheckpointEvent;
import me.davidml16.aparkour.api.events.ParkourReturnEvent;
import me.davidml16.aparkour.data.Parkour;
import me.davidml16.aparkour.data.ParkourSession;
import me.davidml16.aparkour.data.Profile;
import me.davidml16.aparkour.utils.ParkourItems;
import me.davidml16.aparkour.utils.SoundUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class Event_Click implements Listener {

    private Main main;
    public Event_Click(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onClicker(PlayerInteractEvent e) {
        Player p = e.getPlayer();

        if (main.getTimerManager().hasPlayerTimer(p)) {

            ItemStack item = p.getItemInHand();

            if (item != null) {

                ParkourSession session = main.getSessionHandler().getSession(p);

                if (item.equals(main.getParkourItems().getRestartItem())) {
                    if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        e.setCancelled(true);

                        p.teleport(session.getParkour().getSpawn());

                        String message = main.getLanguageHandler().getMessage("Messages.Return");
                        if(message.length() > 0)
                            p.sendMessage(message);

                        main.getParkourHandler().resetPlayer(p);

                        main.getSoundUtil().playReturn(p);

                        Bukkit.getPluginManager().callEvent(new ParkourReturnEvent(p, session.getParkour()));
                    }
                } else if (item.equals(main.getParkourItems().getCheckpointItem())) {
                    if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        e.setCancelled(true);

                        if (session.getLastCheckpoint() < 0) {
                            String message = main.getLanguageHandler().getMessage("Messages.Return");
                            if(message.length() > 0)
                                p.sendMessage(message);

                            if(main.isKickParkourOnFail()) {
                                main.getParkourHandler().resetPlayer(p);
                                p.teleport(session.getParkour().getSpawn());
                            } else {
                                Location loc = session.getParkour().getStart().getLocation().clone();
                                loc.add(0.5, 0, 0.5);
                                loc.setPitch(p.getLocation().getPitch());
                                loc.setYaw(p.getLocation().getYaw());
                                p.teleport(loc);
                            }
                        } else if (session.getLastCheckpoint() >= 0) {
                            p.teleport(session.getLastCheckpointLocation());

                            String message = main.getLanguageHandler().getMessage("Messages.ReturnCheckpoint");
                            if(message.length() > 0)
                                p.sendMessage(message.replaceAll("%checkpoint%", Integer.toString(session.getLastCheckpoint() + 1)));
                        }

                        main.getSoundUtil().playReturn(p);

                        p.setFallDistance(0);
                        p.setNoDamageTicks(40);

                        Bukkit.getPluginManager().callEvent(new ParkourReturnEvent(p, session.getParkour()));
                    }
                }
            }
        }
    }
}