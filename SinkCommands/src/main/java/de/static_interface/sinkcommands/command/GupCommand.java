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

package de.static_interface.sinkcommands.command;

import de.static_interface.sinklibrary.api.command.SinkCommand;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class GupCommand extends SinkCommand {

    public GupCommand(Plugin plugin) {
        super(plugin);
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    protected boolean onExecute(CommandSender sender, String label, String[] args) {
        Player player = (Player) sender;
        World world = player.getWorld();
        Location loc = player.getLocation();
        int maxHeight = world.getMaxHeight() - loc.getBlockY() - 1;
        int height;
        if (args.length > 0) {
            try {
                height = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.DARK_RED + "Fehler: " + ChatColor.RED + "\"" + args[0] + "\" ist keine gueltige Zahl!");
                return true;
            }
        } else {
            height = maxHeight;
        }
        if (height > maxHeight) {
            sender.sendMessage(ChatColor.DARK_RED + "Fehler: " + ChatColor.RED + "Maximale Höhe erreicht!");
            height = maxHeight;
        }
        int x = loc.getBlockX();
        int y = loc.getBlockY() + height;
        int z = loc.getBlockZ();

        if (world.getBlockAt(x, y, z).getType() != Material.AIR) {
            sender.sendMessage(ChatColor.DARK_RED + "Fehler: " + ChatColor.RED + "Ein Block steht im Weg!");
            return true;
        }

        world.getBlockAt(x, y, z).setType(Material.GLASS);
        world.getBlockAt(x + 1, y, z).setType(Material.GLASS);
        world.getBlockAt(x - 1, y, z).setType(Material.GLASS);
        world.getBlockAt(x, y, z + 1).setType(Material.GLASS);
        world.getBlockAt(x, y, z - 1).setType(Material.GLASS);
        world.getBlockAt(x + 1, y, z + 1).setType(Material.GLASS);
        world.getBlockAt(x - 1, y, z - 1).setType(Material.GLASS);
        world.getBlockAt(x + 1, y, z - 1).setType(Material.GLASS);
        world.getBlockAt(x - 1, y, z + 1).setType(Material.GLASS);

        player.teleport(new Location(world, x, y + 1, z, loc.getYaw(), loc.getPitch()));
        return true;
    }
}
