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

package de.static_interface.sinkantispam;

import de.static_interface.sinkantispam.warning.Warning;
import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.api.command.SinkCommand;
import de.static_interface.sinklibrary.user.IngameUser;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class ListWarnsCommand extends SinkCommand {

    public ListWarnsCommand(Plugin plugin) {
        super(plugin);

        getCommandOptions().setIrcOpOnly(true);
    }

    @Override
    protected boolean onExecute(CommandSender sender, String label, String[] args) {
        IngameUser targetUser;

        if (args.length < 1 && sender instanceof Player) {
            targetUser = SinkLibrary.getInstance().getIngameUser((Player) sender);
        } else if (args.length < 1) {
            return false;
        } else {
            targetUser = SinkLibrary.getInstance().getIngameUser(args[0]);
        }

        List<String> out = new ArrayList<>();
        out.add(ChatColor.RED + "Warnings: " + targetUser.getDisplayName());

        List<Warning> warnings = WarnUtil.getWarnings(targetUser);
        if (warnings.size() > 0) {
            for (Warning warning : WarnUtil.getWarnings(targetUser)) {
                out.add(ChatColor.GOLD + "#" + warning.getId() + " - " + warning.getWarnerDisplayName() + ": " + warning.getReason());
            }
        } else {
            out.add(ChatColor.RED + "No warnings found");
        }

        sender.sendMessage(out.toArray(new String[out.size()]));
        return true;
    }
}
