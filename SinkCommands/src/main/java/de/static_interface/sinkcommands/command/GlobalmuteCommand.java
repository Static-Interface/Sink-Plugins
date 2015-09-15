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

package de.static_interface.sinkcommands.command;

import de.static_interface.sinkcommands.SinkCommands;
import de.static_interface.sinklibrary.api.command.SinkCommand;
import de.static_interface.sinklibrary.api.command.annotation.DefaultPermission;
import de.static_interface.sinklibrary.api.command.annotation.Description;
import de.static_interface.sinklibrary.util.BukkitUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

@Description("Mute all players")
@DefaultPermission
public class GlobalmuteCommand extends SinkCommand {

    public static final String PREFIX = ChatColor.DARK_RED + "[GlobalMute] " + ChatColor.RESET;

    public GlobalmuteCommand(Plugin plugin) {
        super(plugin);
        getCommandOptions().setIrcOpOnly(true);
    }

    @Override
    public boolean onExecute(CommandSender sender, String label, String[] args) {
        SinkCommands.getInstance().setGlobalmuteEnabled(!SinkCommands.getInstance().isGlobalmuteEnabled());

        if (SinkCommands.getInstance().isGlobalmuteEnabled()) {
            if (args.length > 0) {
                String reason = "";
                for (String arg : args) {
                    if (reason.isEmpty()) {
                        reason = arg;
                        continue;
                    }
                    reason = reason + ' ' + arg;
                }
                BukkitUtil.broadcastMessage(PREFIX + "Der globale Mute wurde von " + BukkitUtil.getSenderName(sender) + " aktiviert. Grund: " + reason
                                            + ". Alle Spieler sind jetzt stumm.");
            } else {
                BukkitUtil.broadcastMessage(
                        PREFIX + "Der global Mute wurde von " + BukkitUtil.getSenderName(sender) + " aktiviert. Alle Spieler sind jetzt stumm.");
            }
            SinkCommands.getInstance().setGlobalmuteEnabled(true);
            return true;
        } else {
            BukkitUtil.broadcastMessage(PREFIX + "Der global Mute wurde von " + BukkitUtil.getSenderName(sender) + " deaktiviert.");
        }
        return true;
    }
}
