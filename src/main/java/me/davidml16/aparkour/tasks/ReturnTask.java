package me.davidml16.aparkour.tasks;

import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.api.events.ParkourCheckpointEvent;
import me.davidml16.aparkour.api.events.ParkourReturnEvent;
import me.davidml16.aparkour.data.Parkour;
import me.davidml16.aparkour.data.ParkourSession;
import me.davidml16.aparkour.data.Profile;
import me.davidml16.aparkour.utils.WalkableBlocksUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class ReturnTask {

    private int id;

    private Main main;
    public ReturnTask(Main main) {
        this.main = main;
    }

    class Task implements Runnable {
        @SuppressWarnings("deprecation")
        @Override
        public void run() {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (main.getTimerManager().hasPlayerTimer(p)) {
                    ParkourSession session = main.getSessionHandler().getSession(p);

                    if (session.getParkour().getWalkableBlocks().size() == 0) continue;

                    Block block = p.getLocation().getY() % 1 == 0 ? p.getLocation().getBlock().getRelative(BlockFace.DOWN) : p.getLocation().getBlock();

                    if ((WalkableBlocksUtil.noContainsWalkable(session.getParkour().getWalkableBlocks(), block.getType().getId(), block.getData()) && block.getType() != Material.IRON_PLATE && block.getType() != Material.GOLD_PLATE && block.getType() != Material.AIR)) {
                        Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
                            if (main.getTimerManager().hasPlayerTimer(p)) {

                                p.setFlying(false);

                                if(session.getLastCheckpoint() < 0) {
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

                                    Bukkit.getPluginManager().callEvent(new ParkourReturnEvent(p, session.getParkour()));
                                } else if (session.getLastCheckpoint() >= 0) {
                                    p.teleport(session.getLastCheckpointLocation());

                                    String message = main.getLanguageHandler().getMessage("Messages.ReturnCheckpoint");
                                    if(message.length() > 0)
                                        p.sendMessage(message.replaceAll("%checkpoint%", Integer.toString(session.getLastCheckpoint() + 1)));

                                    Bukkit.getPluginManager().callEvent(new ParkourCheckpointEvent(p, session.getParkour()));
                                }

                                main.getSoundUtil().playReturn(p);

                                p.setFallDistance(0);
                                p.setNoDamageTicks(40);
                            }
                        });
                    } else if (p.isFlying()) {
                        if (main.getConfig().getBoolean("ReturnOnFly.Enabled")) {
                            Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
                                if (main.getTimerManager().hasPlayerTimer(p)) {
                                    p.teleport(session.getParkour().getSpawn());

                                    String message = main.getLanguageHandler().getMessage("Messages.Fly");
                                    if(message.length() > 0)
                                        p.sendMessage(message);

                                    main.getParkourHandler().resetPlayer(p);

                                    main.getSoundUtil().playFly(p);

                                    Bukkit.getPluginManager().callEvent(new ParkourReturnEvent(p, session.getParkour()));
                                }
                            });
                        }
                    }
                }
            }
        }
    }

    public int getId() {
        return id;
    }

    @SuppressWarnings("deprecation")
    public void start() {
        id = Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(main, new Task(), 0L, 2);
    }

    public void stop() {
        Bukkit.getServer().getScheduler().cancelTask(id);
    }

}
