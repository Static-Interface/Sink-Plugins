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
import java.util.Map;

import javax.annotation.Nullable;

public class YamlMapListOption extends YamlOption<List<Map<?, ?>>> {

    public YamlMapListOption(String path,
                             List<Map<?, ?>> defaultValue) {
        super(path, defaultValue);
    }

    public YamlMapListOption(String path, List<Map<?, ?>> defaultValue, String comment) {
        super(path, defaultValue, comment);
    }

    public YamlMapListOption(@Nullable YamlOption parent, String path, List<Map<?, ?>> defaultValue) {
        super(parent, path, defaultValue);
    }

    public YamlMapListOption(@Nullable YamlOption parent, String path, List<Map<?, ?>> defaultValue, @Nullable String comment) {
        super(parent, path, defaultValue, comment);
    }


    @Override
    public List<Map<?, ?>> getValue() {
        if (getConfig().getYamlConfiguration().getList(getPath()) == null) {
            return getDefaultValue();
        }
        return getConfig().getYamlConfiguration().getMapList(getPath());
    }
}
