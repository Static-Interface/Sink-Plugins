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

package de.static_interface.sinkcommands.command;

import static de.static_interface.sinklibrary.configuration.LanguageConfiguration.m;

import de.static_interface.sinklibrary.util.BukkitUtil;
import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.SinkUser;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class MilkCommand implements CommandExecutor {

    public static final String PREFIX = ChatColor.BLUE + "[Milk] " + ChatColor.RESET;
    //Todo: convert to command

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        SinkUser user = SinkLibrary.getInstance().getUser(sender);
        if (!user.hasPermission("sinkcommands.milk.all")) {
            sender.sendMessage(m("Permissions.General"));
            return true;
        }
        if (args.length == 0) //Remove all
        {
            String s = "";
            int i = 0;
            for (Player p : BukkitUtil.getOnlinePlayers()) {
                if (p.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                    i++;
                    p.removePotionEffect(PotionEffectType.INVISIBILITY);
                    if (s.isEmpty()) {
                        s = p.getDisplayName();
                    } else {
                        s = s + ", " + p.getDisplayName();
                    }
                }
            }
            if (i > 0) {
                BukkitUtil.broadcast(PREFIX + "Der Unsichtbarkeits Trank von den folgenden Spielern wurde durch " + BukkitUtil.getSenderName(sender)
                                     + " entfernt:", "sinkcommands.milk.message", false);
                BukkitUtil.broadcast(PREFIX + s, "sinkcommands.milk.message", false);
                return true;
            }
            sender.sendMessage(PREFIX + ChatColor.RED + "Es gibt zur Zeit keine Spieler die einen Unsichtbarkeits Trank haben...");
            return true;
        }
        //Remove from specified player
        Player target = BukkitUtil.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(PREFIX + args[0] + " ist nicht online!");
            return true;
        }

        if (!user.isConsole()) {
            if (!user.getPlayer().equals(target) && user.hasPermission("sinkcommands.milk.others")) {
                sender.sendMessage(m("Permissions.General"));
                return true;
            }
        }
        if (target.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
            BukkitUtil.broadcast(
                    PREFIX + "Der Unsichtbarkeits Trank von " + target.getDisplayName() + " wurde durch " + BukkitUtil.getSenderName(sender)
                    + " entfernt.", "sinkcommands.milk.message", false);
            target.removePotionEffect(PotionEffectType.INVISIBILITY);
            return true;
        }
        sender.sendMessage(PREFIX + ChatColor.RED + "Spieler \"" + target.getDisplayName() + "\" hat keinen Unsichtbarkeits Trank Effekt!");
        return true;
    }
}
