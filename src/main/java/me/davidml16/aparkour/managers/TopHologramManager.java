package me.davidml16.aparkour.managers;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.data.Parkour;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map.Entry;

public class TopHologramManager {

    private HashMap<String, Hologram> holoHeader;
    private HashMap<String, Hologram> holoBody;
    private HashMap<String, TextLine> holoFooter;

    private int timeLeft;
    private int reloadInterval;

    public TopHologramManager(int reloadInterval) {
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
        if (Main.getInstance().isHologramsEnabled()) {
            for (Parkour parkour : Main.getInstance().getParkourHandler().getParkours().values()) {
                loadTopHologram(parkour.getId());
            }
        }
    }

    public void loadTopHologram(String id) {
        if (Main.getInstance().isHologramsEnabled()) {
            Parkour parkour = Main.getInstance().getParkourHandler().getParkours().get(id);
            if (parkour.getTopHologram() != null) {
                Hologram header = HologramsAPI.createHologram(Main.getInstance(),
                        parkour.getTopHologram().clone().add(0.5D, 4.5D, 0.5D));
                header.appendTextLine(Main.getInstance().getLanguageHandler()
                        .getMessage("Holograms.Top.Header.Line1").replaceAll("%parkour%", parkour.getName()));
                header.appendTextLine(Main.getInstance().getLanguageHandler()
                        .getMessage("Holograms.Top.Header.Line2").replaceAll("%parkour%", parkour.getName()));

                Hologram body = HologramsAPI.createHologram(Main.getInstance(),
                        parkour.getTopHologram().clone().add(0.5D, 3.75D, 0.5D));

                Hologram footer = HologramsAPI.createHologram(Main.getInstance(),
                        parkour.getTopHologram().clone().add(0.5D, 1D, 0.5D));
                footer.appendTextLine("&aUpdate: &6" + Main.getInstance().getTimerManager().timeAsString(timeLeft));

                HashMap<String, Integer> times = new HashMap<String, Integer>();
                try {
                    times = Main.getInstance().getDatabaseHandler().getParkourBestTimes(parkour.getId(), 10);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                int it = 0;
                for (Entry<String, Integer> entry : times.entrySet()) {
                    try {

                        body.appendTextLine(Main.getInstance().getLanguageHandler()
                                .getMessage("Holograms.Top.Body.Line").replaceAll("%position%", "" + Integer.toString(it + 1))
                                .replaceAll("%player%",
                                        Main.getInstance().getDatabaseHandler().getPlayerName(entry.getKey().toString()))
                                .replaceAll("%time%", Main.getInstance().getTimerManager().timeAsString(entry.getValue())));

                        it++;
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                for (int i = it; i < 10; i++) {
                    body.appendTextLine(Main.getInstance().getLanguageHandler()
                            .getMessage("Holograms.Top.Body.NoTime").replaceAll("%position%", "" + Integer.toString(i + 1)));
                }

                holoHeader.put(id, header);
                holoBody.put(id, body);
                holoFooter.put(id, (TextLine) footer.getLine(0));
            }
        }

    }

    public void removeHologram(String id) {
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

    public void reloadTopHolograms() {
        if (Main.getInstance().isHologramsEnabled()) {
            if (timeLeft <= 0) {

                Main.getInstance().getRankingsGUI().reloadGUI();

                for (Parkour parkour : Main.getInstance().getParkourHandler().getParkours().values()) {
                    if (holoBody.containsKey(parkour.getId()) && holoFooter.containsKey(parkour.getId())) {
                        Hologram body = holoBody.get(parkour.getId());

                        HashMap<String, Integer> times = new HashMap<String, Integer>();
                        try {
                            times = Main.getInstance().getDatabaseHandler().getParkourBestTimes(parkour.getId(), 10);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        int it = 0;
                        for (Entry<String, Integer> entry : times.entrySet()) {
                            try {
                                ((TextLine) body.getLine(it)).setText(Main.getInstance().getLanguageHandler()
                                        .getMessage("Holograms.Top.Body.Line").replaceAll("%position%", Integer.toString(it + 1))
                                        .replaceAll("%player%",
                                                Main.getInstance().getDatabaseHandler()
                                                        .getPlayerName(entry.getKey().toString()))
                                        .replaceAll("%time%",
                                                Main.getInstance().getTimerManager().timeAsString(entry.getValue())));

                                it++;
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }

                        for (int i = it; i < 10; i++) {
                            ((TextLine) body.getLine(i)).setText(Main.getInstance().getLanguageHandler()
                                    .getMessage("Holograms.Top.Body.NoTime").replaceAll("%position%", Integer.toString(i + 1)));
                        }

                        holoFooter.get(parkour.getId()).setText(
                                Main.getInstance().getLanguageHandler().getMessage("Holograms.Top.Footer.Updating"));
                    }
                }

                restartTimeLeft();
                return;
            }
            for (Parkour parkour : Main.getInstance().getParkourHandler().getParkours().values()) {
                if (holoFooter.containsKey(parkour.getId())) {
                    holoFooter.get(parkour.getId())
                            .setText(Main.getInstance().getLanguageHandler()
                                    .getMessage("Holograms.Top.Footer.Line")
                                    .replaceAll("%time%", Main.getInstance().getTimerManager().timeAsString(timeLeft)));
                }
            }
            timeLeft--;
        }
    }

}
