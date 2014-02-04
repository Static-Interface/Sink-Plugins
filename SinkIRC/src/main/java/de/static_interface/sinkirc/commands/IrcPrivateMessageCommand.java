/*
 * Copyright (c) 2014 adventuria.eu / static-interface.de
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.static_interface.sinkirc.commands;

import de.static_interface.sinkirc.SinkIRC;
import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.configuration.LanguageConfiguration;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jibble.pircbot.User;

public class IrcPrivateMessageCommand implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if ( args.length < 2 )
        {
            sender.sendMessage(LanguageConfiguration._("General.CommandMisused.Arguments.TooFew"));
            return true;
        }

        User target = SinkIRC.getIRCBot().getUser(args[0]);

        if ( target == null )
        {
            sender.sendMessage(LanguageConfiguration._("General.NotOnline").replace("%s", args[0]));
            return true;
        }

        String message = SinkLibrary.getUser(sender).getDisplayName() + ": ";

        for ( int x = 1; x < args.length; x++ ) message = message + ' ' + args[x];

        SinkIRC.getIRCBot().sendMessage(target.getNick(), message);

        sender.sendMessage(ChatColor.GREEN + "Nachricht gesendet!");

        return true;
    }
}
