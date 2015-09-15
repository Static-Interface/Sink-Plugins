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

import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.api.command.annotation.Aliases;
import de.static_interface.sinklibrary.api.command.annotation.DefaultPermission;
import de.static_interface.sinklibrary.api.command.annotation.Description;
import de.static_interface.sinklibrary.api.command.annotation.Permission;
import de.static_interface.sinklibrary.api.command.annotation.PermissionMessage;
import de.static_interface.sinklibrary.api.command.annotation.Usage;
import de.static_interface.sinklibrary.configuration.LanguageConfiguration;
import org.apache.commons.lang3.Validate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;

public class NativeCommand extends Command implements PluginIdentifiableCommand {

    private Plugin owningPlugin;
    private CommandExecutor executor;
    private TabCompleter completer;

    public NativeCommand(String name, Plugin plugin, CommandExecutor executor) {
        super(name);
        Validate.notNull(executor);
        Validate.notNull(plugin);
        this.owningPlugin = plugin;
        this.executor = executor;
        completer = SinkLibrary.getInstance().getDefaultTabCompleter();
        initAnnotations();
    }

    private void initAnnotations() {
        Permission perm = executor.getClass().getAnnotation(Permission.class);
        if (perm != null) {
            setPermission(perm.value());
        } else if (executor.getClass().getAnnotation(DefaultPermission.class) != null) {
            setPermission(getPlugin().getName().replace(" ", "_") + ".Command." + getName());
        }

        Aliases aliases = executor.getClass().getAnnotation(Aliases.class);
        if (aliases != null) {
            setAliases(Arrays.asList(aliases.value()));
        }

        Usage usage = executor.getClass().getAnnotation(Usage.class);
        if (usage != null) {
            setUsage(usage.value());
        }

        PermissionMessage permissionMessage = executor.getClass().getAnnotation(PermissionMessage.class);
        if (permissionMessage != null) {
            setPermissionMessage(permissionMessage.value());
        } else {
            setPermissionMessage(LanguageConfiguration.PERMISSIONS_GENERAL.format());
        }

        Description description = executor.getClass().getAnnotation(Description.class);
        if (description != null) {
            setDescription(description.value());
        }
    }

    public TabCompleter getTabCompleter() {
        return this.completer;
    }

    public void setTabCompleter(TabCompleter completer) {
        this.completer = completer;
    }

    public CommandExecutor getExecutor() {
        return this.executor;
    }

    public void setExecutor(CommandExecutor executor) {
        this.executor = executor == null ? this.owningPlugin : executor;
    }


    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws CommandException, IllegalArgumentException {
        org.apache.commons.lang.Validate.notNull(sender, "Sender cannot be null");
        org.apache.commons.lang.Validate.notNull(args, "Arguments cannot be null");
        org.apache.commons.lang.Validate.notNull(alias, "Alias cannot be null");
        List completions = null;

        try {
            if (completer != null) {
                completions = completer.onTabComplete(sender, this, alias, args);
            }

            if (completions == null && executor instanceof TabCompleter) {
                completions = ((TabCompleter) executor).onTabComplete(sender, this, alias, args);
            }
        } catch (Throwable thr) {
            StringBuilder message = new StringBuilder();
            message.append("Unhandled exception during tab completion for command \'/").append(alias).append(' ');

            for (String arg : args) {
                message.append(arg).append(' ');
            }

            message.deleteCharAt(message.length() - 1).append("\' in plugin ").append(getPlugin().getDescription().getFullName());
            throw new CommandException(message.toString(), thr);
        }

        return completions == null ? super.tabComplete(sender, alias, args) : completions;
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!owningPlugin.isEnabled()) {
            return false;
        }

        if (!testPermission(sender)) {
            return false;
        }

        boolean success;
        try {
            success = executor.onCommand(sender, this, commandLabel, args);
        } catch (Throwable thr) {
            throw new CommandException(
                    "Unhandled exception executing command \'" + commandLabel + "\' in plugin " + owningPlugin.getDescription().getFullName(), thr);
        }

        if (!success && usageMessage.length() > 0) {
            String[] lines = usageMessage.replace("<command>", commandLabel).split("\n");

            for (String line : lines) {
                sender.sendMessage(line);
            }
        }
        return success;
    }

    @Override
    public Plugin getPlugin() {
        return owningPlugin;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(super.toString());
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.append(", ").append(this.owningPlugin.getDescription().getFullName()).append(')');
        return stringBuilder.toString();
    }
}
