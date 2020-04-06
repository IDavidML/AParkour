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
		for (Parkour parkour : main.getParkourHandler().getParkours().values()) {
			PreparedStatement statement = null;
			try {
				statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + parkour.getId()
						+ " (`UUID` varchar(40) NOT NULL, `lastTime` integer NOT NULL, `bestTime` integer NOT NULL, PRIMARY KEY (`UUID`));");
				statement.execute();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					if(statement != null) {
						statement.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS playernames (`UUID` varchar(40) NOT NULL, `NAME` varchar(40), PRIMARY KEY (`UUID`));");
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
	}

	public boolean hasData(UUID uuid, String parkour) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = connection.prepareStatement("SELECT * FROM " + parkour + " WHERE UUID = '" + uuid.toString() + "';");
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
			ps = connection.prepareStatement("INSERT INTO " + parkour + " (UUID,lastTime,bestTime) VALUES(?,0,0)");
			ps.setString(1, uuid.toString());
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
			ps = connection.prepareStatement("SELECT * FROM playernames WHERE UUID = '" + p.getUniqueId().toString() + "';");
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
				ps = connection.prepareStatement("INSERT INTO playernames (UUID,NAME) VALUES(?,?)");
				ps.setString(1, p.getUniqueId().toString());
				ps.setString(2, p.getName());
			} else {
				ps = connection.prepareStatement("REPLACE INTO playernames (UUID,NAME) VALUES(?,?)");
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

		try {
			ps = connection.prepareStatement("SELECT * FROM " + parkour + " WHERE UUID = '" + uuid.toString() + "';");

			rs = ps.executeQuery();
			while (rs.next()) {
				return rs.getInt("lastTime");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(ps != null) ps.close();
			if(rs != null) rs.close();
		}

		return 0;
	}

	public Integer getBestTime(UUID uuid, String parkour) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = connection.prepareStatement("SELECT * FROM " + parkour + " WHERE UUID = '" + uuid.toString() + "';");

			rs = ps.executeQuery();
			while (rs.next()) {
				return rs.getInt("bestTime");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(ps != null) ps.close();
			if(rs != null) rs.close();
		}

		return 0;
	}

	public void setTimes(UUID uuid, Integer lastTime, Integer bestTime, String parkour) throws SQLException {
		PreparedStatement ps = null;

		try {
			ps = connection.prepareStatement("REPLACE INTO " + parkour + " (UUID,lastTime,bestTime) VALUES(?,?,?)");
			ps.setString(1, uuid.toString());
			ps.setInt(2, lastTime);
			ps.setInt(3, bestTime);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(ps != null) ps.close();
		}
	}

	public HashMap<String, Integer> getPlayerLastTimes(UUID uuid) {
		HashMap<String, Integer> times = new HashMap<String, Integer>();
		for (Parkour parkour : main.getParkourHandler().getParkours().values()) {
			try {
				if (hasData(uuid, parkour.getId())) {
					times.put(parkour.getId(), getLastTime(uuid, parkour.getId()));
				} else {
					createData(uuid, parkour.getId());
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
		for (Parkour parkour : main.getParkourHandler().getParkours().values()) {
			try {
				if (hasData(uuid, parkour.getId())) {
					times.put(parkour.getId(), getBestTime(uuid, parkour.getId()));
				} else {
					createData(uuid, parkour.getId());
					times.put(parkour.getId(), 0);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return times;
	}
	
	public LinkedHashMap<String, Integer> getParkourBestTimes(String id, int amount) {
		LinkedHashMap<String, Integer> times = new LinkedHashMap<String, Integer>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement("SELECT * FROM " + id + " WHERE bestTime != 0 ORDER BY bestTime ASC LIMIT " + amount + ";");

			rs = ps.executeQuery();
			while (rs.next()) {
				times.put(rs.getString("UUID"), rs.getInt("bestTime"));
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
