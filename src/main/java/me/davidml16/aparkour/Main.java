package me.davidml16.aparkour;

import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import me.davidml16.aparkour.commands.autoCompleter_AParkour;
import me.davidml16.aparkour.commands.cmd_AParkour;
import me.davidml16.aparkour.database.ADatabase;
import me.davidml16.aparkour.events.event_Click;
import me.davidml16.aparkour.events.event_Fall;
import me.davidml16.aparkour.events.event_Fly;
import me.davidml16.aparkour.events.event_InventoryGUI;
import me.davidml16.aparkour.events.event_Others;
import me.davidml16.aparkour.events.event_Plate_End;
import me.davidml16.aparkour.events.event_Plate_Start;
import me.davidml16.aparkour.gui.parkourRanking_GUI;
import me.davidml16.aparkour.gui.playerStats_GUI;
import me.davidml16.aparkour.handlers.DatabaseHandler;
import me.davidml16.aparkour.handlers.LanguageHandler;
import me.davidml16.aparkour.handlers.ParkourHandler;
import me.davidml16.aparkour.handlers.PlayerDataHandler;
import me.davidml16.aparkour.handlers.RewardHandler;
import me.davidml16.aparkour.managers.ColorManager;
import me.davidml16.aparkour.managers.StatsHologramManager;
import me.davidml16.aparkour.managers.TimerManager;
import me.davidml16.aparkour.managers.TopHologramManager;
import me.davidml16.aparkour.tasks.HologramTask;
import me.davidml16.aparkour.utils.RandomFirework;

public class Main extends JavaPlugin {

	public static Main instance;
	public static ConsoleCommandSender log;

	private playerStats_GUI statsGUI;
	private parkourRanking_GUI rankingsGUI;
	
	private HologramTask hologramTask;

	private TimerManager timerManager;
	private StatsHologramManager statsHologramManager;
	private TopHologramManager topHologramManager;

	private ParkourHandler parkourHandler;
	private RewardHandler rewardHandler;
	private LanguageHandler languageHandler;
	private PlayerDataHandler playerDataHandler;
	private DatabaseHandler databaseHandler;

	private ADatabase database;
	
	private MetricsLite metrics;

	public void onEnable() {
		instance = this;
		metrics = new MetricsLite(this, 6728);

		getConfig().options().copyDefaults(true);
		saveConfig();
		reloadConfig();

		log = Bukkit.getConsoleSender();

		languageHandler = new LanguageHandler(getConfig().getString("Language").toLowerCase());
		languageHandler.pushMessages();
		
		statsHologramManager = new StatsHologramManager(getConfig().getBoolean("Hologram.Enabled"));
		topHologramManager = new TopHologramManager(getConfig().getBoolean("Hologram.Enabled"), getConfig().getInt("Tasks.ReloadInterval"));

		parkourHandler = new ParkourHandler();
		parkourHandler.saveConfig();
		parkourHandler.loadParkours();

		rewardHandler = new RewardHandler();
		rewardHandler.saveConfig();
		rewardHandler.loadRewards();
		
		database = new ADatabase();
		database.openConnection();
		databaseHandler = new DatabaseHandler();
		databaseHandler.loadTables();

		playerDataHandler = new PlayerDataHandler();

		timerManager = new TimerManager();

		statsGUI = new playerStats_GUI();
		
		rankingsGUI = new parkourRanking_GUI();
		rankingsGUI.loadGUI();
		
		if(statsHologramManager.isHologramsEnabled()) {
			if (!Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")
					|| !Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
				getLogger().severe("*** HolographicDisplays / ProtocolLib is not installed or not enabled. ***");
				getLogger().severe("*** This plugin will be disabled. ***");
				setEnabled(false);
				return;
			}
		}

		topHologramManager.loadTopHolograms();
		topHologramManager.restartTimeLeft();

		hologramTask = new HologramTask();
		hologramTask.start();

		registerEvents();
		registerCommands();
		RandomFirework.loadFireworks();
		
		for(Player p : Bukkit.getOnlinePlayers()) {
			getPlayerDataHandler().loadPlayerData(p);
			try {
				Main.getInstance().getDatabaseHandler().updatePlayerName(p);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			getStatsHologramManager().loadStatsHolograms(p);
		}

		PluginDescriptionFile pdf = getDescription();
		log.sendMessage("");
		log.sendMessage(ColorManager.translate("  &eAParkour Enabled!"));
		log.sendMessage(ColorManager.translate("    &aVersion: &b" + pdf.getVersion()));
		log.sendMessage(ColorManager.translate("    &aAuthor: &b" + pdf.getAuthors().get(0)));
		log.sendMessage("");
	}

	public void onDisable() {
		PluginDescriptionFile pdf = getDescription();
		log.sendMessage("");
		log.sendMessage(ColorManager.translate("  &eAParkour Disabled!"));
		log.sendMessage(ColorManager.translate("    &aVersion: &b" + pdf.getVersion()));
		log.sendMessage(ColorManager.translate("    &aAuthor: &b" + pdf.getAuthors().get(0)));
		log.sendMessage("");
		for (Hologram hologram : HologramsAPI.getHolograms(this)) {
			hologram.delete();
		}
		for (UUID d : playerDataHandler.getPlayersData().keySet()) {
			getPlayerDataHandler().getData(d).save();
		}
		getParkourHandler().saveParkours();
		
		getHologramTask().stop();
	}

	public static Main getInstance() {
		return instance;
	}

	public playerStats_GUI getStatsGUI() {
		return statsGUI;
	}

	public parkourRanking_GUI getRankingsGUI() {
		return rankingsGUI;
	}

	public TimerManager getTimerManager() {
		return timerManager;
	}

	public StatsHologramManager getStatsHologramManager() {
		return statsHologramManager;
	}

	public TopHologramManager getTopHologramManager() {
		return topHologramManager;
	}

	public ParkourHandler getParkourHandler() {
		return parkourHandler;
	}

	public LanguageHandler getLanguageHandler() {
		return languageHandler;
	}

	public PlayerDataHandler getPlayerDataHandler() {
		return playerDataHandler;
	}

	public DatabaseHandler getDatabaseHandler() {
		return databaseHandler;
	}
	
	public RewardHandler getRewardHandler() {
		return rewardHandler;
	}

	public ADatabase getADatabase() {
		return database;
	}
	
	public MetricsLite getMetrics() {
		return metrics;
	}

	public HologramTask getHologramTask() {
		return hologramTask;
	}

	private void registerCommands() {
		getCommand("aparkour").setExecutor(new cmd_AParkour());
		getCommand("aparkour").setTabCompleter(new autoCompleter_AParkour());
	}

	private void registerEvents() {
		Bukkit.getPluginManager().registerEvents(new event_Click(), this);
		Bukkit.getPluginManager().registerEvents(new event_Plate_Start(), this);
		Bukkit.getPluginManager().registerEvents(new event_Plate_End(), this);
		Bukkit.getPluginManager().registerEvents(new event_Fly(), this);
		Bukkit.getPluginManager().registerEvents(new event_Fall(), this);
		Bukkit.getPluginManager().registerEvents(new event_Others(), this);
		Bukkit.getPluginManager().registerEvents(new event_InventoryGUI(), this);
	}
}
