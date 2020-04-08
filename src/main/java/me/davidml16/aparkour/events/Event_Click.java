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

                if (item.equals(main.getParkourItems().getRestartItem())) {
                    if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        e.setCancelled(true);

                        Parkour parkour = main.getPlayerDataHandler().getData(p).getParkour();

                        p.teleport(parkour.getSpawn());

                        String message = main.getLanguageHandler().getMessage("Messages.Return");
                        if(message.length() > 0)
                            p.sendMessage(message);

                        main.getParkourHandler().resetPlayer(p);

                        main.getSoundUtil().playReturn(p);

                        Bukkit.getPluginManager().callEvent(new ParkourReturnEvent(p, parkour));


                    }
                } else if (item.equals(main.getParkourItems().getCheckpointItem())) {
                    if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        e.setCancelled(true);

                        Profile data = main.getPlayerDataHandler().getData(p);
                        Parkour parkour = main.getPlayerDataHandler().getData(p).getParkour();

                        if (data.getLastCheckpoint() < 0) {
                            p.teleport(parkour.getSpawn());

                            String message = main.getLanguageHandler().getMessage("Messages.Return");
                            if(message.length() > 0)
                                p.sendMessage(message);

                            main.getParkourHandler().resetPlayer(p);

                        } else if (data.getLastCheckpoint() >= 0) {
                            p.teleport(data.getLastCheckpointLocation());

                            String message = main.getLanguageHandler().getMessage("Messages.ReturnCheckpoint");
                            if(message.length() > 0)
                                p.sendMessage(message.replaceAll("%checkpoint%", Integer.toString(data.getLastCheckpoint() + 1)));
                        }

                        main.getSoundUtil().playReturn(p);

                        p.setNoDamageTicks(40);

                        Bukkit.getPluginManager().callEvent(new ParkourReturnEvent(p, parkour));
                    }
                }
            }
        }
    }
}