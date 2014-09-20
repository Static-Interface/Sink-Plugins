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

package de.static_interface.sinklibrary.event;

import org.pircbotx.PircBotX;
import org.pircbotx.User;

public class IrcCommandEvent extends IrcEventBase
{
    private String source;
    private User user;
    private String command;
    private String label;
    private String[] args;
    private final PircBotX bot;

    public IrcCommandEvent(String source, User user, String command, String label, String[] args, PircBotX bot)
    {
        this.source = source;
        this.user = user;
        this.command = command;
        this.label = label;
        this.args = args;
        this.bot = bot;
    }

    public void setSource(String source)
    {
        this.source = source;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public void setCommand(String command)
    {
        this.command = command;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public void setArgs(String[] args)
    {
        this.args = args;
    }

    public String getSource() { return source; }

    public User getUser() { return user; }

    public String getCommand() { return command; }

    public String getLabel()
    {
        return label;
    }

    public String[] getArgs()
    {
        return args;
    }

    @Override
    public PircBotX getBot()
    {
        return bot;
    }
}
