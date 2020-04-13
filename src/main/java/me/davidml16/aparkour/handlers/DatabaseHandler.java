package me.davidml16.aparkour.handlers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.data.Parkour;

public class DatabaseHandler {

	private Connection connection;

	private Main main;

	public DatabaseHandler(Main main) {
		this.main = main;
		this.connection = main.getADatabase().getConnection();
	}

	public void loadTables() {
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS ap_times (`UUID` varchar(40) NOT NULL, `parkourID` varchar(25) NOT NULL, `lastTime` bigint NOT NULL, `bestTime` bigint NOT NULL, PRIMARY KEY (`UUID`, `parkourID`));");
			statement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		PreparedStatement statement2 = null;
		try {
			statement2 = connection.prepareStatement("CREATE TABLE IF NOT EXISTS ap_playernames (`UUID` varchar(40) NOT NULL, `NAME` varchar(40), PRIMARY KEY (`UUID`));");
			statement2.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(statement2 != null) {
				try {
					statement2.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public boolean hasData(UUID uuid, String parkour) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = connection.prepareStatement("SELECT * FROM ap_times WHERE UUID = '" + uuid.toString() + "' AND parkourID = '" + parkour + "';");
			rs = ps.executeQuery();

			if (rs.next()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(ps != null) ps.close();
			if(rs != null) rs.close();
		}

		return false;
	}

	public void createData(UUID uuid, String parkour) throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement("INSERT INTO ap_times (UUID,parkourID,lastTime,bestTime) VALUES(?,?,0,0)");
			ps.setString(1, uuid.toString());
			ps.setString(2, parkour);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(ps != null) ps.close();
		}
	}
	
	public boolean hasName(Player p) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = connection.prepareStatement("SELECT * FROM ap_playernames WHERE UUID = '" + p.getUniqueId().toString() + "';");
			rs = ps.executeQuery();

			if (rs.next()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(ps != null) ps.close();
			if(rs != null) rs.close();
		}

		return false;
	}
	
	public void updatePlayerName(Player p) throws SQLException {
		PreparedStatement ps = null;
		try {
			if (!hasName(p)) {
				ps = connection.prepareStatement("INSERT INTO ap_playernames (UUID,NAME) VALUES(?,?)");
				ps.setString(1, p.getUniqueId().toString());
				ps.setString(2, p.getName());
			} else {
				ps = connection.prepareStatement("REPLACE INTO ap_playernames (UUID,NAME) VALUES(?,?)");
				ps.setString(1, p.getUniqueId().toString());
				ps.setString(2, p.getName());
			}
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(ps != null) ps.close();
		}
	}
	
	public String getPlayerName(String uuid) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;

		ps = connection.prepareStatement("SELECT * FROM ap_playernames WHERE UUID = '" + uuid + "';");
		rs = ps.executeQuery();

		if (rs.next()) {
			return rs.getString("NAME");
		}

		return "";
	}

	public Long getLastTime(UUID uuid, String parkour) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = connection.prepareStatement("SELECT * FROM ap_times WHERE UUID = '" + uuid.toString() + "' AND parkourID = '" + parkour + "';");

			rs = ps.executeQuery();
			while (rs.next()) {
				return rs.getLong("lastTime");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(ps != null) ps.close();
			if(rs != null) rs.close();
		}

		return 0L;
	}

	public Long getBestTime(UUID uuid, String parkour) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = connection.prepareStatement("SELECT * FROM ap_times WHERE UUID = '" + uuid.toString() + "' AND parkourID = '" + parkour + "';");

			rs = ps.executeQuery();
			while (rs.next()) {
				return rs.getLong("bestTime");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(ps != null) ps.close();
			if(rs != null) rs.close();
		}

		return 0L;
	}

	public void setTimes(UUID uuid, Long lastTime, Long bestTime, String parkour) throws SQLException {
		PreparedStatement ps = null;

		try {
			ps = connection.prepareStatement("REPLACE INTO ap_times (UUID,parkourID,lastTime,bestTime) VALUES(?,?,?,?)");
			ps.setString(1, uuid.toString());
			ps.setString(2, parkour);
			ps.setLong(3, lastTime);
			ps.setLong(4, bestTime);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(ps != null) ps.close();
		}
	}

	public HashMap<String, Long> getPlayerLastTimes(UUID uuid) {
		HashMap<String, Long> times = new HashMap<String, Long>();
		for (Parkour parkour : main.getParkourHandler().getParkours().values()) {
			try {
				if (hasData(uuid, parkour.getId())) {
					times.put(parkour.getId(), getLastTime(uuid, parkour.getId()));
				} else {
					createData(uuid, parkour.getId());
					times.put(parkour.getId(), 0L);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return times;
	}

	public HashMap<String, Long> getPlayerBestTimes(UUID uuid) {
		HashMap<String, Long> times = new HashMap<String, Long>();
		for (Parkour parkour : main.getParkourHandler().getParkours().values()) {
			try {
				if (hasData(uuid, parkour.getId())) {
					times.put(parkour.getId(), getBestTime(uuid, parkour.getId()));
				} else {
					createData(uuid, parkour.getId());
					times.put(parkour.getId(), 0L);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return times;
	}
	
	public LinkedHashMap<String, Long> getParkourBestTimes(String id, int amount) {
		LinkedHashMap<String, Long> times = new LinkedHashMap<String, Long>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement("SELECT * FROM ap_times WHERE bestTime != 0 AND parkourID = '" + id + "' ORDER BY bestTime ASC LIMIT " + amount + ";");

			rs = ps.executeQuery();
			while (rs.next()) {
				times.put(rs.getString("UUID"), rs.getLong("bestTime"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return times;
	}

}
