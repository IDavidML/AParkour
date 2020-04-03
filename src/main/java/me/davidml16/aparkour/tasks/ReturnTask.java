package me.davidml16.aparkour.tasks;

import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.api.events.ParkourCheckpointEvent;
import me.davidml16.aparkour.api.events.ParkourReturnEvent;
import me.davidml16.aparkour.data.Parkour;
import me.davidml16.aparkour.data.Profile;
import me.davidml16.aparkour.utils.ParkourItems;
import me.davidml16.aparkour.utils.SoundUtil;
import me.davidml16.aparkour.utils.WalkableBlocksUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class ReturnTask {

    private int id;

    class Task implements Runnable {
        @SuppressWarnings("deprecation")
        @Override
        public void run() {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (Main.getInstance().getTimerManager().hasPlayerTimer(p)) {
                    Parkour parkour = Main.getInstance().getPlayerDataHandler().getData(p).getParkour();

                    if (parkour.getWalkableBlocks().size() == 0) continue;

                    Block block = p.getLocation().getY() % 1 == 0 ? p.getLocation().getBlock().getRelative(BlockFace.DOWN) : p.getLocation().getBlock();

                    if ((WalkableBlocksUtil.noContainsWalkable(parkour.getWalkableBlocks(), block.getType().getId(), block.getData()) && block.getType() != Material.IRON_PLATE && block.getType() != Material.GOLD_PLATE && block.getType() != Material.AIR)) {
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
                            if (Main.getInstance().getTimerManager().hasPlayerTimer(p)) {

                                Profile data = Main.getInstance().getPlayerDataHandler().getData(p);

                                p.setFlying(false);

                                if(data.getLastCheckpoint() < 0) {

                                    p.teleport(parkour.getSpawn());
                                    p.sendMessage(Main.getInstance().getLanguageHandler().getMessage("Messages.Return"));

                                    data.setParkour(null);
                                    data.setLastCheckpoint(-1);

                                    Main.getInstance().getTimerManager().cancelTimer(p);
                                    if (Main.getInstance().isParkourItemsEnabled()) {
                                        Main.getInstance().getPlayerDataHandler().restorePlayerInventory(p);
                                    }

                                    Bukkit.getPluginManager().callEvent(new ParkourReturnEvent(p, parkour));
                                } else if (data.getLastCheckpoint() >= 0) {

                                    p.teleport(data.getLastCheckpointLocation());
                                    p.sendMessage(Main.getInstance().getLanguageHandler().getMessage("Messages.ReturnCheckpoint")
                                            .replaceAll("%checkpoint%", Integer.toString(data.getLastCheckpoint() + 1)));

                                    Bukkit.getPluginManager().callEvent(new ParkourCheckpointEvent(p, parkour));
                                }

                                SoundUtil.playReturn(p);

                                p.setNoDamageTicks(40);
                            }
                        });
                    } else if (p.isFlying()) {
                        if (Main.getInstance().getConfig().getBoolean("ReturnOnFly.Enabled")) {
                            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
                                if (Main.getInstance().getTimerManager().hasPlayerTimer(p)) {
                                    p.setFlying(false);
                                    p.teleport(parkour.getSpawn());

                                    p.sendMessage(Main.getInstance().getLanguageHandler().getMessage("Messages.Fly"));

                                    Profile data = Main.getInstance().getPlayerDataHandler().getData(p);
                                    data.setParkour(null);
                                    data.setLastCheckpoint(-1);

                                    Main.getInstance().getTimerManager().cancelTimer(p);
                                    if (Main.getInstance().isParkourItemsEnabled()) {
                                        Main.getInstance().getPlayerDataHandler().restorePlayerInventory(p);
                                    }

                                    SoundUtil.playFly(p);

                                    Bukkit.getPluginManager().callEvent(new ParkourReturnEvent(p, parkour));

                                    p.setNoDamageTicks(40);
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
        id = Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(Main.getInstance(), new Task(), 0L, 2);
    }

    public void stop() {
        Bukkit.getServer().getScheduler().cancelTask(id);
    }

}
