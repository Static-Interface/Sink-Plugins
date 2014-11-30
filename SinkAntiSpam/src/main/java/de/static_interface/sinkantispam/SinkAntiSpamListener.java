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

package de.static_interface.sinkantispam;


import static de.static_interface.sinklibrary.Constants.COMMAND_PREFIX;
import static de.static_interface.sinklibrary.configuration.LanguageConfiguration.m;

import de.static_interface.sinkantispam.warning.BlacklistWarning;
import de.static_interface.sinkantispam.warning.DomainWarning;
import de.static_interface.sinkantispam.warning.IpWarning;
import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.user.IngameUser;
import de.static_interface.sinklibrary.util.DomainValidator;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SinkAntiSpamListener implements Listener {

    private static List<String> blacklistedWords;
    private static List<String> whiteListDomains;
    private static List<String> excludedCommands;

    public SinkAntiSpamListener() {
        blacklistedWords = SinkLibrary.getInstance().getSettings().getBlackListedWords();
        whiteListDomains = SinkLibrary.getInstance().getSettings().getWhitelistedWords();
        excludedCommands = SinkLibrary.getInstance().getSettings().getExcludedCommands();
    }

    public static WarnResult checkMessage(Player player, String message) {
        WarnResult result = new WarnResult();
        result.setResultcode(WarnResult.PASS);
        IngameUser user = SinkLibrary.getInstance().getIngameUser(player);

        if (user.hasPermission("sinkantispam.bypass")) {
            return result;
        }

        message = ChatColor.stripColor(message);

        if (SinkLibrary.getInstance().getSettings().isBlacklistedWordsEnabled()) {
            String blacklistWord = containsWord(message, blacklistedWords);
            if (blacklistWord != null) {
                blacklistWord = blacklistWord.trim();
                String
                        warnMessage =
                        message.replace(blacklistWord, ChatColor.BLUE + "" + ChatColor.BOLD + ChatColor.UNDERLINE + blacklistWord + ChatColor.RESET);
                WarnUtil.warn(user, new BlacklistWarning(warnMessage, WarnUtil.getWarningId(user)));
                result.setResultcode(WarnResult.CANCEL);
                String tmp = "";
                for (int i = 0; i < blacklistWord.length(); i++) {
                    tmp += "*";
                }
                result.setResultcode(WarnResult.CENSOR);
                result.setCensoredMessage(message.replace(blacklistWord, tmp));
                return result;
            }
        }

        Pattern pattern;
        Matcher matcher;
        if (SinkLibrary.getInstance().getSettings().isIPCheckEnabled()) {
            pattern = Pattern.compile("\\b\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\b");
            matcher = pattern.matcher(message);
            if (matcher.find()) {
                String ip = matcher.group(0);
                WarnUtil.warn(user, new IpWarning(ip, WarnUtil.getWarningId(user)));
                result.setResultcode(WarnResult.CENSOR);
                result.setCensoredMessage(message.replace(ip, m("SinkAntiSpam.ReplaceIP")));
                return result;
            }
        }
        if (SinkLibrary.getInstance().getSettings().isWhitelistedDomainCheckEnabled()) {
            String[] words = message.split(" ");

            for (String word : words) {
                if (!DomainValidator.getInstance().isValid(word)) {
                    continue;
                }

                if (containsWord(word, whiteListDomains) != null) {
                    return result;
                }
                WarnUtil.warn(user, new DomainWarning(word, WarnUtil.getWarningId(user)));
                result.setResultcode(WarnResult.CENSOR);
                result.setCensoredMessage(message.replace(word, m("SinkAntiSpam.ReplaceDomain")));
            }
            return result;
        }
        return result;
    }

    @SuppressWarnings("NonBooleanMethodNameMayNotStartWithQuestion")
    private static String containsWord(String input, List<String> blacklistedWords) {
        for (String blacklistedWord : blacklistedWords) {
            for (String word : input.split(" !?.")) {
                if (word.equals(blacklistedWord)) {
                    return word.trim();
                }
            }
        }
        return null;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        WarnResult result = checkMessage(event.getPlayer(), event.getMessage());

        switch (result.getResultCode()) {
            case WarnResult.CANCEL: {
                event.setCancelled(true);
            }

            case WarnResult.CENSOR: {
                event.setMessage(result.getCensoredMessage());
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        for (String command : excludedCommands) {
            if (event.getMessage().startsWith(COMMAND_PREFIX + command)
                || event.getMessage().startsWith(command)) {
                return;
            }
        }

        WarnResult result = checkMessage(event.getPlayer(), event.getMessage());

        switch (result.getResultCode()) {
            case WarnResult.CANCEL: {
                event.setCancelled(true);
            }

            case WarnResult.CENSOR: {
                event.setMessage(result.getCensoredMessage());
            }
        }
    }
}
