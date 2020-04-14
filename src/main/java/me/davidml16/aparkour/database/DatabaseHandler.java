package me.davidml16.aparkour.database;

import me.davidml16.aparkour.database.types.Database;

import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.database.types.MySQL;
import me.davidml16.aparkour.database.types.SQLite;
import me.davidml16.aparkour.managers.ColorManager;

public class DatabaseHandler {

	private final Database database;

	public DatabaseHandler(Main main) {
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

	public Database getDatabase() { return database; }

}
