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
import de.static_interface.sinklibrary.user.IngameUser;
import de.static_interface.sinklibrary.api.command.SinkCommand;
import de.static_interface.sinklibrary.util.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class RenameCommand extends SinkCommand {

    public static final String PREFIX = ChatColor.AQUA + "[Rename] " + ChatColor.RESET;

    public RenameCommand(Plugin plugin) {
        super(plugin);
        getCommandOptions().setPlayerOnly(true);
    }

    @Override
    public boolean onExecute(CommandSender sender, String label, String[] args) {
        IngameUser user = (IngameUser) SinkLibrary.getInstance().getUser(sender);

        Player p = user.getPlayer();

        if (args.length < 1) {
            return false;
        }
        if (p.getItemInHand().getType() == Material.AIR) {
            sender.sendMessage(PREFIX + "Nimm ein Item in die Hand bevor du diesen Befehl ausführst.");
            return true;
        }
        String text = StringUtil.formatArrayToString(args, " ");

        text = ChatColor.translateAlternateColorCodes('&', text);
        ItemStack item = p.getItemInHand();
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(text);
        sender.sendMessage(PREFIX + ChatColor.GRAY + "Name von Item wurde zu: " + ChatColor.GREEN + text + ChatColor.GRAY + " umbenannt.");
        item.setItemMeta(meta);
        return true;
    }
}