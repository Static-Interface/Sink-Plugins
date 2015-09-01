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

import java.util.List;

import javax.annotation.Nullable;

public class YamlStringListOption extends YamlOption<List<String>> {

    public YamlStringListOption(String path, List<String> defaultValue) {
        super(path, defaultValue);
    }

    public YamlStringListOption(String path, List<String> defaultValue, String comment) {
        super(path, defaultValue, comment);
    }

    public YamlStringListOption(@Nullable YamlParentOption parent, String path, List<String> defaultValue) {
        super(parent, path, defaultValue);
    }

    public YamlStringListOption(@Nullable YamlParentOption parent, String path, List<String> defaultValue, @Nullable String comment) {
        super(parent, path, defaultValue, comment);
    }

    @Override
    public List<String> getValue() {
        List<?> list = getConfig().getYamlConfiguration().getList(getPath());
        if (list == null) {
            return getDefaultValue();
        }
        return getConfig().getYamlConfiguration().getStringList(getPath());
    }
}
