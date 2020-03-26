package me.davidml16.aparkour;

import java.sql.SQLException;
import java.util.UUID;

import me.davidml16.aparkour.events.*;
import me.davidml16.aparkour.gui.ParkourConfig_GUI;
import me.davidml16.aparkour.gui.WalkableBlocks_GUI;
import me.davidml16.aparkour.managers.*;
import me.davidml16.aparkour.tasks.ReturnTask;
import me.davidml16.aparkour.utils.RestartItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import me.davidml16.aparkour.commands.TabCompleter_AParkour;
import me.davidml16.aparkour.commands.Command_AParkour;
import me.davidml16.aparkour.database.ADatabase;
import me.davidml16.aparkour.gui.ParkourRanking_GUI;
import me.davidml16.aparkour.gui.PlayerStats_GUI;
import me.davidml16.aparkour.handlers.DatabaseHandler;
import me.davidml16.aparkour.handlers.LanguageHandler;
import me.davidml16.aparkour.handlers.ParkourHandler;
import me.davidml16.aparkour.handlers.PlayerDataHandler;
import me.davidml16.aparkour.handlers.RewardHandler;
import me.davidml16.aparkour.tasks.HologramTask;
import me.davidml16.aparkour.utils.RandomFirework;

public class Main extends JavaPlugin {

    public static Main instance;
    public static ConsoleCommandSender log;

    private PlayerStats_GUI statsGUI;
    private ParkourRanking_GUI rankingsGUI;
    private ParkourConfig_GUI configGUI;
    private WalkableBlocks_GUI walkableBlocksGUI;

    private HologramTask hologramTask;
    private ReturnTask returnTask;

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

    private boolean hologramsEnabled;

    public void onEnable() {
        instance = this;
        metrics = new MetricsLite(this, 6728);
        log = Bukkit.getConsoleSender();

        saveDefaultConfig();
        reloadConfig();

        hologramsEnabled = getConfig().getBoolean("Hologram.Enabled");
        if (isHologramsEnabled()) {
            if (!Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")
                    || !Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
                getLogger().severe("*** HolographicDisplays / ProtocolLib is not installed or not enabled. ***");
                getLogger().severe("*** This plugin will be disabled. ***");
                setEnabled(false);
                return;
            }
        }

        if (Main.getInstance().getConfig().getBoolean("RestartItem.Enabled")) {
            RestartItemUtil.loadReturnItem();
        }

        languageHandler = new LanguageHandler(getConfig().getString("Language").toLowerCase());
        languageHandler.pushMessages();

        statsHologramManager = new StatsHologramManager();
        topHologramManager = new TopHologramManager(getConfig().getInt("Tasks.ReloadInterval"));

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

        statsGUI = new PlayerStats_GUI();

        rankingsGUI = new ParkourRanking_GUI();
        rankingsGUI.loadGUI();

        configGUI = new ParkourConfig_GUI();
        configGUI.loadGUI();

        walkableBlocksGUI = new WalkableBlocks_GUI();
        walkableBlocksGUI.loadGUI();

        topHologramManager.loadTopHolograms();
        topHologramManager.restartTimeLeft();

        hologramTask = new HologramTask();
        hologramTask.start();

        returnTask = new ReturnTask();
        returnTask.start();

        registerEvents();
        registerCommands();
        RandomFirework.loadFireworks();

        for (Player p : Bukkit.getOnlinePlayers()) {
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

        PluginManager.removePlayersFromParkour();

        if(isHologramsEnabled()) {
            for (Hologram hologram : HologramsAPI.getHolograms(this)) {
                hologram.delete();
            }
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

    public PlayerStats_GUI getStatsGUI() {
        return statsGUI;
    }

    public ParkourRanking_GUI getRankingsGUI() {
        return rankingsGUI;
    }

    public ParkourConfig_GUI getConfigGUI() {
        return configGUI;
    }

    public WalkableBlocks_GUI getWalkableBlocksGUI() {
        return walkableBlocksGUI;
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

    public HologramTask getHologramTask() {
        return hologramTask;
    }

    public ReturnTask getReturnTask() {
        return returnTask;
    }

    public boolean isHologramsEnabled() {
        return hologramsEnabled;
    }

    public void setHologramsEnabled(boolean hologramsEnabled) {
        this.hologramsEnabled = hologramsEnabled;
    }

    private void registerCommands() {
        getCommand("aparkour").setExecutor(new Command_AParkour());
        getCommand("aparkour").setTabCompleter(new TabCompleter_AParkour());
    }

    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new Event_Click(), this);
        Bukkit.getPluginManager().registerEvents(new Event_PlateStart(), this);
        Bukkit.getPluginManager().registerEvents(new Event_PlateEnd(), this);
        Bukkit.getPluginManager().registerEvents(new Event_Fly(), this);
        Bukkit.getPluginManager().registerEvents(new Event_Fall(), this);
        Bukkit.getPluginManager().registerEvents(new Event_Others(), this);
        Bukkit.getPluginManager().registerEvents(new Event_InventoryGUI(), this);
    }
}
