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

package de.static_interface.sinklibrary.listener;

import static de.static_interface.sinklibrary.configuration.LanguageConfiguration.m;

import de.static_interface.sinklibrary.api.command.SinkCommand;
import de.static_interface.sinklibrary.api.event.IrcCommandEvent;
import de.static_interface.sinklibrary.api.exception.UnauthorizedAccessException;
import de.static_interface.sinklibrary.api.sender.IrcCommandSender;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class IrcCommandListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCommand(IrcCommandEvent event) {
        IrcCommandSender sender = event.getCommandSender();
        SinkCommand command = event.getCommand();
        String label = event.getLabel();
        String[] args = event.getArgs();
        String rawCmd = label.split(" ")[0];
        if (command == null || command.getCommandOptions().isPlayerOnly()) {
            sender.sendMessage("Unknown command: " + rawCmd);
            return;
        }

        try {
            command.onCommand(sender, Bukkit.getPluginCommand(rawCmd), label, args);
        } catch (UnauthorizedAccessException exception) {
            sender.sendMessage(m("Permissions.General"));
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "An error occurred. Please check the console");
            e.printStackTrace();
        }
    }
}
