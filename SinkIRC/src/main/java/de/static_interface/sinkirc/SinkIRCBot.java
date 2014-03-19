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

package de.static_interface.sinkirc;

import de.static_interface.sinklibrary.BukkitUtil;
import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.configuration.LanguageConfiguration;
import de.static_interface.sinklibrary.events.*;
import de.static_interface.sinkcommands.SinkCommands;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jibble.pircbot.Colors;
import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.User;

import java.util.ArrayList;
import java.util.List;

public class SinkIRCBot extends PircBot
{
    public static final String IRC_PREFIX = ChatColor.GRAY + "[IRC] " + ChatColor.RESET;
    private static boolean disabled = false;
    private Plugin plugin;

    public SinkIRCBot(Plugin plugin)
    {
        this.plugin = plugin;

        String botName = SinkLibrary.getSettings().getIRCBotUsername();
        setName(botName);
        setLogin(botName);
        setVersion("SinkIRC for Bukkit - visit http://dev.bukkit.org/bukkit-plugins/sink-plugins/");
        disabled = !SinkLibrary.getSettings().isIRCBotEnabled();
    }

    public static String replaceColorCodes(String input)
    {
        if ( input == null || input.isEmpty() ) return input;
        input = input.replace(ChatColor.BLACK.toString(), Colors.BLACK);
        input = input.replace(ChatColor.DARK_BLUE.toString(), Colors.DARK_BLUE);
        input = input.replace(ChatColor.DARK_GREEN.toString(), Colors.DARK_GREEN);
        input = input.replace(ChatColor.DARK_AQUA.toString(), Colors.TEAL);
        input = input.replace(ChatColor.DARK_RED.toString(), Colors.RED);
        input = input.replace(ChatColor.DARK_PURPLE.toString(), Colors.PURPLE);
        input = input.replace(ChatColor.GOLD.toString(), Colors.OLIVE);
        input = input.replace(ChatColor.GRAY.toString(), Colors.LIGHT_GRAY);
        input = input.replace(ChatColor.DARK_GRAY.toString(), Colors.DARK_GRAY);
        input = input.replace(ChatColor.BLUE.toString(), Colors.BLUE);
        input = input.replace(ChatColor.GREEN.toString(), Colors.GREEN);
        input = input.replace(ChatColor.AQUA.toString(), Colors.CYAN);
        input = input.replace(ChatColor.RED.toString(), Colors.RED);
        input = input.replace(ChatColor.LIGHT_PURPLE.toString(), Colors.PURPLE);
        input = input.replace(ChatColor.YELLOW.toString(), Colors.YELLOW);
        input = input.replace(ChatColor.WHITE.toString(), Colors.NORMAL);
        input = input.replace("§k", "");
        input = input.replace(ChatColor.BOLD.toString(), Colors.BOLD);
        input = input.replace(ChatColor.STRIKETHROUGH.toString(), "");
        input = input.replace(ChatColor.UNDERLINE.toString(), Colors.UNDERLINE);
        input = input.replace(ChatColor.ITALIC.toString(), "");
        input = input.replace(ChatColor.RESET.toString(), Colors.NORMAL);
        return input;
    }

    public void sendCleanMessage(String target, String message)
    {
        SinkLibrary.getCustomLogger().debug("sendCleanMessage(\"" + target + "\", \"" + message + "\")");
        message = replaceColorCodes(message);
        sendMessage(target, message);
    }

    @Override
    public void onJoin(String channel, String sender, String login, String hostname)
    {
        IRCJoinEvent event = new IRCJoinEvent(channel, sender, login, hostname);
        if ( disabled )
        {
            event.setCancelled(true);
        }
        Bukkit.getPluginManager().callEvent(event);
    }

    @Override
    public void onPart(String channel, String sender, String login, String hostname)
    {
        IRCPartEvent event = new IRCPartEvent(channel, sender, login, hostname);
        if ( disabled )
        {
            event.setCancelled(true);
        }
        Bukkit.getPluginManager().callEvent(event);
    }

    @Override
    public void onKick(String channel, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick, String reason)
    {
        IRCKickEvent event = new IRCKickEvent(channel, kickerNick, kickerLogin, kickerHostname, recipientNick, reason);
        if ( disabled )
        {
            event.setCancelled(true);
        }
        Bukkit.getPluginManager().callEvent(event);
    }

    @Override
    public void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason)
    {
        IRCQuitEvent event = new IRCQuitEvent(sourceNick, sourceLogin, sourceHostname, reason);
        if ( disabled )
        {
            event.setCancelled(true);
        }
        Bukkit.getPluginManager().callEvent(event);
    }

    @Override
    public void onNickChange(String oldNick, String login, String hostname, String newNick)
    {
        IRCNickChangeEvent event = new IRCNickChangeEvent(oldNick, login, hostname, newNick);
        if ( disabled )
        {
            event.setCancelled(true);
        }
        Bukkit.getPluginManager().callEvent(event);
    }

    @Override
    public void onPrivateMessage(String sender, String login, String hostname, String message)
    {
        IRCPrivateMessageEvent event = new IRCPrivateMessageEvent(sender, login, hostname, message);
        if ( disabled )
        {
            event.setCancelled(true);
        }
        Bukkit.getPluginManager().callEvent(event);
    }

    @Override
    public void onMessage(String channel, String sender, String login, String hostname, String message)
    {
        IRCReceiveMessageEvent event = new IRCReceiveMessageEvent(channel, sender, login, hostname, message);
        if ( disabled )
        {
            event.setCancelled(true);
        }
        Bukkit.getPluginManager().callEvent(event);
    }

    @Override
    public void onPing(String sourceNick, String sourceLogin, String sourceHostname, String target, String pingValue)
    {
        IRCPingEvent event = new IRCPingEvent(sourceNick, sourceLogin, sourceHostname, target, pingValue);
        if ( disabled )
        {
            event.setCancelled(true);
        }
        Bukkit.getPluginManager().callEvent(event);
    }


    public static boolean isOp(String channel, String user, SinkIRCBot sinkIrcBot)
    {
        for ( User u : sinkIrcBot.getUsers(channel) )
        {
            if ( u.isOp() && u.getNick().equals(user) )
            {
                return true;
            }
        }
        return false;
    }

    public static void executeCommand(String command, String[] args, String source, String sender, String label, SinkIRCBot sinkIrcBot)
    {
        try
        {
            boolean isOp = isOp(SinkIRC.getMainChannel(), sender, sinkIrcBot);

            String prefix = IRCListener.COMMAND_PREFIX;
            boolean privateMessageCommand = !source.startsWith("#");
            if ( privateMessageCommand ) prefix = "";

            if ( command.equals("toggle") )
            {
                if ( !isOp )
                {
                    throw new UnauthorizedAccessException();
                }
                disabled = !disabled;
                if ( disabled )
                {
                    sinkIrcBot.sendMessage(source, "Disabled " + sinkIrcBot.getName());
                }
                else
                {
                    sinkIrcBot.sendMessage(source, "Enabled " + sinkIrcBot.getName());
                }
            }

            if ( disabled )
            {
                return;
            }

            if ( command.equals("exec") ) //Execute command as console
            {
                if ( !isOp ) throw new UnauthorizedAccessException();
                String commandWithArgs = "";
                int i = 0;
                for ( String arg : args )
                {
                    if ( i == args.length - 1 )
                    {
                        break;
                    }
                    i++;
                    if ( commandWithArgs.isEmpty() )
                    {
                        commandWithArgs = arg;
                        continue;
                    }
                    commandWithArgs = commandWithArgs + ' ' + arg;
                }


                final String finalCommandWithArgs = commandWithArgs;

                Bukkit.getScheduler().runTask(sinkIrcBot.plugin, new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommandWithArgs);
                    }
                });

                sinkIrcBot.sendMessage(source, sender + ": " + "Executed command: \"" + commandWithArgs + '"');
            }

            if ( command.equals("say") ) //Speak to ingame players
            {
                if ( args.length < 2 )
                {
                    sinkIrcBot.sendCleanMessage(source, sender + ": " + "Usage: " + prefix + "say <text>");
                    return;
                }
                if ( privateMessageCommand )
                {
                    source = "Query";
                }
                String messageWithPrefix;

                if ( isOp )
                {
                    messageWithPrefix = IRC_PREFIX + ChatColor.GRAY + '[' + source + "] " + ChatColor.DARK_AQUA + sender + ChatColor.GRAY + ": " + ChatColor.WHITE + label.replaceFirst("say", "");
                }
                else
                {
                    messageWithPrefix = IRC_PREFIX + ChatColor.GRAY + '[' + source + "] " + ChatColor.DARK_AQUA + sender + ChatColor.GRAY + ": " + ChatColor.WHITE + ChatColor.stripColor(label.replaceFirst("say", ""));
                }

                BukkitUtil.broadcastMessage(messageWithPrefix);
                //sendCleanMessage(SinkIRC.getMainChannel(), replaceColorCodes(messageWithPrefix));
            }

            if ( command.equals("kick") )  //Kick players from IRC
            {
                if ( !isOp )
                {
                    throw new UnauthorizedAccessException();
                }
                String targetPlayerName;
                try
                {
                    targetPlayerName = args[0];
                }
                catch ( Exception ignored )
                {
                    sinkIrcBot.sendCleanMessage(source, sender + ": " + "Usage: " + prefix + "kick <player> <reason>");
                    return;
                }
                final Player targetPlayer = BukkitUtil.getPlayer(targetPlayerName);
                if ( targetPlayer == null )
                {
                    sinkIrcBot.sendCleanMessage(source, "Player \"" + targetPlayerName + "\" is not online!");
                    return;
                }

                String formattedReason;
                if ( args.length > 1 )
                {
                    String reason = label.replace(targetPlayerName, "");
                    reason = reason.replace("kick ", "").trim();
                    formattedReason = " (Reason: " + reason + ')';
                }
                else
                {
                    formattedReason = ".";
                }
                formattedReason = ChatColor.translateAlternateColorCodes('&', formattedReason);
                final String finalReason = "Kicked by " + sender + " from IRC" + formattedReason;
                Bukkit.getScheduler().runTask(sinkIrcBot.plugin, new Runnable()
                {
                    @Override
                    public void run()
                    {
                        targetPlayer.kickPlayer(finalReason);
                    }
                });
                BukkitUtil.broadcastMessage(finalReason);

            }

            if ( command.equals("privmsg") )
            {
                if ( args.length < 2 )
                {
                    sinkIrcBot.sendCleanMessage(source, sender + ": " + "Usage: " + IRCListener.COMMAND_PREFIX + "privmsg <target> <msg>");
                    return;
                }
                de.static_interface.sinklibrary.User target;
                target = SinkLibrary.getUser(args[0]);
                if ( !target.isOnline() )
                {
                    sinkIrcBot.sendCleanMessage(source, LanguageConfiguration._("General.NotOnline").replace("%c", args[1]));
                    return;
                }

                String message = ChatColor.YELLOW + "[IRC] [PRIVMSG] " + ChatColor.DARK_AQUA + sender + ChatColor.YELLOW + ": " + ChatColor.WHITE;

                for ( int x = 1; x < args.length; x++ )
                {
                    message += ' ' + args[x];
                }

                target.getPlayer().sendMessage(message);
                sinkIrcBot.sendCleanMessage(source, "Message was send to \"" + target.getDisplayName() + "\"!");
                return;
            }

            if ( command.equals("list") ) //List Players
            {
                if ( Bukkit.getOnlinePlayers().length == 0 )
                {
                    sinkIrcBot.sendCleanMessage(source, "There are currently no online players");
                    return;
                }
                
                String onlineMessage = "Online Players (" + Bukkit.getOnlinePlayers().length + '/' + Bukkit.getMaxPlayers() + "): ";

                boolean firstPlayer = true;
                for ( Player player : Bukkit.getOnlinePlayers() )
                {
                    de.static_interface.sinklibrary.User user = SinkLibrary.getUser(player);
                    if ( firstPlayer )
                    {
                        onlineMessage += user.getDisplayName();
                        firstPlayer = false;
                    }
                    else
                    {
                        onlineMessage += ChatColor.RESET + ", " + user.getDisplayName();
                    }
                    
                    if ( onlineMessage.length() > 200 )
                    {
                        sinkIrcBot.sendCleanMessage(source, onlineMessage);
                        onlineMessage = "";
                    }
                }
                
                if ( onlineMessage.length() > 0 )
                {
                    sinkIrcBot.sendCleanMessage(source, onlineMessage);
                }
            }
            
            if ( command.equals("lag") ) // shows TPS
            {
                String lagPrefix = ChatColor.DARK_PURPLE + "[Lag] " + ChatColor.RESET;
                
                double realTPS = SinkCommands.getCommandsTimer().getAverageTPS();
                DecimalFormat decimalFormat = new DecimalFormat("##.0");
                String shownTPS = decimalFormat.format(realTPS);
                
                if ( realTPS >= 18.5 )
                {
                    shownTPS = ChatColor.GREEN + shownTPS;
                }
                else if ( realTPS >= 17 )
                {
                    shownTPS = ChatColor.YELLOW + shownTPS;
                }
                else
                {
                    shownTPS = ChatColor.RED + shownTPS;
                }
                
                sinkIrcBot.sendCleanMessage(source, lagPrefix + "TPS: " + shownTPS);
            }

            if ( command.equals("debug") )
            {
                if ( !isOp ) throw new UnauthorizedAccessException();
                sinkIrcBot.sendCleanMessage(source, Colors.BLUE + "Debug Output: ");
                String values = "";
                for ( String user : SinkLibrary.getUsers().keySet() )
                {
                    String tmp = '<' + user + ',' + ChatColor.stripColor(SinkLibrary.getUsers().get(user).getDisplayName()) + '>';
                    if ( values.isEmpty() )
                    {
                        values = tmp;
                        continue;
                    }
                    values = values + ", " + tmp;
                }
                sinkIrcBot.sendCleanMessage(source, "HashMap Values: " + values);
            }
        }
        catch ( UnauthorizedAccessException ignored )
        {
            sinkIrcBot.sendMessage(source, sender + ": " + "You may not use that command");
        }
        catch ( Exception e )
        {
            sinkIrcBot.sendMessage(source, sender + ": " + "Unexpected exception occurred while trying to execute command \"" + command + "\": " + e.getMessage());
        }
    }

    public static boolean isDisabled()
    {
        return disabled;
    }

    /**
     * Get online Player by name.
     *
     * @param name Name of the
     * @return If more than one matches, it will return the player with exact name.
     */
    public User getUser(String name)
    {
        List<User> matchedPlayers = new ArrayList<>();
        User exactUser = null;
        for ( User user : getUsers(SinkIRC.getMainChannel()) )
        {
            if ( user.getNick().toLowerCase().contains(name.toLowerCase()) ) matchedPlayers.add(user);
            if ( user.getNick().equalsIgnoreCase(name) ) exactUser = user;
        }
        if ( matchedPlayers.toArray().length > 1 && exactUser != null )
        {
            return exactUser;
        }
        else
        {
            try
            {
                return matchedPlayers.get(0);
            }
            catch ( Exception ignored )
            {
                return null;
            }
        }
    }
}
