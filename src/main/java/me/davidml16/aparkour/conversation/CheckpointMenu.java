package me.davidml16.aparkour.conversation;

import me.davidml16.aparkour.Main;
import me.davidml16.aparkour.data.Parkour;
import me.davidml16.aparkour.data.Plate;
import me.davidml16.aparkour.data.Reward;
import me.davidml16.aparkour.managers.ColorManager;
import me.davidml16.aparkour.managers.PluginManager;
import me.davidml16.aparkour.utils.Sounds;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;

public class CheckpointMenu implements ConversationAbandonedListener, CommonPrompts {
    public Conversation getConversation(Player paramPlayer, Parkour parkour) {
        Conversation conversation = (new ConversationFactory(Main.getInstance())).withModality(true).withLocalEcho(false).withFirstPrompt(new CheckpointMenuOptions()).withTimeout(3600).thatExcludesNonPlayersWithMessage("").addConversationAbandonedListener(this).buildConversation(paramPlayer);
        conversation.getContext().setSessionData("player", paramPlayer);
        conversation.getContext().setSessionData("parkour", parkour);
        return conversation;
    }

    public Conversation getConversation(Player paramPlayer) { return getConversation(paramPlayer, null); }

    public void conversationAbandoned(ConversationAbandonedEvent paramConversationAbandonedEvent) {}

    public class CheckpointMenuOptions extends FixedSetPrompt {
        CheckpointMenuOptions() { super("1", "2", "3"); }

        protected Prompt acceptValidatedInput(ConversationContext param1ConversationContext, String param1String) {
            Parkour parkour = (Parkour) param1ConversationContext.getSessionData("parkour");
            Player player = (Player) param1ConversationContext.getSessionData("player");
            switch (param1String) {
                case "1":
                    Location loc = player.getLocation().getBlock().getLocation();
                    if(!checkpointExist(parkour, loc)) {
                        if(parkour.getCheckpoints().size() < 21) {
                            Plate checkpoint = new Plate(loc);
                            parkour.getCheckpoints().add(checkpoint);
                            parkour.getCheckpointLocations().add(loc);

                            Block block = loc.getWorld().getBlockAt(loc);
                            if(block.getType() != Material.IRON_PLATE) {
                                block.setType(Material.IRON_PLATE);
                            }

                            Main.getInstance().getParkourHandler().loadCheckpointHologram(parkour, checkpoint);
                            param1ConversationContext.getForWhom().sendRawMessage("\n" + ColorManager.translate(Main.getInstance().getLanguageHandler().getPrefix()
                                    + " &aAdded checkpoint &e#" + parkour.getCheckpoints().size() + " &ato parkour &e" + parkour.getId()));
                            Main.getInstance().getCheckpointsGUI().reloadGUI(parkour.getId());
                            Sounds.playSound(player, player.getLocation(), Sounds.MySound.CLICK, 10, 2);
                        } else {
                            param1ConversationContext.getForWhom().sendRawMessage("\n" + ColorManager.translate(Main.getInstance().getLanguageHandler().getPrefix()
                                    + " &cReached checkpoint limit (21) for parkour &e" + parkour.getId()));
                            Sounds.playSound(player, player.getLocation(), Sounds.MySound.NOTE_PLING, 10, 0);
                        }
                    } else {
                        param1ConversationContext.getForWhom().sendRawMessage("\n" + ColorManager.translate(Main.getInstance().getLanguageHandler().getPrefix()
                                + " &cThis checkpoint location already exist in parkour &e" + parkour.getId()));
                        Sounds.playSound(player, player.getLocation(), Sounds.MySound.NOTE_PLING, 10, 0);
                    }
                    return this;
                case "2":
                    if(parkour.getCheckpoints().size() > 0) {
                        if(parkour.getCheckpoints().get(parkour.getCheckpoints().size() - 1).getHologram() != null)
                            parkour.getCheckpoints().get(parkour.getCheckpoints().size() - 1).getHologram().delete();
                        parkour.getCheckpoints().remove(parkour.getCheckpoints().size() - 1);

                        Location loc2 = parkour.getCheckpointLocations().get(parkour.getCheckpointLocations().size() - 1);
                        Block block = loc2.getWorld().getBlockAt(loc2);
                        if(block.getType() == Material.IRON_PLATE) {
                            block.setType(Material.AIR);
                        }

                        parkour.getCheckpointLocations().remove(parkour.getCheckpointLocations().size() - 1);
                        param1ConversationContext.getForWhom().sendRawMessage("\n" + ColorManager.translate(Main.getInstance().getLanguageHandler().getPrefix()
                                + " &aRemoved checkpoint &e#" + (parkour.getCheckpoints().size() + 1) + " &afrom parkour &e" + parkour.getId()));
                        Main.getInstance().getCheckpointsGUI().reloadGUI(parkour.getId());
                        Sounds.playSound(player, player.getLocation(), Sounds.MySound.CLICK, 10, 2);
                    } else {
                        param1ConversationContext.getForWhom().sendRawMessage("\n" + ColorManager.translate(Main.getInstance().getLanguageHandler().getPrefix()
                                + " &cThere are no checkpoints in parkour &e" + parkour.getId()));
                        Sounds.playSound(player, player.getLocation(), Sounds.MySound.NOTE_PLING, 10, 0);
                    }
                    return this;
                case "3":
                    parkour.saveParkour();
                    param1ConversationContext.getForWhom().sendRawMessage("\n" + ColorManager.translate(Main.getInstance().getLanguageHandler().getPrefix()
                            + " &aSaved data of parkour &e" + parkour.getId() + " &awithout errors!"));
                    Sounds.playSound(player, player.getLocation(), Sounds.MySound.ANVIL_USE, 10, 3);
                    return Prompt.END_OF_CONVERSATION;
            }
            return null;
        }


        public String getPromptText(ConversationContext param1ConversationContext) {
            String cadena = "";
            cadena += ChatColor.GOLD + "" + ChatColor.BOLD + "\n  PARKOUR CHECKPOINT CREATION MENU\n";
            cadena += ChatColor.GREEN + " \n";
            cadena += ChatColor.GREEN + "    1 " + ChatColor.GRAY + "- Add new checkpoint\n";
            cadena += ChatColor.GREEN + "    2 " + ChatColor.GRAY + "- Remove last checkpoint\n";
            cadena += ChatColor.GREEN + "    3 " + ChatColor.GRAY + "- Save and exit\n";
            cadena += ChatColor.GREEN + " \n";
            cadena += ChatColor.GOLD + "" + ChatColor.YELLOW + "  Choose the option: \n";
            cadena += ChatColor.GREEN + " \n";
            return cadena;
        }
    }

    public boolean checkpointExist(Parkour parkour, Location loc) {
        return parkour.getCheckpointLocations().contains(loc);
    }
}