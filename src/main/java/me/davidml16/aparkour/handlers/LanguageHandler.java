package me.davidml16.aparkour.handlers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.configuration.file.YamlConfiguration;

import me.davidml16.aparkour.Main;

public class LanguageHandler {

	private String prefix = "§9§lAParkour §l§o§f>> §r";
	private String language = null;

	private HashMap<String, String> messages;
	
	public LanguageHandler(String language) {
		new File(Main.getInstance().getDataFolder().toString() + "/language").mkdirs();
		loadEnglish();
		loadSpanish();
		this.language = checkLanguage(language);
		this.messages = new HashMap<String, String>();
	}

	public String getLanguage() {
		return language;
	}

	public String getPrefix() {
		return prefix;
	}
	
	public void setLanguage(String language) {
		this.language = language;
	}
	
	public String getMessage(String message, boolean prefix) {
		return (prefix ? this.prefix : "") + messages.get(message).replaceAll("&", "§");
	}

	public String checkLanguage(String lang) {
		File f = new File("plugins/AParkour/language/messages_" + lang + ".yml");
		if(f.exists())
			return lang;
		return "en";
	}

	public void pushMessages() {
		File f = new File("plugins/AParkour/language/messages_" + language + ".yml");
		YamlConfiguration c = YamlConfiguration.loadConfiguration(f);
		
		messages.clear();
		messages.put("COMMANDS_NOPERMS", c.getString("Commands.NoPerms"));
		messages.put("COMMANDS_NOSTATS", c.getString("Commands.NoStats"));
		messages.put("COMMANDS_NOPARKOURS", c.getString("Commands.NoParkours"));
		messages.put("COMMANDS_RELOAD", c.getString("Commands.Reload"));
		messages.put("GUI_STATS_TITLE", c.getString("GUIs.Stats.title"));
		messages.put("GUI_TOP_TITLE", c.getString("GUIs.Top.title"));
		messages.put("TIMES_HOURS", c.getString("Times.Hours"));
		messages.put("TIMES_HOUR", c.getString("Times.Hour"));
		messages.put("TIMES_MINUTES", c.getString("Times.Minutes"));
		messages.put("TIMES_MINUTE", c.getString("Times.Minute"));
		messages.put("TIMES_SECONDS", c.getString("Times.Seconds"));
		messages.put("TIMES_SECOND", c.getString("Times.Second"));
		messages.put("TIMES_NOBESTTIME", c.getString("Times.NoBestTime"));
		messages.put("TIMER_ACTIONBAR", c.getString("Timer.ActionBar"));
		messages.put("HOLOGRAMS_STATS_LINE1", c.getString("Holograms.Stats.Line1"));
		messages.put("HOLOGRAMS_STATS_LINE2", c.getString("Holograms.Stats.Line2"));
		messages.put("HOLOGRAMS_TOP_HEADER_LINE1", c.getString("Holograms.Top.Header.Line1"));
		messages.put("HOLOGRAMS_TOP_HEADER_LINE2", c.getString("Holograms.Top.Header.Line2"));
		messages.put("HOLOGRAMS_TOP_BODY_LINE", c.getString("Holograms.Top.Body.Line"));
		messages.put("HOLOGRAMS_TOP_BODY_NOTIME", c.getString("Holograms.Top.Body.NoTime"));
		messages.put("HOLOGRAMS_TOP_FOOTER_LINE", c.getString("Holograms.Top.Footer.Line"));
		messages.put("HOLOGRAMS_TOP_FOOTER_UPDATING", c.getString("Holograms.Top.Footer.Updating"));
		messages.put("MESSAGES_STARTED", c.getString("Messages.Started"));
		messages.put("MESSAGES_FLY", c.getString("Messages.Fly"));
		messages.put("MESSAGES_RETURN", c.getString("Messages.Return"));
		messages.put("ENDMESSAGE_FIRSTTIME", c.getString("EndMessage.FirstTime"));
		messages.put("ENDMESSAGE_NORMAL", c.getString("EndMessage.Normal"));
		messages.put("ENDMESSAGE_RECORD", c.getString("EndMessage.Record"));
	}

	public void loadEnglish() {
		File file = new File(Main.getInstance().getDataFolder() + "/language/messages_en.yml");
		if (!file.exists()) {
			try {
				file.createNewFile();
				YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

				cfg.set("Commands.NoPerms", "&cYou dont have permissions to use this command!");
				cfg.set("Commands.NoStats", "&cYou dont have statistics at this moment!");
				cfg.set("Commands.NoParkours", "&cAny parkour created at this moment!");
				cfg.set("Commands.Reload", "&aPlugin reloaded with no errors!");

				cfg.set("GUIs.Stats.title", "Parkour Statistics");
				cfg.set("GUIs.Top.title", "Parkour Top Players");

				cfg.set("Times.Hours", "hours");
				cfg.set("Times.Hour", "hour");
				cfg.set("Times.Minutes", "minutes");
				cfg.set("Times.Minute", "minute");
				cfg.set("Times.Seconds", "seconds");
				cfg.set("Times.Second", "second");
				cfg.set("Times.NoBestTime", "N/A");

				cfg.set("Timer.ActionBar", "&e&lCurrent Time: &6%currentTime% &7- &e&lBest Time: &6%bestTime%");

				cfg.set("Holograms.Stats.Line1", "&a%player%'s %parkour% parkour stats");
				cfg.set("Holograms.Stats.Line2", "&aBest time&7: &6%time%");
				cfg.set("Holograms.Top.Header.Line1", "&a&lTOP PARKOUR TIMES");
				cfg.set("Holograms.Top.Header.Line2", "&7- %parkour% -");
				cfg.set("Holograms.Top.Body.Line", "&e%position%. &a%player% &7- &6%time%");
				cfg.set("Holograms.Top.Body.NoTime", "&e%position%. &cN/A");
				cfg.set("Holograms.Top.Footer.Line", "&aUpdating: &6%time%");
				cfg.set("Holograms.Top.Footer.Updating", "&aUpdating: &cLoading...");

				cfg.set("Messages.Started", "&aStarted parkour! Get to the end as quick as possible.");
				cfg.set("Messages.Fly", "&cYou can not fly when you are in parkour.");
				cfg.set("Messages.Return", "&6Returning to beginning of parkour...");

				cfg.set("EndMessage.FirstTime", "&6You completed this parkour for the first time!");
				cfg.set("EndMessage.Normal", "&6You completed the parkour in &d%endTime%");
				cfg.set("EndMessage.Record", "&6You beat your previous best time by &d%recordTime%");

				cfg.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void loadSpanish() {
		File file = new File(Main.getInstance().getDataFolder() + "/language/messages_es.yml");
		if (!file.exists()) {
			try {
				file.createNewFile();
				YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

				cfg.set("Commands.NoPerms", "&cNo tienes permisos para usar este comando!");
				cfg.set("Commands.NoStats", "&cNo tienes estadísticas en este momento!");
				cfg.set("Commands.NoParkours", "&cNo hay ningún parkour creado de momento!");
				cfg.set("Commands.Reload", "&aPlugin recargado sin errores!");

				cfg.set("GUIs.Stats.title", "Estadísticas del Parkour");
				cfg.set("GUIs.Top.title", "Top jugadores del Parkour");

				cfg.set("Times.Hours", "horas");
				cfg.set("Times.Hour", "hora");
				cfg.set("Times.Minutes", "minutos");
				cfg.set("Times.Minute", "minuto");
				cfg.set("Times.Seconds", "segundos");
				cfg.set("Times.Second", "segundo");
				cfg.set("Times.NoBestTime", "N/A");

				cfg.set("Timer.ActionBar", "&e&lTiempo actual: &6%currentTime% &7- &e&lMejor tiempo: &6%bestTime%");

				cfg.set("Holograms.Stats.Line1", "&aEstadísticas de %player% en %parkour%");
				cfg.set("Holograms.Stats.Line2", "&aMejor tiempo&7: &6%time%");
				cfg.set("Holograms.Top.Header.Line1", "&a&lTOP MEJORES TIEMPOS");
				cfg.set("Holograms.Top.Header.Line2", "&7- %parkour% -");
				cfg.set("Holograms.Top.Body.Line", "&e%position%. &a%player% &7- &6%time%");
				cfg.set("Holograms.Top.Body.NoTime", "&e%position%. &cN/A");
				cfg.set("Holograms.Top.Footer.Line", "&aActualización: &6%time%");
				cfg.set("Holograms.Top.Footer.Updating", "&aActualización: &cCargando...");

				cfg.set("Messages.Started", "&aParkour iniciado! Llega al final lo más rapido que puedeas.");
				cfg.set("Messages.Fly", "&cNo puedes volar cuando estas en el parkour.");
				cfg.set("Messages.Return", "&6Volviendo al inicio del parkour...");

				cfg.set("EndMessage.FirstTime", "&6Has completado este parkour por primera vez!");
				cfg.set("EndMessage.Normal", "&6Has completado el parkour en &d%endTime%");
				cfg.set("EndMessage.Record", "&6Has mejorado tu anterior tiempo por &d%recordTime%");

				cfg.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
