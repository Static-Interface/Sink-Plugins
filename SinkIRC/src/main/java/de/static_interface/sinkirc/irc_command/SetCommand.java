/*
 * Copyright (c) 2013 - 2014 http://adventuria.eu, http://static-interface.de and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.static_interface.sinkirc.irc_command;

import de.static_interface.sinkirc.IrcUtil;
import de.static_interface.sinklibrary.api.command.SinkCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class SetCommand extends SinkCommand {

    public SetCommand(Plugin plugin) {
        super(plugin);
        setUsage("Wrong usage! Usage: " + getCommandPrefix() + "set <option> <value>");
    }

    @Override
    public boolean isIrcOpOnly() {
        return true;
    }

    @Override
    protected boolean onExecute(CommandSender sender, String label, String[] args) {
        if (args.length < 2) {
            return false;
        }

        switch (args[0].toLowerCase()) {
            case "cmdprefix": {
                IrcUtil.setCommandPrefix(args[1]);
                sender.sendMessage("Commandprefix has been changed to: " + args[1]);
                break;
            }

            default: {
                sender.sendMessage("Unknown option! Valid options: cmdprefix");
                break;
            }
        }
        return true;
    }
}
