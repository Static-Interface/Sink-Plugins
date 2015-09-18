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

package de.static_interface.sinkirc.irc_command;

import de.static_interface.sinklibrary.api.command.annotation.Description;
import de.static_interface.sinklibrary.api.command.annotation.Usage;
import de.static_interface.sinklibrary.api.configuration.Configuration;
import de.static_interface.sinklibrary.util.BukkitUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

@Description("Kick a player ingame")
@Usage("kick <player> [reason]")
public class KickCommand extends IrcCommand {

    public KickCommand(Plugin plugin, Configuration config) {
        super(plugin, config);
        getCommandOptions().setIrcOpOnly(true);
    }

    @Override
    public boolean onExecute(CommandSender sender, String label, String[] args) {
        String targetPlayerName;
        try {
            targetPlayerName = args[0];
        } catch (Exception ignored) {
            return false;
        }
        final Player targetPlayer = BukkitUtil.getPlayer(targetPlayerName);
        if (targetPlayer == null) {
            sender.sendMessage("Player \"" + targetPlayerName + "\" is not online!");
            return true;
        }

        String reason;
        if (args.length > 1) {
            reason = label.replace(targetPlayerName, "");
            reason = reason.replaceFirst("\\Qkick \\E", "").trim();
        } else {
            reason = "Kicked by " + BukkitUtil.getSenderName(sender) + " from IRC";
        }
        final String formattedReason = ChatColor.translateAlternateColorCodes('&', reason);
        Bukkit.getScheduler().runTask(plugin, new Runnable() {
            @Override
            public void run() {
                targetPlayer.kickPlayer(formattedReason);
            }
        });
        return true;
    }
}
