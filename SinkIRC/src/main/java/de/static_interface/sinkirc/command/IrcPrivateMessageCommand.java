/*
 * Copyright (c) 2014 http://adventuria.eu, http://static-interface.de and contributors
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.static_interface.sinkirc.command;

import de.static_interface.sinkirc.IrcUtil;
import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.configuration.LanguageConfiguration;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class IrcPrivateMessageCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(LanguageConfiguration.m("General.CommandMisused.Arguments.TooFew"));
            return true;
        }

        String target = args[0];

        if (target.startsWith("#")) {
            return false;
        }

        String message = ChatColor.GRAY + SinkLibrary.getUser(sender).getDisplayName() + ChatColor.GRAY + ": ";

        for (int x = 1; x < args.length; x++) {
            message = message + ' ' + args[x];
        }

        if (!IrcUtil.sendMessage(target, message)) {
            sender.sendMessage(LanguageConfiguration.m("General.NotOnline").replace("%s", args[0]));
        } else {
            sender.sendMessage(ChatColor.GREEN + "Nachricht gesendet!");
        }
        return true;
    }
}
