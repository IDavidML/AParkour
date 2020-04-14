package me.davidml16.aparkour.data;

import me.davidml16.aparkour.enums.CommandBlockType;

import java.util.ArrayList;
import java.util.List;

public class CommandBlocker {

    private CommandBlockType type;
    private List<String> commands;

    public CommandBlocker() {
        this.commands = new ArrayList<>();
    }

    public List<String> getCommands() {
        return commands;
    }

    public void setCommands(List<String> commands) {
        this.commands = commands;
    }

    public CommandBlockType getType() {
        return type;
    }

    public void setType(CommandBlockType type) {
        if(type == CommandBlockType.WHITELIST || type == CommandBlockType.BLACKLIST)
            this.type = type;
        else
            this.type = CommandBlockType.BLACKLIST;
    }

    @Override
    public String toString() {
        return "CommandBlocker{" +
                "type=" + type +
                ", commands=" + commands +
                '}';
    }
}
