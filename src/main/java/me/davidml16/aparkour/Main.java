package me.davidml16.aparkour;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;

import me.davidml16.aparkour.placeholders.PlaceholderHook;
import me.davidml16.aparkour.api.ParkourAPI;
import me.davidml16.aparkour.data.CommandBlocker;
import me.davidml16.aparkour.database.DatabaseHandler;
import me.davidml16.aparkour.database.types.Database;
import me.davidml16.aparkour.enums.CommandBlockType;
import me.davidml16.aparkour.events.*;
import me.davidml16.aparkour.gui.*;
import me.davidml16.aparkour.handlers.*;
import me.davidml16.aparkour.managers.*;
import me.davidml16.aparkour.tasks.ReturnTask;
import me.davidml16.aparkour.utils.*;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import me.davidml16.aparkour.commands.TabCompleter_AParkour;
import me.davidml16.aparkour.commands.Command_AParkour;
import me.davidml16.aparkour.tasks.HologramTask;

public class Main extends JavaPlugin {

    public static ConsoleCommandSender log;
    private static Main instance;

    private PlayerStats_GUI statsGUI;
    private MainConfig_GUI configGUI;
    private WalkableBlocks_GUI walkableBlocksGUI;
    private Rewards_GUI rewardsGUI;
    private Checkpoints_GUI checkpointsGUI;
    private Holograms_GUI hologramsGUI;
    private Titles_GUI titlesGUI;
    private Miscellaneous_GUI miscellaneousGUI;
    private PlayParkour_GUI playParkourGUI;

    private HologramTask hologramTask;
    private ReturnTask returnTask;

    private TimerManager timerManager;
    private StatsHologramManager statsHologramManager;
    private TopHologramManager topHologramManager;
    private PlateManager plateManager;
    private PluginManager pluginManager;

    private ParkourHandler parkourHandler;
    private RewardHandler rewardHandler;
    private CheckpointsHandler checkpointsHandler;
    private LanguageHandler languageHandler;
    private PlayerDataHandler playerDataHandler;
    private DatabaseHandler databaseHandler;
    private SessionHandler sessionHandler;
    private LeaderboardHandler leaderboardHandler;

    private SoundUtil soundUtil;
    private LocationUtil locationUtil;
    private TitleUtil titleUtil;

    private ParkourItems parkourItems;

    private CommandBlocker commandBlocker;

    private MetricsLite metrics;

    private ParkourAPI parkourAPI;

    private boolean hologramsEnabled;
    private boolean parkourItemsEnabled;
    private boolean kickParkourOnFail;
    private boolean joinByGUI;

    private Chat chat;

    public void onEnable() {
        instance = this;
        metrics = new MetricsLite(this, 6728);
        log = Bukkit.getConsoleSender();

        saveDefaultConfig();
        try {
            ConfigUpdater.update(this, "config.yml", new File(getDataFolder(), "config.yml"), Collections.emptyList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        reloadConfig();

        hologramsEnabled = getConfig().getBoolean("Hologram.Enabled");
        if (isHologramsEnabled()) {
            if (!Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays") || !Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
                getLogger().severe("*** HolographicDisplays / ProtocolLib is not installed or not enabled. ***");
                getLogger().severe("*** This plugin will be disabled. ***");
                setEnabled(false);
                return;
            }
        }

        parkourItems = new ParkourItems(this);
        parkourItemsEnabled = getConfig().getBoolean("Items.Enabled");
        if (parkourItemsEnabled) {
            parkourItems.loadReturnItem();
            parkourItems.loadCheckpointItem();
        }

        kickParkourOnFail = getConfig().getBoolean("KickParkourOnFail.Enabled");

        joinByGUI = getConfig().getBoolean("JoinParkourByGUI");

        languageHandler = new LanguageHandler(this, getConfig().getString("Language").toLowerCase());
        languageHandler.pushMessages();

        statsHologramManager = new StatsHologramManager(this);

        checkpointsHandler = new CheckpointsHandler();

        plateManager = new PlateManager();

        parkourHandler = new ParkourHandler(this);
        parkourHandler.loadParkours();
        parkourHandler.loadHolograms();

        rewardHandler = new RewardHandler(this);
        rewardHandler.loadRewards();

        databaseHandler = new DatabaseHandler(this);
        databaseHandler.openConnection();
        databaseHandler.getDatabase().loadTables();

        playerDataHandler = new PlayerDataHandler(this);
        sessionHandler = new SessionHandler(this);

        leaderboardHandler = new LeaderboardHandler(this);

        timerManager = new TimerManager(this);

        pluginManager = new PluginManager(this);

        statsGUI = new PlayerStats_GUI(this);

        playParkourGUI = new PlayParkour_GUI(this);

        configGUI = new MainConfig_GUI(this);
        configGUI.loadGUI();

        walkableBlocksGUI = new WalkableBlocks_GUI(this);
        walkableBlocksGUI.loadGUI();

        rewardsGUI = new Rewards_GUI(this);
        rewardsGUI.loadGUI();

        checkpointsGUI = new Checkpoints_GUI(this);
        checkpointsGUI.loadGUI();

        hologramsGUI = new Holograms_GUI(this);
        hologramsGUI.loadGUI();

        titlesGUI = new Titles_GUI(this);
        titlesGUI.loadGUI();

        miscellaneousGUI = new Miscellaneous_GUI(this);
        miscellaneousGUI.loadGUI();

        topHologramManager = new TopHologramManager(this, getConfig().getInt("Hologram.ReloadInterval"));
        topHologramManager.loadTopHolograms();
        topHologramManager.restartTimeLeft();

        hologramTask = new HologramTask(this);
        hologramTask.start();

        returnTask = new ReturnTask(this);
        returnTask.start();

        soundUtil = new SoundUtil(this);
        locationUtil = new LocationUtil(this);
        titleUtil = new TitleUtil(this);

        commandBlocker = new CommandBlocker();
        commandBlocker.setCommands(getConfig().getStringList("CommandBlocker.Commands"));
        commandBlocker.setType(CommandBlockType.valueOf(getConfig().getString("CommandBlocker.Type").toUpperCase()));

        parkourAPI = new ParkourAPI();

        registerEvents();
        registerCommands();
        RandomFirework.loadFireworks();

        for (Player p : Bukkit.getOnlinePlayers()) {
            playerDataHandler.loadPlayerData(p);
            try {
                databaseHandler.getDatabase().updatePlayerName(p);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            statsHologramManager.loadStatsHolograms(p);
        }

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PlaceholderHook(this).register();
        }

        if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            setupChat();
        }

        PluginDescriptionFile pdf = getDescription();
        log.sendMessage("");
        log.sendMessage(ColorManager.translate("  &eAParkour Enabled!"));
        log.sendMessage(ColorManager.translate("    &aVersion: &b" + pdf.getVersion()));
        log.sendMessage(ColorManager.translate("    &aAuthor: &b" + pdf.getAuthors().get(0)));
        log.sendMessage("");

        if(getConfig().getBoolean("CheckUpdates")) {
            Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
                try {
                    new UpdateChecker(this).getVersion(version -> {
                        Main.log.sendMessage(ColorManager.translate(""));
                        Main.log.sendMessage(ColorManager.translate("  &eAParkour checking updates:"));
                        if (getDescription().getVersion().equalsIgnoreCase(version)) {
                            Main.log.sendMessage(ColorManager.translate("    &cNo update found!"));
                            Main.log.sendMessage(ColorManager.translate(""));
                        } else {
                            Main.log.sendMessage(ColorManager.translate("    &aNew update found! [" + version + "]"));
                            Main.log.sendMessage(ColorManager.translate(""));
                        }
                    });
                } catch (Exception e) {
                    Main.log.sendMessage(ColorManager.translate("    &cCould not proceed update-checking"));
                    Main.log.sendMessage(ColorManager.translate(""));
                }
            });
        }
    }

    public void onDisable() {
        PluginDescriptionFile pdf = getDescription();
        log.sendMessage("");
        log.sendMessage(ColorManager.translate("  &eAParkour Disabled!"));
        log.sendMessage(ColorManager.translate("    &aVersion: &b" + pdf.getVersion()));
        log.sendMessage(ColorManager.translate("    &aAuthor: &b" + pdf.getAuthors().get(0)));
        log.sendMessage("");

        pluginManager.removePlayersFromParkour();

        if(isHologramsEnabled()) {
            for (Hologram hologram : HologramsAPI.getHolograms(this)) {
                hologram.delete();
            }
        }

        hologramTask.stop();
        databaseHandler.getDatabase().close();
    }

    private void setupChat() {
        try {
            RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
            chat = rsp.getProvider();
        } catch (NullPointerException e) {
            chat = null;
        }
    }

    public Chat getChat() { return chat; }

    public boolean vaultEnabled() { return chat != null && getConfig().getBoolean("UseVaultInHolograms"); }

    public static Main getInstance() { return instance; }

    public PlayerStats_GUI getStatsGUI() {
        return statsGUI;
    }

    public PlayParkour_GUI getPlayParkourGUI() { return playParkourGUI; }

    public MainConfig_GUI getConfigGUI() {
        return configGUI;
    }

    public WalkableBlocks_GUI getWalkableBlocksGUI() {
        return walkableBlocksGUI;
    }

    public Rewards_GUI getRewardsGUI() {
        return rewardsGUI;
    }

    public Checkpoints_GUI getCheckpointsGUI() {
        return checkpointsGUI;
    }

    public Holograms_GUI getHologramsGUI() { return hologramsGUI; }

    public Titles_GUI getTitlesGUI() { return titlesGUI; }

    public Miscellaneous_GUI getMiscellaneousGUI() { return miscellaneousGUI; }

    public TimerManager getTimerManager() {
        return timerManager;
    }

    public StatsHologramManager getStatsHologramManager() {
        return statsHologramManager;
    }

    public TopHologramManager getTopHologramManager() {
        return topHologramManager;
    }

    public PlateManager getPlateManager() {
        return plateManager;
    }

    public ParkourHandler getParkourHandler() {
        return parkourHandler;
    }

    public SessionHandler getSessionHandler() { return sessionHandler; }

    public PluginManager getPluginManager() { return pluginManager; }

    public LanguageHandler getLanguageHandler() {
        return languageHandler;
    }

    public PlayerDataHandler getPlayerDataHandler() {
        return playerDataHandler;
    }

    public Database getDatabaseHandler() {
        return databaseHandler.getDatabase();
    }

    public DatabaseHandler getDatabase() { return databaseHandler; }

    public RewardHandler getRewardHandler() {
        return rewardHandler;
    }

    public CheckpointsHandler getCheckpointsHandler() {
        return checkpointsHandler;
    }

    public LeaderboardHandler getLeaderboardHandler() { return leaderboardHandler; }

    public HologramTask getHologramTask() {
        return hologramTask;
    }

    public boolean isHologramsEnabled() {
        return hologramsEnabled;
    }

    public void setHologramsEnabled(boolean hologramsEnabled) {
        this.hologramsEnabled = hologramsEnabled;
    }

    public boolean isParkourItemsEnabled() {
        return parkourItemsEnabled;
    }

    public void setParkourItemsEnabled(boolean parkourItemsEnabled) {
        this.parkourItemsEnabled = parkourItemsEnabled;
    }

    public boolean isKickParkourOnFail() { return kickParkourOnFail; }

    public void setKickParkourOnFail(boolean kickParkourOnFail) { this.kickParkourOnFail = kickParkourOnFail; }

    public boolean isJoinByGUI() { return joinByGUI; }

    public void setJoinByGUI(boolean joinByGUI) { this.joinByGUI = joinByGUI; }

    public ParkourItems getParkourItems() {
        return parkourItems;
    }

    public SoundUtil getSoundUtil() { return soundUtil; }

    public LocationUtil getLocationUtil() { return locationUtil; }

    public TitleUtil getTitleUtil() { return titleUtil; }

    public CommandBlocker getCommandBlocker() { return commandBlocker; }

    public ParkourAPI getParkourAPI() { return parkourAPI; }

    private void registerCommands() {
        getCommand("aparkour").setExecutor(new Command_AParkour(this));
        getCommand("aparkour").setTabCompleter(new TabCompleter_AParkour(this));
    }

    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new Event_Click(this), this);
        Bukkit.getPluginManager().registerEvents(new Event_PlateStart(this), this);
        Bukkit.getPluginManager().registerEvents(new Event_PlateEnd(this), this);
        Bukkit.getPluginManager().registerEvents(new Event_PlateCheckpoint(this), this);
        Bukkit.getPluginManager().registerEvents(new Event_Fall(this), this);
        Bukkit.getPluginManager().registerEvents(new Event_Void(this), this);
        Bukkit.getPluginManager().registerEvents(new Event_Others(this), this);

        if(!Bukkit.getBukkitVersion().contains("1.8")) {
            Bukkit.getPluginManager().registerEvents(new Event_Swap(this), this);
        }

    }

}
