package me.davidml16.aparkour.handlers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import me.davidml16.aparkour.managers.ColorManager;
import org.bukkit.ChatColor;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;

import me.davidml16.aparkour.Main;
import org.bukkit.entity.Player;

public class LanguageHandler {

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

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getPrefix() {
		return ColorManager.translate(messages.get("Prefix"));
	}

	public String getMessage(String message) {
		return ColorManager.translate(messages.get(message).replaceAll("%prefix%", messages.get("Prefix")));
	}

	public String checkLanguage(String lang) {
		File f = new File("plugins/AParkour/language/messages_" + lang + ".yml");
		if(f.exists())
			return lang;
		return "en";
	}

	public void pushMessages() {
		Main.log.sendMessage(ColorManager.translate(""));
		Main.log.sendMessage(ColorManager.translate("  &eLoading language:"));

		File f = new File("plugins/AParkour/language/messages_" + language + ".yml");
		YamlConfiguration c = YamlConfiguration.loadConfiguration(f);

		for(String key : c.getKeys(true)) {
			if(!(c.get(key) instanceof MemorySection)) {
				messages.put(key, c.getString(key));
			}
		}

		Main.log.sendMessage(ColorManager.translate("    &a'" + language + "' loaded!"));
	}

	public void loadEnglish() {
		File file = new File(Main.getInstance().getDataFolder() + "/language/messages_en.yml");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

		cfg.options().header("\n\nThis is the messsages file.\nYou can change any messages that are in this file\n\nIf you want to reset a message back to the default,\ndelete the entire line the message is on and restart the server.\n\t\n\t");

		Map<String, String> msgDefaults = new LinkedHashMap<String, String>();
		msgDefaults.put("Prefix", "&9&lAParkour &l&o&f>>&r");
		msgDefaults.put("Commands.NoPerms", "%prefix% &cYou dont have permissions to use this command!");
		msgDefaults.put("Commands.NoStats", "%prefix% &cYou dont haves statistics at this moment!");
		msgDefaults.put("Commands.NoParkours", "%prefix% &cAny parkour created at this moment!");
		msgDefaults.put("Commands.Reload", "%prefix% &aPlugin reloaded with no errors!");

		msgDefaults.put("GUIs.Stats.title", "Parkour Statistics");
		msgDefaults.put("GUIs.Top.title", "Parkour Top Players");
		msgDefaults.put("GUIs.Config.title", "%parkour% | Configuration");
		msgDefaults.put("GUIs.WalkableBlocks.title", "%parkour% | Blocks");
		msgDefaults.put("GUIs.Rewards.title", "%parkour% | Rewards");

		msgDefaults.put("Times.Hours", "hours");
		msgDefaults.put("Times.Hour", "hour");
		msgDefaults.put("Times.Minutes", "minutes");
		msgDefaults.put("Times.Minute", "minute");
		msgDefaults.put("Times.Seconds", "seconds");
		msgDefaults.put("Times.Second", "second");
		msgDefaults.put("Times.NoBestTime", "N/A");

		msgDefaults.put("Timer.ActionBar", "&e&lCurrent Time: &6%currentTime% &7- &e&lBest Time: &6%bestTime%");

		msgDefaults.put("Holograms.Stats.Line1", "&a%player%'s %parkour% parkour stats");
		msgDefaults.put("Holograms.Stats.Line2", "&aBest time&7: &6%time%");
		msgDefaults.put("Holograms.Top.Header.Line1", "&a&lTOP PARKOUR TIMES");
		msgDefaults.put("Holograms.Top.Header.Line2", "&7- %parkour% -");
		msgDefaults.put("Holograms.Top.Body.Line", "&e%position%. &a%player% &7- &6%time%");
		msgDefaults.put("Holograms.Top.Body.NoTime", "&e%position%. &cN/A");
		msgDefaults.put("Holograms.Top.Footer.Line", "&aUpdating: &6%time%");
		msgDefaults.put("Holograms.Top.Footer.Updating", "&aUpdating: &cLoading...");
		msgDefaults.put("Holograms.Plates.Start.Line1", "&e&lParkour Challenge");
		msgDefaults.put("Holograms.Plates.Start.Line2", "&fStart here!");
		msgDefaults.put("Holograms.Plates.End.Line1", "&e&lParkour Challenge");
		msgDefaults.put("Holograms.Plates.End.Line2", "&fFinish here!");

		msgDefaults.put("Messages.Started", "&aStarted parkour! Get to the end as quick as possible.");
		msgDefaults.put("Messages.Fly", "&cYou can not fly when you are in parkour.");
		msgDefaults.put("Messages.Return", "&6Returning to beginning of parkour...");

		msgDefaults.put("EndMessage.FirstTime", "&6You completed this parkour for the first time!");
		msgDefaults.put("EndMessage.Normal", "&6You completed the parkour in &d%endTime%");
		msgDefaults.put("EndMessage.Record", "&6You beat your previous best time by &d%recordTime%");

		for (String key : msgDefaults.keySet()) {
			if (!cfg.isSet(key)) {
				cfg.set(key, msgDefaults.get(key));
			}
		}

		try {
			cfg.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void loadSpanish() {
		File file = new File(Main.getInstance().getDataFolder() + "/language/messages_es.yml");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

		cfg.options().header("\n\nThis is the messsages file.\nYou can change any messages that are in this file\n\nIf you want to reset a message back to the default,\ndelete the entire line the message is on and restart the server.\n\t\n\t");

		Map<String, String> msgDefaults = new LinkedHashMap<String, String>();
		msgDefaults.put("Prefix", "&9&lAParkour &l&o&f>>&r");
		msgDefaults.put("Commands.NoPerms", "%prefix% &cNo tienes permisos para usar este comando!");
		msgDefaults.put("Commands.NoStats", "%prefix% &cNo tienes estadísticas en este momento!");
		msgDefaults.put("Commands.NoParkours", "%prefix% &cNo hay ningún parkour creado de momento!");
		msgDefaults.put("Commands.Reload", "%prefix% &aPlugin recargado sin errores!");

		msgDefaults.put("GUIs.Stats.title", "Estadísticas del Parkour");
		msgDefaults.put("GUIs.Top.title", "Top jugadores del Parkour");
		msgDefaults.put("GUIs.Config.title", "%parkour% | Configuración");
		msgDefaults.put("GUIs.WalkableBlocks.title", "%parkour% | Bloques");
		msgDefaults.put("GUIs.Rewards.title", "%parkour% | Recompensas");

		msgDefaults.put("Times.Hours", "horas");
		msgDefaults.put("Times.Hour", "hora");
		msgDefaults.put("Times.Minutes", "minutos");
		msgDefaults.put("Times.Minute", "minuto");
		msgDefaults.put("Times.Seconds", "segundos");
		msgDefaults.put("Times.Second", "segundo");
		msgDefaults.put("Times.NoBestTime", "N/A");

		msgDefaults.put("Timer.ActionBar", "&e&lTiempo actual: &6%currentTime% &7- &e&lMejor tiempo: &6%bestTime%");

		msgDefaults.put("Holograms.Stats.Line1", "&aEstadísticas de %player% en %parkour%");
		msgDefaults.put("Holograms.Stats.Line2", "&aMejor tiempo&7: &6%time%");
		msgDefaults.put("Holograms.Top.Header.Line1", "&a&lTOP MEJORES TIEMPOS");
		msgDefaults.put("Holograms.Top.Header.Line2", "&7- %parkour% -");
		msgDefaults.put("Holograms.Top.Body.Line", "&e%position%. &a%player% &7- &6%time%");
		msgDefaults.put("Holograms.Top.Body.NoTime", "&e%position%. &cN/A");
		msgDefaults.put("Holograms.Top.Footer.Line", "&aActualización: &6%time%");
		msgDefaults.put("Holograms.Top.Footer.Updating", "&aActualización: &cCargando...");
		msgDefaults.put("Holograms.Plates.Start.Line1", "&e&lReto parkour");
		msgDefaults.put("Holograms.Plates.Start.Line2", "&fEmpieza aquí!");
		msgDefaults.put("Holograms.Plates.End.Line1", "&e&lReto parkour");
		msgDefaults.put("Holograms.Plates.End.Line2", "&fTermina aquí!");

		msgDefaults.put("Messages.Started", "&aParkour iniciado! Llega al final lo más rapido que puedeas.");
		msgDefaults.put("Messages.Fly", "&cNo puedes volar cuando estas en el parkour.");
		msgDefaults.put("Messages.Return", "&6Volviendo al inicio del parkour...");

		msgDefaults.put("EndMessage.FirstTime", "&6Has completado este parkour por primera vez!");
		msgDefaults.put("EndMessage.Normal", "&6Has completado el parkour en &d%endTime%");
		msgDefaults.put("EndMessage.Record", "&6Has mejorado tu anterior tiempo por &d%recordTime%");

		for (String key : msgDefaults.keySet()) {
			if (!cfg.isSet(key)) {
				cfg.set(key, msgDefaults.get(key));
			}
		}

		try {
			cfg.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
