package me.davidml16.aparkour.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.bukkit.Bukkit;

import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.managers.ColorManager;

public class ADatabase {
	
	private Connection connection;
	
	private boolean mysqlEnabled;
	
	private String host, user, password, database;
	private int port;
	
	public ADatabase() {
		if(Main.getInstance().getConfig().getBoolean("MySQL.Enabled")) {
			this.host = Main.getInstance().getConfig().getString("MySQL.Host");
			this.user = Main.getInstance().getConfig().getString("MySQL.User");
			this.password = Main.getInstance().getConfig().getString("MySQL.Password");
			this.database = Main.getInstance().getConfig().getString("MySQL.Database");
			this.port = Main.getInstance().getConfig().getInt("MySQL.Port");
			mysqlEnabled = true;
		} else {
			mysqlEnabled = false;
		}
	}
	
	public void openConnection() {
		Main.log.sendMessage(ColorManager.translate("  &eLoading database:"));
		if(mysqlEnabled)
			openConnectionMySQL();
		else
			openConnectionSQLite();
	}
	
	private void openConnectionMySQL() {
		
		if (connection != null)  return;
		
		String URL = "jdbc:mysql://" + host + ":" + port + "/" + database;
		
		synchronized (this) {
			try {
				Class.forName("com.mysql.jdbc.Driver");
				connection = DriverManager.getConnection(URL, user, password);
				Main.log.sendMessage(ColorManager.translate("    &aMySQL has been enabled!"));
			} catch (SQLException | ClassNotFoundException e) {
				Main.log.sendMessage(ColorManager.translate("    &cMySQL has an error on the conection! Now trying with SQLite..."));
				openConnectionSQLite();
			}
		}
	}
	
	private void openConnectionSQLite() {
		
		if (connection != null)  return;
		
		File file = new File(Main.getInstance().getDataFolder(), "playerData.db");
		String URL = "jdbc:sqlite:" + file;
		
		synchronized (this) {
			try {
				Class.forName("org.sqlite.JDBC");
				connection = DriverManager.getConnection(URL);
				Main.log.sendMessage(ColorManager.translate("    &aSQLite has been enabled!"));
			} catch (SQLException | ClassNotFoundException e) {
				Main.log.sendMessage(ColorManager.translate("    &cSQLite has an error on the conection! Plugin disabled : Database needed"));
				Bukkit.getPluginManager().disablePlugin(Bukkit.getPluginManager().getPlugin("AParkour"));
			}
		}
	}

	public Connection getConnection() {
		return connection;
	}

}
