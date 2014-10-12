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

package de.static_interface.sinklibrary.api.command;

import de.static_interface.sinklibrary.api.annotation.Unstable;

public class SinkCommandOptions {

    private boolean useNotices;
    private boolean isPlayerOnly;
    private boolean isIrcOnly;
    private boolean isIrcOpOnly;
    private boolean isIrcQueryOnly;

    public SinkCommandOptions(boolean isPlayerOnly, boolean isIrcOnly, boolean isIrcOpOnly, boolean isIrcQueryOnly, @Unstable boolean useNotices) {
        if (isPlayerOnly && isIrcOnly) {
            throw new IllegalStateException("A command can't be player only and irc only at the same time");
        }

        this.isPlayerOnly = isPlayerOnly;
        this.isIrcOnly = isIrcOnly;
        this.isIrcOpOnly = isIrcOpOnly;
        this.isIrcQueryOnly = isIrcQueryOnly;
        this.useNotices = useNotices;
    }

    public boolean isPlayerOnly() {
        return isPlayerOnly;
    }

    public void setPlayerOnly(boolean value) {
        this.isPlayerOnly = value;
    }

    public boolean isIrcOnly() {
        return isIrcOnly;
    }

    public void setIrcOnly(boolean value) {
        this.isIrcOnly = value;
    }

    public boolean isIrcOpOnly() {
        return isIrcOpOnly;
    }

    public void setIrcOpOnly(boolean value) {
        this.isIrcOpOnly = value;
    }

    public boolean isIrcQueryOnly() {
        return isIrcQueryOnly;
    }

    public void setIrcQueryOnly(boolean value) {
        this.isIrcQueryOnly = value;
    }

    public void setUseNotices(boolean useNotices) {
        this.useNotices = useNotices;
    }

    public boolean useNotices() {
        return useNotices;
    }
}
