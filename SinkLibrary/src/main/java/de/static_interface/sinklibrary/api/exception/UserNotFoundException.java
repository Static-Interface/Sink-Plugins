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

package de.static_interface.sinklibrary.api.exception;

import de.static_interface.sinklibrary.configuration.LanguageConfiguration;

@SuppressWarnings("serial")
public class UserNotFoundException extends RuntimeException {

    private String name;

    @Deprecated
    public UserNotFoundException() {
    }

    public UserNotFoundException(String name) {
        this.name = name;
    }

    @Override
    public String getMessage() {
        return LanguageConfiguration.GENERAL_USER_NOT_FOUND.format(name);
    }
}
