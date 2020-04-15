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

	private Main main;
	public StatsHologramManager(Main main) {
		this.main = main;
	}

	public void loadStatsHolograms(Player p) {
		if (main.isHologramsEnabled()) {
			for (String parkour : main.getParkourHandler().getParkours().keySet()) {
				loadStatsHologram(p, parkour);
			}
		}
	}
	
	public void loadStatsHologram(Player p, String id) {
		if (main.isHologramsEnabled()) {
			Parkour parkour = main.getParkourHandler().getParkours().get(id);
			if(parkour.getStatsHologram() != null) {
				List<String> lines = new ArrayList<String>();
				String Line1 = main.getLanguageHandler().getMessage("Holograms.Stats.Line1");
				String Line2 = main.getLanguageHandler().getMessage("Holograms.Stats.Line2");

				Line1 = Line1.replaceAll("%player%", p.getName())
						.replaceAll("%time%", ColorManager.translate(main.getLanguageHandler().getMessage("Times.Loading")))
						.replaceAll("%parkour%", parkour.getName());
				Line2 = Line2.replaceAll("%player%", p.getName())
						.replaceAll("%time%", ColorManager.translate(main.getLanguageHandler().getMessage("Times.Loading")))
						.replaceAll("%parkour%", parkour.getName());

				lines.add(Line1);
				lines.add(Line2);

				Hologram hologram = HologramsAPI.createHologram(main, parkour.getStatsHologram().clone().add(0.5D, 2.0D, 0.5D));
				VisibilityManager visibilityManager = hologram.getVisibilityManager();

				visibilityManager.showTo(p);
				visibilityManager.setVisibleByDefault(false);

				hologram.insertTextLine(0, lines.get(0));
				hologram.insertTextLine(1, lines.get(1));

				main.getPlayerDataHandler().getData(p).getHolograms().put(parkour.getId(), hologram);
			}
		}
	}
	
	public void reloadStatsHolograms(Player p) {
		if (main.isHologramsEnabled()) {
			for (String parkour : main.getParkourHandler().getParkours().keySet()) {
				reloadStatsHologram(p, parkour);
			}
		}
	}

	public void reloadStatsHologram(Player p, String id) {
		if (main.isHologramsEnabled()) {
			Parkour parkour = main.getParkourHandler().getParkours().get(id);
			if(main.getPlayerDataHandler().getData(p).getHolograms().containsKey(parkour.getId())) {
				Hologram hologram = main.getPlayerDataHandler().getData(p).getHolograms().get(parkour.getId());

				long bestTime = main.getPlayerDataHandler().getData(p).getBestTimes().get(parkour.getId());

				List<String> lines = getLines(parkour, p, bestTime);

				((TextLine) hologram.getLine(0)).setText(lines.get(0));
				((TextLine) hologram.getLine(1)).setText(lines.get(1));
			}
		}
	}
	
	public List<String> getLines(Parkour parkour, Player p, long bestTime) {
		List<String> lines = new ArrayList<String>();
		String NoBestTime = main.getLanguageHandler().getMessage("Times.NoBestTime");
		String Line1 = main.getLanguageHandler().getMessage("Holograms.Stats.Line1");
		String Line2 = main.getLanguageHandler().getMessage("Holograms.Stats.Line2");
		
		if (bestTime != 0) {
			Line1 = Line1.replaceAll("%player%", p.getName())
					.replaceAll("%time%", main.getTimerManager().millisToString(bestTime))
					.replaceAll("%parkour%", parkour.getName());
			Line2 = Line2.replaceAll("%player%", p.getName())
					.replaceAll("%time%", main.getTimerManager().millisToString(bestTime))
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
		if (main.isHologramsEnabled()) {
			for (Parkour parkour : main.getParkourHandler().getParkours().values()) {
				if(parkour.getStatsHologram() != null) {
					removeStatsHologram(p, parkour.getId());
				}
			}
			main.getPlayerDataHandler().getData(p).getHolograms().clear();
		}
	}
	
	public void removeStatsHologram(Player p, String id) {
		if (main.isHologramsEnabled()) {
			if(main.getPlayerDataHandler().getData(p).getHolograms().containsKey(id)) {
				main.getPlayerDataHandler().getData(p).getHolograms().get(id).delete();
				main.getPlayerDataHandler().getData(p).getHolograms().remove(id);
			}
		}
	}

	public void reloadStatsHolograms() {
		if (main.isHologramsEnabled()) {
			for (Hologram hologram : HologramsAPI.getHolograms(main)) {
				hologram.delete();
			}
		}
	}

}
