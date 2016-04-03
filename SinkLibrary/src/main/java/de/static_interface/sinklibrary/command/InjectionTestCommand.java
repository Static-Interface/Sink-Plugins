/*
 * Copyright (c) 2013 - 2016 http://static-interface.de and contributors
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

package de.static_interface.sinklibrary.command;

import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.api.command.SinkSubCommand;
import de.static_interface.sinklibrary.api.injection.Inject;
import de.static_interface.sinklibrary.api.injection.InjectTarget;
import de.static_interface.sinklibrary.api.injection.Injector;
import org.apache.commons.cli.ParseException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import javax.annotation.Nullable;

public class InjectionTestCommand extends SinkSubCommand<SinkDebugCommand> {

    public InjectionTestCommand(SinkDebugCommand sinkDebugCommand) {
        super(sinkDebugCommand, "inject");
    }

    @Inject(targetClass = "de.static_interface.sinklibrary.api.command.SinkCommand",
            method = "onPreExecute",
            methodArgNames = {"sender", "command", "label", "args"},
            injectTarget = InjectTarget.BEFORE_METHOD)
    public static void onPreExecuteInject(CommandSender sender, @Nullable Command command, String label, String[] args) {
        SinkLibrary.getInstance().sendIrcMessage("onPreExecute from " + sender.getName(), "#dev");
    }

    @Override
    protected boolean onExecute(CommandSender sender, String label, String[] args) throws ParseException {
        try {
            Injector.loadInjections(getClass());
        } catch (Exception e) {
            sender.sendMessage(e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
            return true;
        }
        sender.sendMessage("No exceptions occurred, success?");
        return true;
    }
}