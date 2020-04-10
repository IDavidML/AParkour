package me.davidml16.aparkour.conversation;

import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.data.Parkour;
import me.davidml16.aparkour.data.Plate;
import me.davidml16.aparkour.managers.ColorManager;
import me.davidml16.aparkour.utils.Sounds;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;

public class RenameMenu implements ConversationAbandonedListener, CommonPrompts {

    private Main main;
    public RenameMenu(Main main) {
        this.main = main;
    }

    public Conversation getConversation(Player paramPlayer, Parkour parkour) {
        Conversation conversation = (new ConversationFactory(main)).withModality(true).withLocalEcho(false).withFirstPrompt(new RenameMenuOptions()).withTimeout(3600).thatExcludesNonPlayersWithMessage("").addConversationAbandonedListener(this).buildConversation(paramPlayer);
        conversation.getContext().setSessionData("player", paramPlayer);
        conversation.getContext().setSessionData("parkour", parkour);
        conversation.getContext().setSessionData("parkourName", parkour.getName());
        return conversation;
    }

    public Conversation getConversation(Player paramPlayer) { return getConversation(paramPlayer, null); }

    public void conversationAbandoned(ConversationAbandonedEvent paramConversationAbandonedEvent) {}

    public class RenameMenuOptions extends FixedSetPrompt {
        RenameMenuOptions() { super("1", "2"); }

        protected Prompt acceptValidatedInput(ConversationContext param1ConversationContext, String param1String) {
            Parkour parkour = (Parkour) param1ConversationContext.getSessionData("parkour");
            Player player = (Player) param1ConversationContext.getSessionData("player");
            switch (param1String) {
                case "1":
                    return new CommonPrompts.UncoloredStringPrompt(main, this, true,ChatColor.YELLOW + "  Enter parkour name (Don't use color codes), \"cancel\" to return.\n\n ", "parkourName");
                case "2":
                    String name = (String) param1ConversationContext.getSessionData("parkourName");
                    parkour.setName(name);
                    main.getParkourHandler().getConfig(parkour.getId()).set("parkour.name", name);
                    parkour.saveParkour();
                    param1ConversationContext.getForWhom().sendRawMessage("\n" + ColorManager.translate(main.getLanguageHandler().getPrefix()
                            + " &aSaved data of parkour &e" + parkour.getId() + " &awithout errors!"));
                    Sounds.playSound(player, player.getLocation(), Sounds.MySound.ANVIL_USE, 10, 3);
                    main.getConfigGUI().open(player, parkour.getId());
                    return Prompt.END_OF_CONVERSATION;
            }
            return null;
        }


        public String getPromptText(ConversationContext param1ConversationContext) {
            String cadena = "";
            cadena += ChatColor.GOLD + "" + ChatColor.BOLD + "\n  PARKOUR RENAME MENU\n";
            cadena += ChatColor.GREEN + " \n";
            cadena += ChatColor.GREEN + "    1 " + ChatColor.GRAY + "- Rename parkour (" + ChatColor.YELLOW + ChatColor.translateAlternateColorCodes('&', (String)param1ConversationContext.getSessionData("parkourName")) + ChatColor.GRAY + ")\n";
            cadena += ChatColor.GREEN + "    2 " + ChatColor.GRAY + "- Save and exit\n";
            cadena += ChatColor.GREEN + " \n";
            cadena += ChatColor.GOLD + "" + ChatColor.YELLOW + "  Choose the option: \n";
            cadena += ChatColor.GREEN + " \n";
            return cadena;
        }
    }

}