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

package de.static_interface.sinkcommands.commands;

import de.static_interface.sinklibrary.BukkitUtil;
import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

import static de.static_interface.sinklibrary.Constants.COMMAND_PREFIX;

public class SpectateCommands
{
    public static HashMap<Player, Player> specedPlayers = new HashMap<>();
    public static final String PREFIX = ChatColor.DARK_PURPLE + "[Spec] " + ChatColor.RESET;

    public static class SpectateCommand implements CommandExecutor
    {
        @Override
        public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
        {
            User user = SinkLibrary.getUser(sender);
            if ( user.isConsole() )
            {
                sender.sendMessage("Dieser Befehl kann nur von einem Spieler ausgeführt werden!");
                return true;
            }
            Player player = user.getPlayer();
            if ( specedPlayers.containsKey(player) )
            {
                player.sendMessage(PREFIX + ChatColor.RED + "Du befindest dich bereits im Spectate Modus! Verlasse ihn erst mit " + COMMAND_PREFIX + "unspec bevor du einen anderen Spieler beobachtest..");
                return true;
            }
            if ( args.length < 1 )
            {
                return false;
            }

            Player target = (BukkitUtil.getPlayer(args[0]));
            if ( target == null )
            {
                player.sendMessage(PREFIX + args[0] + " ist nicht online!");
                return true;
            }

            if ( target.hasPermission("sinkcommands.spectate.bypass") )
            {
                player.sendMessage(PREFIX + "Der Spectate Modus kann nicht für den gewählten Spieler aktiviert werden!");
                return true;
            }

            player.sendMessage(PREFIX + "Zum verlassen des spectate Modus, " + COMMAND_PREFIX + "unspec nutzen.");


            specedPlayers.put(player, target);

            target.setPassenger(player);
            hide(player, "sinkcommands.spectate.bypass");

            return true;
        }
    }

    public static class UnspectateCommand implements CommandExecutor
    {
        @Override
        public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
        {
            User user = SinkLibrary.getUser(sender);
            if ( user.isConsole() )
            {
                sender.sendMessage("This command can only be run by a player.");
                return true;
            }

            Player player = user.getPlayer();
            if ( !specedPlayers.containsKey(player) )
            {
                player.sendMessage(PREFIX + ChatColor.RED + "Du befindest dich nicht im Spectate Modus!");
                return true;
            }
            Player target = specedPlayers.get(player);

            target.eject();
            show(player);
            specedPlayers.remove(player);
            sender.sendMessage(PREFIX + "Du hast den Spectate Modus verlassen.");
            return true;
        }
    }

    public static class SpectatorlistCommand implements CommandExecutor
    {

        @Override
        public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
        {
            sender.sendMessage(PREFIX + "Spieler im Spectate Modus:");
            int i = 0;
            String message = "";
            for ( Player player : specedPlayers.keySet() )
            {
                Player target = specedPlayers.get(player);
                if ( message.isEmpty() )
                {
                    message = player.getDisplayName() + " beobachtet: " + target.getDisplayName();
                }
                else
                {
                    message = message + ", " + player.getDisplayName() + " beobachtet: " + target.getDisplayName();
                }
                i++;
            }
            if ( i == 0 || message.isEmpty() )
            {
                message = "Es gibt keine Spieler im Spectate Modus.";
            }
            sender.sendMessage(PREFIX + message);
            return true;
        }
    }

    /**
     * Hide a player for all players except with this permission
     *
     * @param player     Player to hide
     * @param permission Players with this permission will be able to see that player
     */
    public static void hide(Player player, String permission)
    {
        for ( Player p : Bukkit.getOnlinePlayers() )
        {
            User user = SinkLibrary.getUser(p);
            if ( user.hasPermission(permission) )
            {
                continue;
            }
            p.hidePlayer(player);
        }
    }

    /**
     * Show player again to all
     *
     * @param player Player who was hidden
     */
    public static void show(Player player)
    {
        for ( Player p : Bukkit.getOnlinePlayers() )
        {
            p.showPlayer(player);
        }
    }
}
