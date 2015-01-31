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

package de.static_interface.sinkantispam.command;

import static de.static_interface.sinklibrary.configuration.LanguageConfiguration.m;

import de.static_interface.sinkantispam.WarnUtil;
import de.static_interface.sinkantispam.warning.Warning;
import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.api.command.SinkCommand;
import de.static_interface.sinklibrary.api.sender.IrcCommandSender;
import de.static_interface.sinklibrary.api.user.Identifiable;
import de.static_interface.sinklibrary.api.user.SinkUser;
import de.static_interface.sinklibrary.user.IngameUser;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class WarnCommand extends SinkCommand {

    public static final String PREFIX = ChatColor.RED + "[Warn] " + ChatColor.RESET;

    public WarnCommand(Plugin plugin) {
        super(plugin);
        getCommandOptions().setIrcOpOnly(true);
    }

    @Override
    public boolean onExecute(CommandSender sender, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(PREFIX + m("General.CommandMisused.Arguments.TooFew"));
            sender.sendMessage(PREFIX + ChatColor.RED + "Usage: " + getCommandPrefix() + "warn [Player] [Reason]");
            return false;
        }

        IngameUser target = SinkLibrary.getInstance().getIngameUser(args[0]);

        if (target.getName().equals(sender.getName())) {
            sender.sendMessage(PREFIX + m("SinkAntiSpam.WarnSelf"));
            return true;
        }

        String reason = "";

        for (int i = 1; i < args.length; i++) {
            if (reason.isEmpty()) {
                reason = args[i];
                continue;
            }
            reason = reason + ' ' + args[i];
        }

        SinkUser user = SinkLibrary.getInstance().getUser(sender);
        UUID uuid = null;
        if (user instanceof Identifiable) {
            uuid = ((Identifiable) user).getUniqueId();
        }
        String name = (sender instanceof IrcCommandSender) ? user.getDisplayName() + " (IRC)" : user.getDisplayName();

        WarnUtil.warn(target, new Warning(reason, name, uuid, WarnUtil.getWarningId(target), false));
        return true;
    }
}
