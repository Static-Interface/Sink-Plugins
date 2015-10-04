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

package de.static_interface.sinkirc.stream;

import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.api.stream.MessageStream;
import de.static_interface.sinklibrary.api.user.SinkUser;
import de.static_interface.sinklibrary.util.StringUtil;

import javax.annotation.Nullable;

public class IrcMessageStream extends MessageStream<SinkUser> {
    private String target;
    public IrcMessageStream(String target) {
        super("irc_" + target);
        this.target = target;
    }

    @Override
    protected boolean onSendMessage(@Nullable SinkUser sender, String message) {
        message = StringUtil.format(message, sender, null);
        return SinkLibrary.getInstance().sendIrcMessage(message, target);
    }
}
