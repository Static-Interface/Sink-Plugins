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

package de.static_interface.sinkchat.channel;

import de.static_interface.sinkchat.SinkChat;
import de.static_interface.sinkchat.TownyHelper;
import de.static_interface.sinkchat.Util;
import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.api.stream.MessageStream;
import de.static_interface.sinklibrary.configuration.IngameUserConfiguration;
import de.static_interface.sinklibrary.user.IngameUser;
import de.static_interface.sinklibrary.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

public class Channel extends MessageStream<IngameUser> {

    private String name;
    private String callCode;
    private boolean enabled;
    private String permission;
    private boolean sendToIRC;
    private int range;
    private String format;

    public Channel(String name, String callCode, boolean enabled, String permission,
                   boolean sendToIRC, int range, @Nullable String format) {
        super(name.toLowerCase());
        this.name = name;
        this.callCode = callCode;
        this.enabled = enabled;
        this.permission = permission;
        this.sendToIRC = sendToIRC;
        this.range = range;
        this.format = format;
    }

    /**
     * Returns the callCode.
     */
    public String getCallCode() {
        return callCode;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getPermission() {
        return permission;
    }

    public boolean sendToIRC() {
        return sendToIRC;
    }

    public int getRange() {
        return range;
    }

    public String getFormat() {
        return format;
    }

    public boolean enabledForPlayer(UUID uuid) {
        String enabledPath = "Channels." + getName() + ".Enabled";
        IngameUser user = SinkLibrary.getInstance().getIngameUser(uuid);
        IngameUserConfiguration config = user.getConfiguration();
        try {
            return (boolean) config.get(enabledPath, true);
        } catch (NullPointerException ignored) {
            return true;
        }
    }

    public void setEnabledForPlayer(UUID uuid, boolean setEnabled) {
        String enabledPath = "Channels." + getName() + ".Enabled";
        IngameUser user = SinkLibrary.getInstance().getIngameUser(uuid);
        IngameUserConfiguration config = user.getConfiguration();
        config.set(enabledPath, setEnabled);
    }

    public String formatEventFormat(IngameUser user) {
        if (format == null) {
            return null;
        }
        return StringUtil.format(format, user, null, "%2$s", getCustomParams(user), false, null);
    }

    @Override
    public String formatMessage(IngameUser user, String message) {
        String format = this.format;
        format = format.replace("%1$s", user.getDisplayName());
        format = format.replace("%2$s", formatMessage(message));
        return StringUtil.format(format, user, null, formatMessage(message), getCustomParams(user), false, null);
    }

    private Map<String, Object> getCustomParams(IngameUser user) {
        HashMap<String, Object> customParams = new HashMap<>();
        if (SinkChat.getInstance().isTownyAvailable()) {
            customParams.put("NationTag", TownyHelper.getNationTag(user.getPlayer()));
            customParams.put("Town(y)?Tag", TownyHelper.getTownTag(user.getPlayer()));
            customParams.put("Town(y)?", TownyHelper.getTown(user.getPlayer()));
            customParams.put("Nation", TownyHelper.getNation(user.getPlayer()));
        }
        return customParams;
    }

    public void handleMessage(IngameUser sender, Set<Player> recipients, String message) {
        for (Player p : new HashSet<>(recipients)) {
            if ((getRange() > 0 && !Util.isInRange(sender, p, getRange()))
                || (!enabledForPlayer(p.getUniqueId()))
                || (StringUtil.isEmptyOrNull(getPermission()) && !p.hasPermission(getPermission()))) {
                recipients.remove(p);
            }
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (recipients.contains(p)) {
                continue;
            }

            IngameUser target = SinkLibrary.getInstance().getIngameUser(p);
            // Todo: make spy configurable for channels
            if (!Util.isInRange(sender, p, getRange()) && Util.canSpySender(target, sender)) {
                p.sendMessage(Util.getSpyPrefix() + message);
            }
        }
    }

    public String formatMessage(String message) {
        return message.replaceFirst("\\Q" + getCallCode() + "\\E", "");
    }

    @Override
    protected boolean onSendMessage(@Nullable IngameUser sender, String message, Object... args) {
        if (args.length > 0 && args[0] == Boolean.TRUE) {
            return true;
        }

        String formattedMessage = StringUtil.format(format, sender, message);
        Set<Player> recipients = new HashSet<>(Bukkit.getOnlinePlayers());
        handleMessage(sender, recipients, formattedMessage);
        for(Player p : recipients) {
            p.sendMessage(formattedMessage);
        }
        return true;
    }
}
