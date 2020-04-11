package me.davidml16.aparkour.events;

import me.davidml16.aparkour.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class Event_Swap implements Listener {

    private Main main;
    public Event_Swap(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent e) {
        if (main.getTimerManager().hasPlayerTimer(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

}
