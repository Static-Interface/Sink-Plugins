/*
 * Copyright (c) 2013 - 2015 http://static-interface.de and contributors
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

package de.static_interface.sinklibrary.api.configuration.option;

import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.api.user.SinkUser;
import de.static_interface.sinklibrary.configuration.GeneralLanguage;
import de.static_interface.sinklibrary.util.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

import javax.annotation.Nullable;

public class YamlI18nOption extends YamlStringOption {

    private boolean isError;
    public YamlI18nOption(String path, String defaultValue) {
        super(path, defaultValue);
    }

    public YamlI18nOption(String path, String defaultValue, String comment) {
        super(path, defaultValue, comment);
    }

    public YamlI18nOption(@Nullable YamlParentOption parent, String path, String defaultValue) {
        super(parent, path, defaultValue);
    }

    public YamlI18nOption(@Nullable YamlParentOption parent, String path, String defaultValue, @Nullable String comment) {
        super(parent, path, defaultValue, comment);
    }

    public YamlI18nOption(YamlParentOption parent, String path, String defaultValue, boolean isError) {
        super(parent, path, defaultValue);
        this.isError = isError;
    }

    public YamlI18nOption(String path, String defaultValue, boolean isError) {
        super(path, defaultValue);
        this.isError = isError;
    }

    public String format(CommandSender sender, Object... bindings) {
        return StringUtil.format(getValue(), getUser(sender), bindings);
    }

    public String format(CommandSender sender, CommandSender target, Object... bindings) {
        return StringUtil.format(getValue(), getUser(sender), getUser(target), null, null, bindings);
    }

    public String format(CommandSender sender, CommandSender target, String userMessage, Object... bindings) {
        return StringUtil.format(getValue(), getUser(sender), getUser(target), userMessage, null, bindings);
    }

    public String format(CommandSender sender, CommandSender target, String userMessage, HashMap<String, Object> customPlaceHolders,
                         Object... bindings) {
        return StringUtil.format(getValue(), getUser(sender), getUser(target), userMessage, customPlaceHolders, bindings);
    }

    public String format(CommandSender sender, String userMessage, Object... bindings) {
        return StringUtil.format(getValue(), getUser(sender), userMessage, bindings);
    }

    public String format(SinkUser user, Object... bindings) {
        return StringUtil.format(getValue(), user, bindings);
    }

    public String format(SinkUser user, SinkUser target, Object... bindings) {
        return StringUtil.format(getValue(), user, target, null, null, bindings);
    }

    public String format(SinkUser user, SinkUser target, String userMessage, Object... bindings) {
        return StringUtil.format(getValue(), user, target, userMessage, null, bindings);
    }

    public String format(SinkUser user, SinkUser target, String userMessage, HashMap<String, Object> customPlaceHolders, Object... bindings) {
        return StringUtil.format(getValue(), user, target, userMessage, customPlaceHolders, bindings);
    }

    public String format(SinkUser user, String userMessage, Object... bindings) {
        return StringUtil.format(getValue(), user, userMessage, bindings);
    }

    public String format(Object... bindings) {
        return StringUtil.format(getValue(), bindings);
    }

    private SinkUser getUser(CommandSender sender) {
        return SinkLibrary.getInstance().getUser((Object) sender);
    }

    @Override
    public String getValue() {
        String s = ChatColor.translateAlternateColorCodes('&', super.getValue());
        if (isErrorMessage()) {
            s = GeneralLanguage.formatError(s);
        }
        return s;
    }

    public boolean isErrorMessage() {
        return isError;
    }
}