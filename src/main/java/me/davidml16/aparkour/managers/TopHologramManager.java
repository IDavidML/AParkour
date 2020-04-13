package me.davidml16.aparkour.managers;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.data.LeaderboardEntry;
import me.davidml16.aparkour.data.Parkour;
import org.bukkit.Bukkit;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class TopHologramManager {

    private HashMap<String, Hologram> holoHeader;
    private HashMap<String, Hologram> holoBody;
    private HashMap<String, TextLine> holoFooter;

    private int timeLeft;
    private int reloadInterval;

    private Main main;

    public TopHologramManager(Main main, int reloadInterval) {
        this.main = main;
        this.reloadInterval = reloadInterval;
        this.holoHeader = new HashMap<String, Hologram>();
        this.holoBody = new HashMap<String, Hologram>();
        this.holoFooter = new HashMap<String, TextLine>();
    }

    public HashMap<String, Hologram> getHoloHeader() {
        return holoHeader;
    }

    public HashMap<String, Hologram> getHoloBody() {
        return holoBody;
    }

    public HashMap<String, TextLine> getHoloFooter() {
        return holoFooter;
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public void setReloadInterval(int reloadInterval) {
        this.reloadInterval = reloadInterval;
    }

    public void restartTimeLeft() {
        this.timeLeft = reloadInterval;
    }

    public void loadTopHolograms() {
        if (main.isHologramsEnabled()) {
            for (String parkour : main.getParkourHandler().getParkours().keySet()) {
                loadTopHologram(parkour);
            }
        }
    }

    public void loadTopHologram(String id) {
        if (main.isHologramsEnabled()) {
            Parkour parkour = main.getParkourHandler().getParkours().get(id);
            if (parkour.getTopHologram() != null) {
                Hologram header = HologramsAPI.createHologram(main,
                        parkour.getTopHologram().clone().add(0.5D, 4.5D, 0.5D));
                header.appendTextLine(main.getLanguageHandler()
                        .getMessage("Holograms.Top.Header.Line1").replaceAll("%parkour%", parkour.getName()));
                header.appendTextLine(main.getLanguageHandler()
                        .getMessage("Holograms.Top.Header.Line2").replaceAll("%parkour%", parkour.getName()));

                Hologram body = HologramsAPI.createHologram(main,
                        parkour.getTopHologram().clone().add(0.5D, 3.75D, 0.5D));

                Hologram footer = HologramsAPI.createHologram(main,
                        parkour.getTopHologram().clone().add(0.5D, 1D, 0.5D));
                footer.appendTextLine(main.getLanguageHandler()
                        .getMessage("Holograms.Top.Footer.Line")
                        .replaceAll("%time%", main.getTimerManager().secondsToString(timeLeft * 1000)));

                HashMap<String, Long> times = main.getDatabaseHandler().getParkourBestTimes(parkour.getId(), 10);

                int it = 0;
                for (Entry<String, Long> entry : times.entrySet()) {
                    try {

                        body.appendTextLine(main.getLanguageHandler()
                                .getMessage("Holograms.Top.Body.Line").replaceAll("%position%", "" + Integer.toString(it + 1))
                                .replaceAll("%player%",
                                        main.getDatabaseHandler().getPlayerName(entry.getKey().toString()))
                                .replaceAll("%time%", main.getTimerManager().millisToString(entry.getValue())));

                        it++;
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                for (int i = it; i < 10; i++) {
                    body.appendTextLine(main.getLanguageHandler()
                            .getMessage("Holograms.Top.Body.NoTime").replaceAll("%position%", "" + Integer.toString(i + 1)));
                }

                holoHeader.put(id, header);
                holoBody.put(id, body);
                holoFooter.put(id, (TextLine) footer.getLine(0));
            }
        }

    }

    public void removeHologram(String id) {
        if (main.isHologramsEnabled()) {
            if (holoHeader.containsKey(id)) {
                holoHeader.get(id).delete();
                holoHeader.remove(id);
            }

            if (holoBody.containsKey(id)) {
                holoBody.get(id).delete();
                holoBody.remove(id);
            }

            if (holoFooter.containsKey(id)) {
                holoFooter.get(id).getParent().delete();
                holoFooter.remove(id);
            }
        }
    }

    public void reloadTopHolograms() {
        if (main.isHologramsEnabled()) {
            if (timeLeft <= 0) {

                for (String parkour : main.getParkourHandler().getParkours().keySet()) {
                    Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
                        holoFooter.get(parkour).setText(main.getLanguageHandler().getMessage("Holograms.Top.Footer.Updating"));

                        main.getLeaderboardHandler().reloadLeaderboard(parkour);

                        if (holoBody.containsKey(parkour) && holoFooter.containsKey(parkour)) {
                            Hologram body = holoBody.get(parkour);

                            Map<Integer, LeaderboardEntry> leaderboard = main.getLeaderboardHandler().getLeaderboard(parkour);

                            for (int i = 0; i < 10; i++) {
                                if (leaderboard.get(i).getTime() > 0) {
                                    ((TextLine) body.getLine(i)).setText(main.getLanguageHandler()
                                            .getMessage("Holograms.Top.Body.Line").replaceAll("%position%", Integer.toString(i + 1))
                                            .replaceAll("%player%", leaderboard.get(i).getName())
                                            .replaceAll("%time%", main.getTimerManager().millisToString(leaderboard.get(i).getTime())));
                                } else {
                                    ((TextLine) body.getLine(i)).setText(main.getLanguageHandler()
                                            .getMessage("Holograms.Top.Body.NoTime").replaceAll("%position%", Integer.toString(i + 1)));
                                }
                            }
                        }
                    });
                }

                restartTimeLeft();
            }
            for (String parkour : main.getParkourHandler().getParkours().keySet()) {
                if (holoFooter.containsKey(parkour)) {
                    holoFooter.get(parkour)
                            .setText(main.getLanguageHandler()
                                    .getMessage("Holograms.Top.Footer.Line")
                                    .replaceAll("%time%", main.getTimerManager().secondsToString(timeLeft * 1000)));
                }
            }
            timeLeft--;
        }
    }

}
