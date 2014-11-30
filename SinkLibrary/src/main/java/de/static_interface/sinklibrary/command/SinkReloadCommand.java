/*
 * Copyright (c) 2013 - 2014 http://static-interface.de and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.static_interface.sinklibrary.command;

import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.api.command.SinkCommand;
import de.static_interface.sinklibrary.api.configuration.Configuration;
import de.static_interface.sinklibrary.util.SinkIrcReflection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.pircbotx.User;

public class SinkReloadCommand extends SinkCommand {
    //Todo: fix exceptions on reload

    private static String pluginName = SinkLibrary.getInstance().getPluginName();
    public static final String PREFIX = ChatColor.DARK_GREEN + "[" + pluginName + "] " + ChatColor.RESET;

    public SinkReloadCommand(Plugin plugin) {
        super(plugin);
        getCommandOptions().setIrcOpOnly(true);
    }

    @Override
    public boolean onExecute(CommandSender sender, String label, String[] args) {
        sender.sendMessage(PREFIX + "Reloading Configs...");

        // Reload all configs
        for (Configuration base : Configuration.getConfigs().values()) {
            base.reload();
        }

        sender.sendMessage(PREFIX + "Reloading Users...");

        for (Player player : Bukkit.getOnlinePlayers()) {
            SinkLibrary.getInstance().unloadUser(player.getUniqueId());
            SinkLibrary.getInstance().loadUser(player);
        }

        if (SinkLibrary.getInstance().isIrcAvailable()) {
            for (User ircUser : SinkIrcReflection.getMainChannel().getUsers()) {
                SinkLibrary.getInstance().unloadIrcUser(ircUser);
                SinkLibrary.getInstance().loadIrcUser(ircUser, SinkIrcReflection.getMainChannel().getName()); //?
            }
        }
        sender.sendMessage(PREFIX + ChatColor.GREEN + "Done");
        return true;
    }
}
