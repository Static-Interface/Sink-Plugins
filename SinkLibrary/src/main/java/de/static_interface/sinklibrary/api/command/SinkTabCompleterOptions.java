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

package de.static_interface.sinklibrary.api.command;

public class SinkTabCompleterOptions {

    private boolean includeIngameUsers;
    private boolean includeIrcUsers;
    private boolean includeSuffix;

    public SinkTabCompleterOptions(boolean includeIngameUsers, boolean includeIrcUsers, boolean includeSuffix) {
        this.includeIngameUsers = includeIngameUsers;
        this.includeIrcUsers = includeIrcUsers;
        this.includeSuffix = includeSuffix;
    }

    public void setIncludeIngameUsers(boolean includeIngameUsers) {
        this.includeIngameUsers = includeIngameUsers;
    }

    public boolean includeIngameUsers() {
        return includeIngameUsers;
    }

    public void setIncludeIrcUsers(boolean includeIrcUsers) {
        this.includeIrcUsers = includeIrcUsers;
    }

    public boolean includeIrcUsers() {
        return includeIrcUsers;
    }

    public void setIncludeSuffix(boolean includeSuffix) {
        this.includeSuffix = includeSuffix;
    }

    public boolean includeSuffix() {
        return includeSuffix;
    }
}
