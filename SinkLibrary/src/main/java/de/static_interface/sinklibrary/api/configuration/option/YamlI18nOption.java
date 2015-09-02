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
import de.static_interface.sinklibrary.user.IngameUser;
import de.static_interface.sinklibrary.util.StringUtil;
import org.bukkit.entity.Player;

import java.util.HashMap;

import javax.annotation.Nullable;

public class YamlI18nOption extends YamlStringOption {

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

    public String format(Player player, Object... bindings) {
        return StringUtil.format(getValue(), SinkLibrary.getInstance().getIngameUser(player), bindings);
    }

    public String format(Player player, Player target, Object... bindings) {
        return StringUtil.format(getValue(), SinkLibrary.getInstance().getIngameUser(player),
                                 SinkLibrary.getInstance().getIngameUser(target), null, null, bindings);
    }

    public String format(IngameUser user, Object... bindings) {
        return StringUtil.format(getValue(), user, bindings);
    }

    public String format(IngameUser user, IngameUser target, Object... bindings) {
        return StringUtil.format(getValue(), user, target, null, null, bindings);
    }

    public String format(IngameUser user, IngameUser target, String userMessage, Object... bindings) {
        return StringUtil.format(getValue(), user, target, userMessage, null, bindings);
    }

    public String format(IngameUser user, IngameUser target, String userMessage, HashMap<String, Object> customPlaceHolders, Object... bindings) {
        return StringUtil.format(getValue(), user, target, userMessage, customPlaceHolders, bindings);
    }

    public String format(IngameUser user, String userMessage, Object... bindings) {
        return StringUtil.format(getValue(), user, userMessage, bindings);
    }

    public String format(Object... bindings) {
        return StringUtil.format(getValue(), bindings);
    }
}
