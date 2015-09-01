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

public class YamlParentOption extends YamlOption {

    public YamlParentOption(String path) {
        super(path, null);
    }

    public YamlParentOption(YamlParentOption parent, String path) {
        super(parent, path, null);
    }

    @Override
    public boolean setValue(Object value) {
        throw new IllegalStateException("YamlParentOptions can't have values");
    }

    @Override
    public Object getValue() {
        throw new IllegalStateException("YamlParentOptions can't have values");
    }

    @Override
    public String getComment() {
        throw new IllegalStateException("YamlParentOptions can't have comments");
    }
}
