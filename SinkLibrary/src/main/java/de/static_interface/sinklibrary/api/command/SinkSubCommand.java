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

import de.static_interface.sinklibrary.util.CommandUtil;
import de.static_interface.sinklibrary.util.ReflectionUtil;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.lang.reflect.AnnotatedElement;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class SinkSubCommand<T extends SinkCommandBase> extends SinkCommandBase implements ConfigurableCommand {

    private final String name;
    private T parentCommand;
    private String description;
    private List<String> aliases;

    public SinkSubCommand(T parentCommand, String name) {
        super(parentCommand.getPlugin(), parentCommand.getConfig(), false);
        this.name = name.toLowerCase();
        this.parentCommand = parentCommand;

        AnnotatedElement element;
        if (this.getClass().isAnonymousClass()) {
            try {
                element = ReflectionUtil.getDeclaredMethod(getClass(), "onExecute", CommandSender.class, String.class, String[].class);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        } else {
            element = getClass();
        }

        CommandUtil.initAnnotations(this, element);
    }

    @Override
    protected boolean onPreExecute(CommandSender sender, Command cmd, String label, String[] args) {
        return true;
    }

    public T getParentCommand() {
        return parentCommand;
    }

    void validateParent(T parentCommand) {
        if (this.parentCommand != parentCommand) {
            throw new RuntimeException("Command " + getConfigPath() + " parent is wrong!");
        }
    }

    @Override
    public final String getName() {
        return name;
    }

    @Override
    protected abstract boolean onExecute(CommandSender sender, String label, String[] args) throws ParseException;

    @Nullable
    @Override
    public String getDescription() {
        return getCommandDescription();
    }

    @Override
    protected String getCommandPrefix(CommandSender sender) {
        String s = "";
        SinkCommandBase parent = getParentCommand();
        while (true) {
            s = parent.getName() + " " + s;
            if (parent instanceof SinkSubCommand) {
                parent = ((SinkSubCommand) parent).getParentCommand();
                continue;
            }
            break;
        }

        return super.getCommandPrefix(sender).trim() + s.trim() + " ";
    }

    @Nullable
    @Override
    public String getCommandPermission() {
        return getPermission();
    }

    @Override
    public void setCommandPermission(String permission) {
        setPermission(permission);
    }

    @Nonnull
    @Override
    public String getConfigPath() {
        return getParentCommand().getConfigPath() + ".SubCommand." + WordUtils.capitalizeFully(getName());
    }

    @Nullable
    @Override
    public List<String> getCommandAliases() {
        return aliases;
    }

    @Override
    public void setCommandAliases(List<String> aliases) {
        this.aliases = aliases;
    }

    @Nullable
    @Override
    public String getCommandUsage() {
        return getUsageSyntax();
    }

    @Override
    public void setCommandUsage(String usage) {
        setUsageSyntax(usage);
    }

    @Nullable
    @Override
    public String getCommandDescription() {
        return description;
    }

    @Override
    public void setCommandDescription(String description) {
        this.description = description;
    }

    @Nullable
    @Override
    public String getDefaultPermission() {
        if (getParentCommand().getPermission() == null) {
            return null;
        }
        return getParentCommand().getPermission() + "." + getName().toLowerCase();
    }
}
