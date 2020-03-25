package me.davidml16.aparkour.tasks;

import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.api.events.ParkourReturnEvent;
import me.davidml16.aparkour.data.Parkour;
import me.davidml16.aparkour.utils.LocationUtil;
import me.davidml16.aparkour.utils.SoundUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ReturnTask {
	
	private int id;

	class Task implements Runnable {
		@Override
		public void run() {
			for(Player p : Bukkit.getOnlinePlayers()) {
				if (Main.getInstance().getTimerManager().hasPlayerTimer(p)) {
					Parkour parkour = Main.getInstance().getPlayerDataHandler().getData(p).getParkour();

					if (parkour.getWalkableBlocks().size() == 0) continue;

					Block block = p.getLocation().getBlock().getRelative(BlockFace.DOWN);

					if (!parkour.getWalkableBlocks().contains(block.getType()) && block.getType() != Material.IRON_PLATE && block.getType() != Material.GOLD_PLATE && block.getType() != Material.AIR) {
					    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
                            String Return = Main.getInstance().getLanguageHandler().getMessage("MESSAGES_RETURN", false);
							Main.getInstance().getPlayerDataHandler().getData(p).setParkour(null);

                            Main.getInstance().getTimerManager().cancelTimer(p);

                            p.sendMessage(Return);
                            p.teleport(parkour.getSpawn());

                            if (Main.getInstance().getConfig().getBoolean("RestartItem.Enabled")) {
                                Main.getInstance().getPlayerDataHandler().restorePlayerInventory(p);
                            }

                            SoundUtil.playReturn(p);

                            Bukkit.getPluginManager().callEvent(new ParkourReturnEvent(p, parkour));

                            p.setNoDamageTicks(20);
                        });
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
		id = Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(Main.getInstance(), new Task(), 0L, 5);
	}
	
	public void stop() {
		Bukkit.getServer().getScheduler().cancelTask(id);
	}
	
}
