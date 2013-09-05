package de.static_interface.commandsplugin.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class CommandsverCommand implements CommandExecutor
{
    public static final String PREFIX = ChatColor.BLUE + "[CommandsPlugin] " + ChatColor.RESET;

    Plugin plugin;

    public CommandsverCommand(Plugin plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        List<String> authorsList = plugin.getDescription().getAuthors();
        String authors = "";
        int i = 0;
        for (String s : authorsList)
        {
            i++;
            if (authors.equals(""))
            {
                authors = s;
                continue;
            }
            if (i == authorsList.toArray().length)
            {
                authors = authors + " and " + s;
                continue;
            }
            authors = authors + ", " + s;
        }
        sender.sendMessage(PREFIX + plugin.getDescription().getName() + " by " + authors);
        sender.sendMessage(PREFIX + "Version: " + plugin.getDescription().getVersion());
        sender.sendMessage(PREFIX + "Copyright © 2013 Adventuria");
        return true;
    }
}
