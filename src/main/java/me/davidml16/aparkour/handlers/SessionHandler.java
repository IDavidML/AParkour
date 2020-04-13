package me.davidml16.aparkour.handlers;

import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.data.Parkour;
import me.davidml16.aparkour.data.ParkourSession;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionHandler {

    private Map<UUID, ParkourSession> sessions;

    private Main main;

    public SessionHandler(Main main) {
        this.main = main;
        this.sessions = new HashMap<UUID, ParkourSession>();
    }

    public Map<UUID, ParkourSession> getSessions() {
        return sessions;
    }

    public void setSessions(Map<UUID, ParkourSession> sessions) {
        this.sessions = sessions;
    }

    public ParkourSession getSession(Player p) {
        return sessions.get(p.getUniqueId());
    }

    public void createSession(Player p, Parkour parkour) {
        sessions.put(p.getUniqueId(), new ParkourSession(main, p, parkour));
    }

    public void removeSession(Player p) {
        sessions.remove(p.getUniqueId());
    }

}
