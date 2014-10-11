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

package de.static_interface.sinklibrary.api.event;

import de.static_interface.sinklibrary.api.command.SinkCommand;
import de.static_interface.sinklibrary.api.sender.IrcCommandSender;
import org.pircbotx.PircBotX;

public class IrcCommandEvent extends IrcEvent {

    private PircBotX bot;
    private IrcCommandSender sender;
    private SinkCommand command;
    private String label;
    private String[] args;

    public IrcCommandEvent(IrcCommandSender sender, SinkCommand cmd, String label, String[] args, PircBotX bot) {
        this.sender = sender;
        this.command = cmd;
        this.label = label;
        this.args = args;
        this.bot = bot;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    @Override
    public PircBotX getBot() {
        return bot;
    }

    public IrcCommandSender getCommandSender() {
        return sender;
    }

    public void setCommandSender(IrcCommandSender sender) {
        this.sender = sender;
    }

    public SinkCommand getCommand() {
        return command;
    }

    public void setCommand(SinkCommand command) {
        this.command = command;
    }
}
