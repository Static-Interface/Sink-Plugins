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

package de.static_interface.sinkchat.channel;

import static de.static_interface.sinklibrary.configuration.LanguageConfiguration.m;

import de.static_interface.sinkchat.SinkChat;
import de.static_interface.sinkchat.TownyHelper;
import de.static_interface.sinkchat.Util;
import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.user.IngameUser;
import de.static_interface.sinklibrary.configuration.IngameUserConfiguration;
import de.static_interface.sinklibrary.util.BukkitUtil;
import de.static_interface.sinklibrary.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

import javax.annotation.Nullable;

public class Channel {

    private String name;
    private String callCode;
    private boolean enabled;
    private String permission;
    private boolean sendToIRC;
    private int range;
    private String format;

    public Channel(String name, String callCode, boolean enabled, String permission,
                   boolean sendToIRC, int range, @Nullable String format) {
        this.name = name;
        this.callCode = callCode;
        this.enabled = enabled;
        this.permission = permission;
        this.sendToIRC = sendToIRC;
        this.range = range;
        this.format = format;
    }

    public String getName() {
        return name;
    }

    /**
     * Returns the callChar
     * @deprecated use {@link #getCallCode()} instead.  
     */
    @Deprecated
    public String getCallChar() {
        return getCallCode();
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

    public boolean sendMessage(IngameUser user, String message) {
        if (!isEnabled()) {
            user.sendMessage(m("SinkChat.DisabledChannel"));
            return true;
        }

        if (!enabledForPlayer(user.getUniqueId())) {
            return false;
        }

        HashMap<String, Object> customParams = new HashMap<>();
        if (SinkChat.getInstance().isTownyAvailable()) {
            customParams.put("NationTag", TownyHelper.getNationTag(user.getPlayer()));
            customParams.put("Town(y)?Tag", TownyHelper.getTownTag(user.getPlayer()));
            customParams.put("Town(y)?", TownyHelper.getTown(user.getPlayer()));
            customParams.put("Nation", TownyHelper.getNation(user.getPlayer()));
        }

        String formattedMessage = StringUtil.format(getFormat(), user,
                                                    message.substring(callCode.length()), customParams, null);
        if (getRange() > 0) {
            Util.sendMessage(user, formattedMessage, getRange());
        } else {
            for (Player target : BukkitUtil.getOnlinePlayers()) {
                if ((!enabledForPlayer(target.getUniqueId())) || !target.hasPermission(getPermission())) {
                    continue;
                }
                target.sendMessage(formattedMessage);
            }
        }

        Bukkit.getConsoleSender().sendMessage(formattedMessage);
        if (sendToIRC()) {
            SinkLibrary.getInstance().sendIrcMessage(formattedMessage);
        }
        return true;
    }
}
