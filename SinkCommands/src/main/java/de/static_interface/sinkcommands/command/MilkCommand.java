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

import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.api.command.SinkCommand;
import de.static_interface.sinklibrary.api.command.annotation.DefaultPermission;
import de.static_interface.sinklibrary.api.command.annotation.Description;
import de.static_interface.sinklibrary.api.configuration.Configuration;
import de.static_interface.sinklibrary.api.exception.NotEnoughArgumentsException;
import de.static_interface.sinklibrary.api.exception.NotEnoughPermissionsException;
import de.static_interface.sinklibrary.api.exception.UserNotFoundException;
import de.static_interface.sinklibrary.api.user.SinkUser;
import de.static_interface.sinklibrary.user.IngameUser;
import de.static_interface.sinklibrary.util.BukkitUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;

@DefaultPermission
@Description("Remove invisibility effects")
public class MilkCommand extends SinkCommand {

    public static final String PREFIX = ChatColor.BLUE + "[Milk] " + ChatColor.RESET;

    public MilkCommand(Plugin plugin, Configuration config) {
        super(plugin, config);
        getCommandOptions().setIrcOpOnly(true);
    }

    @Override
    public boolean onExecute(CommandSender sender, String label, String[] args) {
        SinkUser user = SinkLibrary.getInstance().getUser(sender);
        if (!user.hasPermission("sinkcommands.commands.milk.all")) {
            throw new NotEnoughArgumentsException();
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
            throw new UserNotFoundException(args[0]);
        }

        if (sender instanceof Player) {
            if (!((Player) sender).canSee(target) && !sender.hasPermission("sinklibrary.bypassvanish")) {
                throw new UserNotFoundException(args[0]);
            }
        }

        boolean equals = user instanceof IngameUser && ((IngameUser) user).getPlayer().equals(target);
        if (!equals && user.hasPermission("sinkcommands.commands.milk.others")) {
            throw new NotEnoughPermissionsException();
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
