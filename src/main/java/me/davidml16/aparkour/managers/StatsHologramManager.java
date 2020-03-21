package me.davidml16.aparkour.managers;

import java.util.ArrayList;
import java.util.List;

import me.davidml16.aparkour.data.Parkour;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.VisibilityManager;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;

import me.davidml16.aparkour.Main;

public class StatsHologramManager {

	public void loadStatsHolograms(Player p) {
		if (Main.getInstance().isHologramsEnabled()) {
			for (Parkour parkour : Main.getInstance().getParkourHandler().getParkours().values()) {
				loadStatsHologram(p, parkour.getId());
			}
		}
	}
	
	public void loadStatsHologram(Player p, String id) {
		if (Main.getInstance().isHologramsEnabled()) {
			Parkour parkour = Main.getInstance().getParkourHandler().getParkours().get(id);
			if(parkour.getStatsHologram() != null) {
				int bestTime = Main.getInstance().getPlayerDataHandler().getData(p).getBestTimes().get(parkour.getId());

				List<String> lines = getLines(parkour, p, bestTime);

				Hologram hologram = HologramsAPI.createHologram(Main.getInstance(),
						parkour.getStatsHologram().clone().add(0.5D, 2.0D, 0.5D));
				VisibilityManager visibilityManager = hologram.getVisibilityManager();

				visibilityManager.showTo(p);
				visibilityManager.setVisibleByDefault(false);

				hologram.insertTextLine(0, lines.get(0));
				hologram.insertTextLine(1, lines.get(1));

				Main.getInstance().getPlayerDataHandler().getData(p).getHolograms().put(parkour.getId(), hologram);
			}
		}
	}
	
	public void reloadStatsHolograms(Player p) {
		if (Main.getInstance().isHologramsEnabled()) {
			for (Parkour parkour : Main.getInstance().getParkourHandler().getParkours().values()) {
				reloadStatsHologram(p, parkour.getId());
			}
		}
	}

	public void reloadStatsHologram(Player p, String id) {
		if (Main.getInstance().isHologramsEnabled()) {
			Parkour parkour = Main.getInstance().getParkourHandler().getParkours().get(id);
			if(Main.getInstance().getPlayerDataHandler().getData(p).getHolograms().containsKey(parkour.getId())) {
				Hologram hologram = Main.getInstance().getPlayerDataHandler().getData(p).getHolograms().get(parkour.getId());

				int bestTime = Main.getInstance().getPlayerDataHandler().getData(p).getBestTimes().get(parkour.getId());

				List<String> lines = getLines(parkour, p, bestTime);

				((TextLine) hologram.getLine(0)).setText(lines.get(0));
				((TextLine) hologram.getLine(1)).setText(lines.get(1));
			}
		}
	}
	
	public List<String> getLines(Parkour parkour, Player p, int bestTime) {
		List<String> lines = new ArrayList<String>();
		String NoBestTime = Main.getInstance().getLanguageHandler().getMessage("TIMES_NOBESTTIME", false);
		String Line1 = Main.getInstance().getLanguageHandler().getMessage("HOLOGRAMS_STATS_LINE1", false);
		String Line2 = Main.getInstance().getLanguageHandler().getMessage("HOLOGRAMS_STATS_LINE2", false);
		
		if (bestTime != 0) {
			Line1 = Line1.replaceAll("%player%", p.getName())
					.replaceAll("%time%", Main.getInstance().getTimerManager().timeAsString(bestTime))
					.replaceAll("%parkour%", parkour.getName());
			Line2 = Line2.replaceAll("%player%", p.getName())
					.replaceAll("%time%", Main.getInstance().getTimerManager().timeAsString(bestTime))
					.replaceAll("%parkour%", parkour.getName());
		} else if (bestTime == 0) {
			Line1 = Line1.replaceAll("%player%", p.getName()).replaceAll("%time%", NoBestTime)
					.replaceAll("%parkour%", parkour.getName());
			Line2 = Line2.replaceAll("%player%", p.getName()).replaceAll("%time%", NoBestTime)
					.replaceAll("%parkour%", parkour.getName());
		}
		
		lines.add(Line1);
		lines.add(Line2);
		return lines;
	}
	
	public void removeStatsHolograms(Player p) {
		if (Main.getInstance().isHologramsEnabled()) {
			for (Parkour parkour : Main.getInstance().getParkourHandler().getParkours().values()) {
				if(parkour.getStatsHologram() != null) {
					removeStatsHologram(p, parkour.getId());
				}
			}
			Main.getInstance().getPlayerDataHandler().getData(p).getHolograms().clear();
		}
	}
	
	public void removeStatsHologram(Player p, String id) {
		if (Main.getInstance().isHologramsEnabled()) {
			if(Main.getInstance().getPlayerDataHandler().getData(p).getHolograms().containsKey(id)) {
				Main.getInstance().getPlayerDataHandler().getData(p).getHolograms().get(id).delete();
				Main.getInstance().getPlayerDataHandler().getData(p).getHolograms().remove(id);
			}
		}
	}

	public void reloadStatsHolograms() {
		if (Main.getInstance().isHologramsEnabled()) {
			for (Hologram hologram : HologramsAPI.getHolograms(Main.getInstance())) {
				hologram.delete();
			}
			
			Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
				public void run() {
					for (Player p : Bukkit.getServer().getOnlinePlayers()) {
						loadStatsHolograms(p);
					}
				}
			}, 1L);
		}
	}

}
