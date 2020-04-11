package me.davidml16.aparkour.events;

import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.api.events.ParkourStartEvent;
import me.davidml16.aparkour.data.Parkour;
import me.davidml16.aparkour.managers.ColorManager;
import me.davidml16.aparkour.utils.Sounds;
import me.davidml16.aparkour.utils.TitleUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;

public class Event_PlateStart implements Listener {

    private Main main;
    public Event_PlateStart(Main main) {
        this.main = main;
    }

    private List<Player> cooldown = new ArrayList<Player>();

    @EventHandler
    public void Plate(PlayerInteractEvent e) {
        final Player p = e.getPlayer();
        Action action = e.getAction();

        if (action == Action.PHYSICAL) {
            if (e.getClickedBlock().getType() == Material.IRON_PLATE) {

                Parkour parkour = main.getParkourHandler().getParkourByLocation(e.getClickedBlock().getLocation());

                if (parkour == null) return;

                e.setCancelled(true);

                if (!parkour.getCheckpointLocations().contains(e.getClickedBlock().getLocation())) {
                    if (e.getClickedBlock().getLocation().equals(parkour.getStart().getLocation())) {
                        if (parkour.isPermissionRequired()) {
                            if (!main.getPlayerDataHandler().playerHasPermission(p, parkour.getPermission())) {
                                if (!cooldown.contains(p)) {
                                    cooldown.add(p);
                                    p.sendMessage(ColorManager.translate(main.getLanguageHandler().getPrefix() + " " + parkour.getPermissionMessage()));
                                    Sounds.playSound(p, p.getLocation(), Sounds.MySound.NOTE_PLING, 10, 0);
                                    Bukkit.getScheduler().runTaskLaterAsynchronously(main, () -> cooldown.remove(p), 40);
                                    return;
                                }
                                return;
                            }
                        }

                        if (!main.getTimerManager().hasPlayerTimer(p)) {
                            String message = main.getLanguageHandler().getMessage("Messages.Started");
                            if(message.length() > 0)
                                p.sendMessage(message);
                            p.setFlying(false);
                            main.getSoundUtil().playStart(p);

                            if (main.isParkourItemsEnabled()) {
                                main.getPlayerDataHandler().savePlayerInventory(p);
                                if (parkour.getCheckpoints().size() > 0) {
                                    p.getInventory().setItem(3, main.getParkourItems().getRestartItem());
                                    p.getInventory().setItem(5, main.getParkourItems().getCheckpointItem());
                                } else {
                                    p.getInventory().setItem(4, main.getParkourItems().getRestartItem());
                                }
                            }

                            main.getTitleUtil().sendStartTitle(p, parkour);

                            main.getSessionHandler().createSession(p, parkour);

                            main.getTimerManager().startTimer(p, parkour);

                            Bukkit.getPluginManager().callEvent(new ParkourStartEvent(p, parkour));
                        }
                    }
                }
            }
        }
    }

}
