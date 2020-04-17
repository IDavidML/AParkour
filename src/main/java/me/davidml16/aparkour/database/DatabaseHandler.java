package me.davidml16.aparkour.database;

import me.davidml16.aparkour.database.types.Database;

import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.database.types.MySQL;
import me.davidml16.aparkour.database.types.SQLite;
import me.davidml16.aparkour.managers.ColorManager;

public class DatabaseHandler {

	private Database database;

	private Main main;

	public DatabaseHandler(Main main) {
		this.main = main;
		if(main.getConfig().getBoolean("MySQL.Enabled")) {
			database = new MySQL(main);
		} else {
			database = new SQLite(main);
		}
	}

	public void openConnection() {
		Main.log.sendMessage(ColorManager.translate("  &eLoading database:"));
		database.open();
	}

	public void changeToSQLite() {
		database = new SQLite(main);
		database.open();
	}

	public Database getDatabase() { return database; }

}
