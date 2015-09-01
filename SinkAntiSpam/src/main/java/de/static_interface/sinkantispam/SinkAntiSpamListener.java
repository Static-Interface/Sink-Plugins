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
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class SinkAntiSpamListener implements Listener {

    public static final int SPAM_DELAY = 750; // Todo make configurable
    private Map<UUID, String> lastMessages = new ConcurrentHashMap<>();
    private Map<UUID, Long> lastMessagesTime = new ConcurrentHashMap<>();

    public static WarnResult checkMessage(Player player, String message) {
        WarnResult result = new WarnResult();
        result.setResultcode(WarnResult.PASS);
        IngameUser user = SinkLibrary.getInstance().getIngameUser(player);

        message = ChatColor.stripColor(message);

        Pattern pattern;
        Matcher matcher;
        if (SinkLibrary.getInstance().getSettings().SAS_IPFILTER_ENABLED.getValue()) {
            pattern = Pattern.compile("\\b\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\b");
            matcher = pattern.matcher(message);
            if (matcher.find()) {
                String ip = matcher.group(0);
                WarnUtil.performWarning(new IpWarning(user, ip, WarnUtil.getNextWarningId(user)));
                result.setResultcode(WarnResult.CENSOR);
                result.setCensoredMessage(message.replace(ip, m("SinkAntiSpam.ReplaceIP")));
                return result;
            }
        }
        if (SinkLibrary.getInstance().getSettings().SAS_WHITELISTED_DOMAINS_ENABLED.getValue()) {
            String[] words = message.split(" ");

            for (String word : words) {
                if (!DomainValidator.getInstance().isValid(word)) {
                    continue;
                }

                if (isBlackListed(word, SinkLibrary.getInstance().getSettings().SAS_WHITELISTED_DOMAINS.getValue(), false) != null) {
                    return result;
                }
                WarnUtil.performWarning(new DomainWarning(user, word, WarnUtil.getNextWarningId(user)));
                result.setResultcode(WarnResult.CENSOR);
                result.setCensoredMessage(message.replace(word, m("SinkAntiSpam.ReplaceDomain")));
            }
            return result;
        }
        if (SinkLibrary.getInstance().getSettings().SAS_BLACKLIST_ENABLED.getValue()) {
            String blacklistWord = isBlackListed(message, SinkLibrary.getInstance().getSettings().SAS_BLACKLISED_WORDS.getValue(), true);
            if (blacklistWord != null) {
                blacklistWord = blacklistWord.trim();
                String
                        warnMessage =
                        message.replace(blacklistWord, ChatColor.BLUE + "" + ChatColor.BOLD + ChatColor.UNDERLINE + blacklistWord + ChatColor.RESET);
                WarnUtil.performWarning(new BlacklistWarning(user, warnMessage, WarnUtil.getNextWarningId(user)));
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
        return result;
    }

    private static String isBlackListed(String input, List<String> blacklistedWords, boolean isRegex) {
        for (String s : blacklistedWords) {
            if (isRegex) {
                Pattern pattern;
                try {
                    pattern = Pattern.compile(s);
                } catch (PatternSyntaxException e) {
                    SinkAntiSpam.getInstance().getLogger().warning("Wrong regex: " + s);
                    continue;
                }
                Matcher matcher = pattern.matcher(input);
                if (matcher.find()) {
                    return input;
                }
            } else {
                if (input.contains(s)) {
                    return s;
                }
            }
        }
        return null;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        if(event.getPlayer().hasPermission("sinkantispam.bypass")){
            return;
        }

        WarnResult result = checkMessage(event.getPlayer(), event.getMessage());
        UUID uuid = event.getPlayer().getUniqueId();
        String msg = event.getMessage();
        String lastMsg = lastMessages.get(uuid);

        lastMessages.put(uuid, msg);

        Long lastMsgTime = lastMessagesTime.get(uuid);
        lastMessagesTime.put(uuid, System.currentTimeMillis());

        switch (result.getResultCode()) {
            case WarnResult.CANCEL: {
                event.setCancelled(true);
                return;
            }

            case WarnResult.CENSOR: {
                event.setMessage(result.getCensoredMessage());
                break;
            }
        }

        if(lastMsg != null && lastMsg.equalsIgnoreCase(msg)) {
            event.getPlayer().sendMessage(m("SinkAntiSpam.RepeatingMessage"));
            event.setCancelled(true);
            return;
        }

        if(lastMsgTime == null) {
            return;
        }

        long difference = System.currentTimeMillis() - lastMsgTime;
        if(lastMsgTime != null && difference < SPAM_DELAY) {
            event.getPlayer().sendMessage(m("SinkAntiSpam.SpamMessage", SPAM_DELAY - difference));
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        for (String command : SinkLibrary.getInstance().getSettings().SAS_EXCLUDED_COMMANDS.getValue()) {
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
