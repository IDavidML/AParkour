package me.davidml16.aparkour.handlers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.data.Parkour;

public class DatabaseHandler {

	private Connection connection;

	public DatabaseHandler() {
		this.connection = Main.getInstance().getADatabase().getConnection();
	}

	public void loadTables() {
		for (Parkour parkour : Main.getInstance().getParkourHandler().getParkours().values()) {
			try {
				Statement statement = connection.createStatement();
				statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + parkour.getId()
						+ " (`UUID` varchar(40) NOT NULL, `lastTime` int(11) NOT NULL, `bestTime` int(11) NOT NULL, PRIMARY KEY (`UUID`));");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		try {
			Statement statement = connection.createStatement();
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS playerNames (`UUID` varchar(40) NOT NULL, `NAME` varchar(40), PRIMARY KEY (`UUID`));");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean hasData(UUID uuid, String parkour) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;

		ps = connection.prepareStatement("SELECT * FROM " + parkour + " WHERE UUID = '" + uuid.toString() + "';");
		rs = ps.executeQuery();

		if (rs.next()) {
			return true;
		}

		return false;
	}

	public void createDataAllParkours(UUID uuid) throws SQLException {
		for (Parkour parkour : Main.getInstance().getParkourHandler().getParkours().values()) {
			createData(uuid, parkour.getId());
		}
	}

	public void createData(UUID uuid, String parkour) throws SQLException {
		PreparedStatement ps = null;
		ps = connection.prepareStatement("INSERT INTO " + parkour + " (UUID,lastTime,bestTime) VALUES(?,0,0)");
		ps.setString(1, uuid.toString());
		ps.executeUpdate();
	}
	
	public boolean hasName(Player p) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;

		ps = connection.prepareStatement("SELECT * FROM playernames WHERE UUID = '" + p.getUniqueId().toString() + "';");
		rs = ps.executeQuery();

		if (rs.next()) {
			return true;
		}

		return false;
	}
	
	public void updatePlayerName(Player p) throws SQLException {
		if(!hasName(p)) { 
			PreparedStatement ps = null;
			ps = connection.prepareStatement("INSERT INTO playerNames (UUID,NAME) VALUES(?,?)");
			ps.setString(1, p.getUniqueId().toString());
			ps.setString(2, p.getName());
			ps.executeUpdate();
		} else {
			PreparedStatement ps = null;
			ps = connection.prepareStatement("REPLACE INTO playernames (UUID,NAME) VALUES(?,?)");
			ps.setString(1, p.getUniqueId().toString());
			ps.setString(2, p.getName());
			ps.executeUpdate();
		}
	}
	
	public String getPlayerName(String uuid) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;

		ps = connection.prepareStatement("SELECT * FROM playernames WHERE UUID = '" + uuid + "';");
		rs = ps.executeQuery();

		if (rs.next()) {
			return rs.getString("NAME");
		}

		return "";
	}

	public Integer getLastTime(UUID uuid, String parkour) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;

		ps = connection.prepareStatement("SELECT * FROM " + parkour + " WHERE UUID = '" + uuid.toString() + "';");

		rs = ps.executeQuery();
		while (rs.next()) {
			return rs.getInt("lastTime");
		}

		return 0;
	}

	public Integer getBestTime(UUID uuid, String parkour) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;

		ps = connection.prepareStatement("SELECT * FROM " + parkour + " WHERE UUID = '" + uuid.toString() + "';");

		rs = ps.executeQuery();
		while (rs.next()) {
			return rs.getInt("bestTime");
		}

		return 0;
	}

	public void setTimes(UUID uuid, Integer lastTime, Integer bestTime, String parkour) throws SQLException {
		PreparedStatement ps = null;

		ps = connection.prepareStatement("REPLACE INTO " + parkour + " (UUID,lastTime,bestTime) VALUES(?,?,?)");
		ps.setString(1, uuid.toString());
		ps.setInt(2, lastTime);
		ps.setInt(3, bestTime);
		ps.executeUpdate();
	}

	public HashMap<String, Integer> getPlayerLastTimes(UUID uuid) {
		HashMap<String, Integer> times = new HashMap<String, Integer>();
		for (Parkour parkour : Main.getInstance().getParkourHandler().getParkours().values()) {
			try {
				if (Main.getInstance().getDatabaseHandler().hasData(uuid, parkour.getId())) {
					times.put(parkour.getId(),
							Main.getInstance().getDatabaseHandler().getLastTime(uuid, parkour.getId()));
				} else {
					Main.getInstance().getDatabaseHandler().createData(uuid, parkour.getId());
					times.put(parkour.getId(), 0);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return times;
	}

	public HashMap<String, Integer> getPlayerBestTimes(UUID uuid) {
		HashMap<String, Integer> times = new HashMap<String, Integer>();
		for (Parkour parkour : Main.getInstance().getParkourHandler().getParkours().values()) {
			try {
				if (Main.getInstance().getDatabaseHandler().hasData(uuid, parkour.getId())) {
					times.put(parkour.getId(),
							Main.getInstance().getDatabaseHandler().getBestTime(uuid, parkour.getId()));
				} else {
					Main.getInstance().getDatabaseHandler().createData(uuid, parkour.getId());
					times.put(parkour.getId(), 0);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return times;
	}
	
	public LinkedHashMap<String, Integer> getParkourBestTimes(String id, int amount) throws SQLException {
		LinkedHashMap<String, Integer> times = new LinkedHashMap<String, Integer>();
		PreparedStatement ps = null;
		ResultSet rs = null;

		ps = connection.prepareStatement("SELECT * FROM " + id + " WHERE bestTime != 0 ORDER BY bestTime ASC LIMIT " + amount + ";");

		rs = ps.executeQuery();
		while (rs.next()) {
			times.put(rs.getString("UUID"), rs.getInt("bestTime"));
		}
		
		return times;
	}

}
