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
import de.static_interface.sinklibrary.configuration.PlayerConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DutyCommands
{
    public static HashMap<String, Boolean> dutyPlayers = new HashMap<>();
    public static HashMap<String, Long> dutyTime = new HashMap<>();
    public static List<String> godmodeUsers = new ArrayList<>();

    public static final String PREFIX = ChatColor.DARK_RED + "[" + ChatColor.RESET + ChatColor.RED + "Duty" + ChatColor.RESET + ChatColor.DARK_RED + "] " + ChatColor.RESET;
    public static final String CHATPREFIX = ChatColor.DARK_GRAY + "[" + ChatColor.RESET + ChatColor.GRAY + "Duty" + ChatColor.RESET + ChatColor.DARK_GRAY + "] " + ChatColor.RESET + ChatColor.GRAY;

    public static class DutyCommand implements CommandExecutor
    {
        @Override
        public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
        {
            if ( !(sender instanceof Player) )
            {
                sender.sendMessage("This command is only available to players");
                return true;
            }

            User user = SinkLibrary.getUser(sender);

            String mode = "";
            for ( String arg : args )
            {
                mode += arg + ' ';
            }

            mode = mode.trim();

            switch ( mode )
            {
                case "on gm":
                    if ( isOnDuty(user) )
                    {
                        godmodeUsers.add(user.getName());
                        break;
                    }
                    if ( !user.hasPermission("sinkcommands.duty.gm") )
                    {
                        user.sendMessage(ChatColor.DARK_RED + "You don't have permission");
                        break;
                    }
                    BukkitUtil.broadcastMessage(CHATPREFIX + "Teammitglied " + user.getDisplayName() + ChatColor.GRAY + " ist jetzt im Dienstmodus", true);
                    activateDutyMode(user.getName(), true);
                    user.sendMessage(PREFIX + "Godmode Modus wurde aktiviert");
                    break;

                case "on":
                    if ( isOnDuty(user) )
                    {
                        user.sendMessage(PREFIX + "Der Dienstmodus ist schon aktiviert!");
                        break;
                    }
                    BukkitUtil.broadcastMessage(CHATPREFIX + "Teammitglied " + user.getDisplayName() + ChatColor.GRAY + " ist jetzt im Dienstmodus", true);
                    activateDutyMode(user.getName(), false);
                    break;

                case "off":
                    if ( !isOnDuty(user) )
                    {
                        user.sendMessage(PREFIX + "Der Dienstmodus ist schon deaktiviert!");
                        break;
                    }
                    deactivateDutyMode(user.getName());
                    BukkitUtil.broadcastMessage(CHATPREFIX + "Teammitglied " + user.getDisplayName() + ChatColor.GRAY + " hat den Dienstmodus verlassen", true);
                    break;

                case "status":
                    if ( isOnDuty(user) )
                    {
                        user.sendMessage(PREFIX + "Der Dienstmodus ist derzeit " + ChatColor.DARK_GREEN + "aktiviert");
                        break;
                    }
                    user.sendMessage(PREFIX + "Der Dienstmodus ist derzeit " + ChatColor.RED + "deaktiviert");
                    break;
                default:
                    sendHelp(user);
            }
            return true;
        }

        private void sendHelp(User user)
        {
            user.sendMessage(PREFIX + ChatColor.GRAY + "Benutzung:");
            user.sendMessage(PREFIX + ChatColor.GRAY + "/duty on: Dienstmodus aktivieren");
            if ( user.hasPermission("sinkcommands.duty.gm") )
            {
                user.sendMessage(PREFIX + ChatColor.GRAY + "/duty on gm: Dienstmodus mit Godmode aktivieren");
            }
            user.sendMessage(PREFIX + ChatColor.GRAY + "/duty off: Dienstmodus deaktivieren");
            user.sendMessage(PREFIX + ChatColor.GRAY + "/duty status: Dienstmodus einsehen");
        }

        public static void activateDutyMode(String name, boolean gm)
        {
            dutyPlayers.put(name, true);
            dutyTime.put(name, System.currentTimeMillis());
            if ( gm )
            {
                godmodeUsers.add(name);
            }
            Player player = Bukkit.getPlayerExact(name);
            player.setCustomName(PREFIX + ChatColor.DARK_RED + player.getCustomName());
        }

        public static void deactivateDutyMode(String name)
        {
            dutyPlayers.put(name, false);
            User user = SinkLibrary.getUser(name);
            updateDutyTime(user, getDutyTime(user));
            dutyTime.remove(name);
            godmodeUsers.remove(name);
            Player player = Bukkit.getPlayer(name);
            if ( player == null ) return;
            String customName = player.getCustomName();

            if ( customName == null || customName.isEmpty() ) customName = player.getName();
            player.setCustomName(customName.replace(PREFIX + ChatColor.RED, ""));
        }

        public static boolean isOnDuty(User user)
        {
            try
            {
                return dutyPlayers.get(user.getName());
            }
            catch ( NullPointerException ignored )
            {
                dutyPlayers.put(user.getName(), false);
                return false;
            }
        }

        public static long getDutyTime(User user)
        {
            try
            {
                long time = System.currentTimeMillis() - dutyTime.get(user.getName());
                return user.getPlayerConfiguration().getDutyTime() + time;
            }
            catch ( NullPointerException ignored )
            {
                return user.getPlayerConfiguration().getDutyTime();
            }
        }

        public static void updateDutyTime(User user, long time)
        {
            PlayerConfiguration pc = user.getPlayerConfiguration();
            pc.setDutyTime(time);
        }
    }

    public static class DutyListCommand implements CommandExecutor
    {
        @Override
        public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
        {
            User user = SinkLibrary.getUser(sender);
            String onDutyUsers = "";
            String offDutyUsers = "";
            for ( Player p : Bukkit.getOnlinePlayers() )
            {
                User onlineUser = SinkLibrary.getUser(p);
                if ( onlineUser.hasPermission("sinkcommands.duty") )
                {
                    if ( DutyCommand.isOnDuty(onlineUser) )
                    {
                        onDutyUsers += ", " + onlineUser.getDisplayName();
                    }
                    else
                    {
                        offDutyUsers += ", " + onlineUser.getDisplayName();
                    }
                }
            }

            onDutyUsers = onDutyUsers.replaceFirst(", ", "").trim();
            offDutyUsers = offDutyUsers.replaceFirst(", ", "").trim();

            if ( !onDutyUsers.isEmpty() )
            {
                user.sendMessage(" ");
                user.sendMessage(PREFIX + ChatColor.DARK_GREEN + "Im Dienst befindende Teammitglieder:");
                user.sendMessage(PREFIX + onDutyUsers);
                user.sendMessage(" ");
            }
            if ( !offDutyUsers.isEmpty() )
            {
                if ( !onDutyUsers.isEmpty() ) user.sendMessage(" ");
                user.sendMessage(PREFIX + ChatColor.RED + "Nicht im Dienst befindende Teammitglieder:");
                user.sendMessage(PREFIX + offDutyUsers);
                user.sendMessage(" ");
            }
            if ( onDutyUsers.isEmpty() && offDutyUsers.isEmpty() )
            {
                user.sendMessage(PREFIX + ChatColor.DARK_RED + "Zur Zeit sind keine Teammitglieder online...");
            }
            return true;
        }
    }

    public static class DutyTimeCommand implements CommandExecutor
    {
        @Override
        public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
        {
            User user = SinkLibrary.getUser(sender);
            if ( args.length < 1 && user.isConsole() )
            {
                user.sendMessage(PREFIX + "Zu wenige Argumente!");
                return false;
            }

            if ( args.length < 1 )
            {
                sender.sendMessage(PREFIX + ChatColor.DARK_GREEN + "Du warst " + DutyCommand.getDutyTime(user) / (1000 * 60) + " Minute(n) im Dienstmodus.");
                return true;
            }

            if ( !user.hasPermission("sinkcommands.dutytime.others") )
            {
                user.sendMessage(PREFIX + ChatColor.DARK_RED + "Du hast keine Berechtigung um die Duty Zeit eines anderen Users einzusehen");
            }

            User target = SinkLibrary.getUser(args[0]);
            if ( !target.joinedServer() )
            {
                user.sendMessage(PREFIX + ChatColor.DARK_RED + "Fehler: " + ChatColor.RED + "Der Teammitglied \"" + target.getName() + "\" war noch nie auf dem Server...");
                return true;
            }

            long duttyTime = DutyCommand.getDutyTime(target);

            if ( !target.isOnline() )
            {
                sender.sendMessage(PREFIX + target.getName() + ChatColor.DARK_GREEN + " war " + duttyTime / (1000 * 60) + " Minute(n) im Dienstmodus.");
                return true;
            }

            sender.sendMessage(PREFIX + target.getDisplayName() + ChatColor.DARK_GREEN + " war " + duttyTime / (1000 * 60) + " Minute(n) im Dienstmodus.");
            return true;
        }
    }
}
