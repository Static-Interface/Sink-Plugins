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

package de.static_interface.sinkantispam;


import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.User;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.static_interface.sinklibrary.Constants.COMMAND_PREFIX;
import static de.static_interface.sinklibrary.configuration.LanguageConfiguration._;

public class SinkAntiSpamListener implements Listener
{
    private static List<String> blacklistedWords;
    private static List<String> whiteListDomains;
    private static List<String> excludedCommands;

    public SinkAntiSpamListener()
    {
        blacklistedWords = SinkLibrary.getSettings().getBlackListedWords();
        whiteListDomains = SinkLibrary.getSettings().getWhitelistedWords();
        excludedCommands = SinkLibrary.getSettings().getExcludedCommands();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event)
    {
        validateMessageEvent(event);
    }

    @SuppressWarnings("HardcodedFileSeparator")
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
    {
        for ( String command : excludedCommands )
        {
            if ( event.getMessage().startsWith(COMMAND_PREFIX + command) || event.getMessage().startsWith(command) )
            {
                return;
            }
        }
        validateMessageEvent(event);
    }

    public static void validateMessageEvent(Event event)
    {
        String message;
        Player player;

        if ( event instanceof AsyncPlayerChatEvent )
        {
            message = ((AsyncPlayerChatEvent) event).getMessage();
            player = ((AsyncPlayerChatEvent) event).getPlayer();
        }
        else if ( event instanceof PlayerCommandPreprocessEvent )
        {
            message = ((PlayerCommandPreprocessEvent) event).getMessage();
            player = ((PlayerCommandPreprocessEvent) event).getPlayer();
        }
        else
        {
            throw new IllegalArgumentException("Event needs to be an instance of AsyncPlayerChatEvent or PlayerCommandPreprocessEvent!");
        }

        message = ChatColor.stripColor(message);

        User user = SinkLibrary.getUser(player);

        if ( user.hasPermission("sinkcommands.bypass") )
        {
            return;
        }

        String word = containsBlacklistedWord(message, blacklistedWords);
        if ( word != null && !word.isEmpty() && SinkLibrary.getSettings().isBlacklistedWordsEnabled() )
        {
            word = word.trim();
            message = message.replace(word, ChatColor.BLUE.toString() + ChatColor.BOLD.toString() + ChatColor.UNDERLINE.toString() + word + ChatColor.RESET.toString());
            SinkAntiSpam.warnPlayer(player, String.format(_("SinkAntiSpam.Reasons.BlacklistedWord"), message));
            if ( event instanceof AsyncPlayerChatEvent )
            {
                ((AsyncPlayerChatEvent) event).setCancelled(true);
            }
            else
            {
                ((PlayerCommandPreprocessEvent) event).setCancelled(true);
            }
            return;
        }

        Pattern pattern = Pattern.compile("\\b\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\b");
        Matcher matcher = pattern.matcher(message);
        if ( matcher.find() && SinkLibrary.getSettings().isIPCheckEnabled() )
        {
            String match = matcher.group(0);
            SinkAntiSpam.warnPlayer(player, String.format(_("SinkAntiSpam.Reasons.IP"), match));//"Fremdwerbung für folgende IP: " + match + " !");
            if ( event instanceof AsyncPlayerChatEvent )
            {
                ((AsyncPlayerChatEvent) event).setMessage(message.replace(match, ' ' + _("SinkAntiSpam.ReplaceIP") + ' '));
            }
            else
            {
                ((PlayerCommandPreprocessEvent) event).setMessage(message.replace(match, ' ' + _("SinkAntiSpam.ReplaceIP") + ' '));
            }
            return;
        }

        pattern = Pattern.compile("((w{3}\\.)?([A-Za-z0-9]+\\.)+[A-Za-z]{2,3}/?)\\s");
        matcher = pattern.matcher(message);
        if ( matcher.find() && SinkLibrary.getSettings().isWhitelistedDomainCheckEnabled() )
        {
            String match = matcher.group(0);
            if ( containsBlacklistedWord(match, whiteListDomains) != null )
            {
                return;
            }
            SinkAntiSpam.warnPlayer(player, String.format(_("SinkAntiSpam.Reasons.Domain"), match.trim()));
            if ( event instanceof AsyncPlayerChatEvent )
            {
                ((AsyncPlayerChatEvent) event).setMessage(message.replace(match.trim(), ' ' + _("SinkAntiSpam.ReplaceDomain") + ' '));
            }
            else
            {
                ((PlayerCommandPreprocessEvent) event).setMessage(message.replace(match, ' ' + _("SinkAntiSpam.ReplaceDomain") + ' '));
            }
        }
    }

    @SuppressWarnings("NonBooleanMethodNameMayNotStartWithQuestion")
    private static String containsBlacklistedWord(String input, List<String> blacklistedWords)
    {
        for ( String blacklistedWord : blacklistedWords )
        {
            for ( String word : input.split(" !?.") )
            {
                if ( word.equals(blacklistedWord) ) return word.trim();
            }
        }
        return null;
    }
}
