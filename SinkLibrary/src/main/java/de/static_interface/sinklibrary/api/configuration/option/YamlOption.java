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

import de.static_interface.sinklibrary.api.configuration.Configuration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class YamlOption<T> extends Option<T> {

    protected final YamlOption parent;
    private final String path;
    private final String comment;
    private Configuration config;

    public YamlOption(String path, T defaultValue) {
        this(null, path, defaultValue, null);
    }

    public YamlOption(String path, T defaultValue, String comment) {
        this(null, path, defaultValue, comment);
    }


    public YamlOption(@Nullable YamlOption parent, String path, T defaultValue) {
        this(parent, path, defaultValue, null);
    }

    public YamlOption(@Nullable YamlOption parent, String path, T defaultValue, @Nullable String comment) {
        super(path, defaultValue);
        this.parent = parent;
        this.comment = comment;
        if (parent != null) {
            this.path = parent.getPath() + "." + path;
        } else {
            this.path = path;
        }
    }

    public Configuration getConfig() {
        return config;
    }

    public void setConfig(Configuration config) {
        this.config = config;
    }

    @Nonnull
    public String getPath() {
        return path;
    }

    @Override
    public T getValue() {
        return (T) config.get(getPath(), getDefaultValue());
    }

    @Override
    public boolean setValue(T value) {
        return setValue(value, false);
    }

    public boolean setValue(T value, boolean save) {
        config.set(getPath(), value);
        if (save) {
            config.save();
        }
        return true;
    }

    @Nullable
    public String getComment() {
        return comment;
    }
}
