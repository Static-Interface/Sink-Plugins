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

import de.static_interface.sinklibrary.api.configuration.Configuration;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class SinkCommand extends SinkCommandBase {
    private SinkTabCompleterOptions defaultTabOptions = new SinkTabCompleterOptions(true, false, false);
    private Command command;

    public SinkCommand(@Nonnull Plugin plugin) {
        super(plugin);
    }

    public SinkCommand(@Nonnull Plugin plugin, boolean async) {
        super(plugin, async);
    }

    public SinkCommand(@Nonnull Plugin plugin, @Nullable Configuration config) {
        super(plugin, config);
    }

    public SinkCommand(@Nonnull Plugin plugin, @Nullable Configuration config, boolean async) {
        super(plugin, config, async);
    }

    public SinkTabCompleterOptions getTabCompleterOptions() {
        return defaultTabOptions;
    }

    protected boolean onPreExecute(CommandSender sender, @Nullable Command command, String label, String[] args) {
        return true;
    }

    @Override
    public String getConfigPath() {
        return "Commands." + WordUtils.capitalizeFully(getName());
    }

    public String getName() {
        return command.getName();
    }

    @Nullable
    @Override
    public String getDescription() {
        return getCommand().getDescription();
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
        String usageSyntax = command.getUsage();
        if (usageSyntax != null && (usageSyntax.trim().equalsIgnoreCase("/<command>") || usageSyntax.trim()
                .equalsIgnoreCase("/" + getCommand().getName()))) {
            usageSyntax = null;
        }

        setUsageSyntax(usageSyntax);
        setPermission(command.getPermission());
    }
}
