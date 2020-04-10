package me.davidml16.aparkour.conversation;

import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.data.Parkour;
import me.davidml16.aparkour.data.Reward;
import me.davidml16.aparkour.managers.ColorManager;
import me.davidml16.aparkour.utils.Sounds;
import org.bukkit.ChatColor;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationAbandonedListener;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.FixedSetPrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

public class RewardMenu implements ConversationAbandonedListener, CommonPrompts {

    private Main main;
    public RewardMenu(Main main) {
        this.main = main;
    }

    public Conversation getConversation(Player paramPlayer, Parkour parkour) {
        Conversation conversation = (new ConversationFactory(main)).withModality(true).withLocalEcho(false).withFirstPrompt(new RewardMenuOptions()).withTimeout(3600).thatExcludesNonPlayersWithMessage("").addConversationAbandonedListener(this).buildConversation(paramPlayer);
        conversation.getContext().setSessionData("player", paramPlayer);
        conversation.getContext().setSessionData("parkour", parkour);
        return conversation;
    }

    public Conversation getConversation(Player paramPlayer) { return getConversation(paramPlayer, null); }

    public void conversationAbandoned(ConversationAbandonedEvent paramConversationAbandonedEvent) {}

    public class RewardMenuOptions extends FixedSetPrompt {
        RewardMenuOptions() { super("1", "2", "3", "4", "5", "6", "7"); }

        protected Prompt acceptValidatedInput(ConversationContext param1ConversationContext, String param1String) {
            Parkour parkour = (Parkour) param1ConversationContext.getSessionData("parkour");
            switch (param1String) {
                case "1":
                    return new CommonPrompts.CommonStringPrompt(main, this, false,ChatColor.YELLOW + "  Enter reward identificator, \"cancel\" to return.\n\n ", "rewardID");
                case "2":
                    return new CommonPrompts.CommonStringPrompt(main, this, false, ChatColor.YELLOW + "  Enter reward permission, \"cancel\" to return.\n  Example 'aparkour.permission.reward'\n  Write * to disable permission\n\n ", "rewardPermission");
                case "3":
                    return new CommonPrompts.CommonStringPrompt(main,this, true,ChatColor.YELLOW + "  Enter reward command, \"cancel\" to return.\n  Available variables: %player%\n\n ", "rewardCommand");
                case "4":
                    return new CommonPrompts.BooleanPrompt(main, this,ChatColor.YELLOW + "  Choose if reward is only in first time, \"cancel\" to return.\n  Write 'true' or 'false'\n\n ", "rewardFirstTime");
                case "5":
                    return new CommonPrompts.CommonStringPrompt(main, this, true,ChatColor.YELLOW + "  Enter reward chance (0% to 100%), \"cancel\" to return.\n\n ", "rewardChance");
                case "6":
                    if(param1ConversationContext.getSessionData("rewardID") != null
                            && param1ConversationContext.getSessionData("rewardPermission") != null
                            && param1ConversationContext.getSessionData("rewardCommand") != null
                            && param1ConversationContext.getSessionData("rewardFirstTime") != null
                            && param1ConversationContext.getSessionData("rewardChance") != null) {
                        if(!rewardsIdExist(parkour, (String)param1ConversationContext.getSessionData("rewardID"))) {
                            String chance = (String) param1ConversationContext.getSessionData("rewardChance");
                            Reward reward = new Reward((String) param1ConversationContext.getSessionData("rewardID"),
                                    (String) param1ConversationContext.getSessionData("rewardPermission"),
                                    (String) param1ConversationContext.getSessionData("rewardCommand"),
                                    Boolean.valueOf((String) param1ConversationContext.getSessionData("rewardFirstTime")),
                                    Integer.parseInt(chance.replaceAll("%", ""))
                            );
                            parkour.getRewards().add(reward);
                            parkour.saveParkour();
                            param1ConversationContext.getForWhom().sendRawMessage("\n" + ColorManager.translate(main.getLanguageHandler().getPrefix()
                                    + " &aYou added reward &e" + reward.getId() + " &ato rewards of parkour &e" + parkour.getId()));

                            Sounds.playSound((Player) param1ConversationContext.getSessionData("player"),
                                    ((Player) param1ConversationContext.getSessionData("player")).getLocation(), Sounds.MySound.ANVIL_USE, 10, 3);
                            main.getRewardsGUI().reloadGUI(parkour.getId());
                            main.getRewardsGUI().open((Player) param1ConversationContext.getSessionData("player"), parkour.getId());
                            return Prompt.END_OF_CONVERSATION;
                        } else {
                            return new CommonPrompts.ErrorPrompt(main, this, "\n" + ChatColor.RED + "  There is already a reward with that ID, please change it and try again\n  Write anything to continue\n ");
                        }
                    } else {
                        return new CommonPrompts.ErrorPrompt(main, this, "\n" + ChatColor.RED + "  You need to setup ID, PERMISSION, COMMAND and FIRST_TIME to save reward!\n  Write anything to continue\n ");
                    }
                case "7":
                    return new CommonPrompts.ConfirmExitPrompt(main, this);
            }
            return null;
        }


        public String getPromptText(ConversationContext param1ConversationContext) {
            String cadena = "";
            cadena += ChatColor.GOLD + "" + ChatColor.BOLD + "\n  PARKOUR REWARD CREATION MENU\n";
            cadena += ChatColor.GREEN + " \n";
            if (param1ConversationContext.getSessionData("rewardID") == null) {
                cadena += ChatColor.RED + "    1 " + ChatColor.GRAY + "- Set Reward ID (" + ChatColor.RED + "none" + ChatColor.GRAY + ")\n";
            } else {
                cadena += ChatColor.GREEN + "    1 " + ChatColor.GRAY + "- Set Reward ID (" + ChatColor.YELLOW + ChatColor.translateAlternateColorCodes('&', (String)param1ConversationContext.getSessionData("rewardID")) + ChatColor.GRAY + ")\n";
            }  if (param1ConversationContext.getSessionData("rewardPermission") == null) {
                cadena += ChatColor.RED + "    2 " + ChatColor.GRAY + "- Set Reward Permission (" + ChatColor.RED + "none" + ChatColor.GRAY + ")\n";
            } else {
                cadena += ChatColor.GREEN + "    2 " + ChatColor.GRAY + "- Set Reward Permission (" + ChatColor.YELLOW + param1ConversationContext.getSessionData("rewardPermission") + ChatColor.GRAY + ")\n";
            }  if (param1ConversationContext.getSessionData("rewardCommand") == null) {
                cadena += ChatColor.RED + "    3 " + ChatColor.GRAY + "- Set Reward Command (" + ChatColor.RED + "none" + ChatColor.GRAY + ")\n";
            } else {
                cadena += ChatColor.GREEN + "    3 " + ChatColor.GRAY + "- Set Reward Command (" + ChatColor.YELLOW + ChatColor.translateAlternateColorCodes('&', (String)param1ConversationContext.getSessionData("rewardCommand")) + ChatColor.GRAY + ")\n";
            }  if (param1ConversationContext.getSessionData("rewardFirstTime") == null) {
                cadena += ChatColor.RED + "    4 " + ChatColor.GRAY + "- Set Reward FirstTime (" + ChatColor.RED + "none" + ChatColor.GRAY + ")\n";
            } else {
                cadena += ChatColor.GREEN + "    4 " + ChatColor.GRAY + "- Set Reward FirstTime (" + ChatColor.YELLOW + param1ConversationContext.getSessionData("rewardFirstTime") + ChatColor.GRAY + ")\n";
            }  if (param1ConversationContext.getSessionData("rewardChance") == null) {
                cadena += ChatColor.RED + "    5 " + ChatColor.GRAY + "- Set Reward Chance (" + ChatColor.RED + "none" + ChatColor.GRAY + ")\n";
            } else {
                cadena += ChatColor.GREEN + "    5 " + ChatColor.GRAY + "- Set Reward Chance (" + ChatColor.YELLOW + param1ConversationContext.getSessionData("rewardChance") + "%" + ChatColor.GRAY + ")\n";
            }

            cadena += ChatColor.GREEN + "    6 " + ChatColor.GRAY + "- Save\n";
            cadena += ChatColor.GREEN + "    7 " + ChatColor.GRAY + "- Exit and discard\n";
            cadena += ChatColor.GREEN + " \n";
            cadena += ChatColor.GOLD + "" + ChatColor.YELLOW + "  Choose the option: \n";
            cadena += ChatColor.GREEN + " \n";
            return cadena;
        }
    }

    private boolean rewardsIdExist(Parkour parkour, String rewardID) {
        for(Reward reward : parkour.getRewards()) {
            if(reward.getId().equalsIgnoreCase(rewardID)) return true;
        }
        return false;
    }
}