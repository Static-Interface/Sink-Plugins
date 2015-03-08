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

package de.static_interface.sinklibrary.sender;

import de.static_interface.sinklibrary.api.sender.ProxiedCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;

public class ProxiedCommandSenderConversable extends ProxiedCommandSender implements Conversable {

    public ProxiedCommandSenderConversable(CommandSender base, CommandSender faker) {
        super(base, faker);
        if (!(base instanceof Conversable)) {
            throw new IllegalArgumentException("Base is not instanceof conversable!");
        }
    }

    @Override
    public boolean isConversing() {
        return ((Conversable) getCallee()).isConversing();
    }

    @Override
    public void acceptConversationInput(String s) {
        ((Conversable) getCallee()).acceptConversationInput(s);
    }

    @Override
    public boolean beginConversation(Conversation conversation) {
        return ((Conversable) getCallee()).beginConversation(conversation);
    }

    @Override
    public void abandonConversation(Conversation conversation) {
        ((Conversable) getCallee()).abandonConversation(conversation);
    }

    @Override
    public void abandonConversation(Conversation conversation, ConversationAbandonedEvent conversationAbandonedEvent) {
        ((Conversable) getCallee()).abandonConversation(conversation, conversationAbandonedEvent);
    }

    @Override
    public void sendRawMessage(String s) {
        ((Conversable) getCallee()).sendRawMessage(s);
        if (getCaller() instanceof Conversable) {
            ((Conversable) getCaller()).sendRawMessage(s);
        } else {
            getCaller().sendMessage(s);
        }
    }
}
