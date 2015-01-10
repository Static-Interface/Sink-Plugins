/*
 * Copyright (c) 2013 - 2015 http://static-interface.de and contributors
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

package de.static_interface.sinkantispam.command;

import de.static_interface.sinkantispam.WarnUtil;
import de.static_interface.sinkantispam.warning.Warning;
import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.api.command.SinkCommand;
import de.static_interface.sinklibrary.api.exception.UserNotFoundException;
import de.static_interface.sinklibrary.user.IngameUser;
import org.apache.commons.cli.ParseException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class DeleteWarnCommand extends SinkCommand {

    public DeleteWarnCommand(Plugin plugin) {
        super(plugin);
        getCommandOptions().setIrcOpOnly(true);
    }

    @Override
    protected boolean onExecute(CommandSender sender, String label, String[] args) throws ParseException {
        if (args.length < 2) {
            return false;
        }

        IngameUser target = SinkLibrary.getInstance().getIngameUser(args[0]);

        if (sender instanceof Player && target instanceof IngameUser) {
            if (!((Player) sender).canSee(target.getPlayer()) && !sender.hasPermission("sinklibrary.bypassvanish")) {
                throw new UserNotFoundException(args[0]);
            }
        }

        int id;
        try {
            id = Integer.parseInt(args[1]);
        } catch (Exception e) {
            return false;
        }

        List<Warning> warnings = WarnUtil.getWarnings(target);
        for (Warning warning : warnings) {
            if (warning.getId() == id) {
                warnings.remove(warning);
                warning.delete(SinkLibrary.getInstance().getUser((Object) sender));
                warnings.add(warning);
                WarnUtil.setWarnings(target, warnings);
                sender.sendMessage(ChatColor.DARK_GREEN + "Success");
                return true;
            }
        }

        sender.sendMessage(ChatColor.DARK_RED + "ID not found: " + id + "!");
        return true;
    }
}
