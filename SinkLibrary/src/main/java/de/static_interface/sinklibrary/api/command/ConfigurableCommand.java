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

package de.static_interface.sinklibrary.api.command;

import de.static_interface.sinklibrary.api.configuration.Configuration;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ConfigurableCommand {

    @Nullable
    Configuration getConfig();

    @Nullable
    String getCommandPermission();

    void setCommandPermission(String permission);

    @Nonnull
    String getConfigPath();

    @Nullable
    List<String> getCommandAliases();

    void setCommandAliases(List<String> aliases);

    @Nullable
    String getCommandUsage();

    void setCommandUsage(String usage);

    @Nullable
    String getCommandDescription();

    void setCommandDescription(String description);

    @Nullable
    String getDefaultPermission();
}
