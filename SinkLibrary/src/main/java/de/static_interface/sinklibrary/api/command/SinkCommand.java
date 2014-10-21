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

import static de.static_interface.sinklibrary.configuration.LanguageConfiguration.m;

import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.api.exception.NotEnoughArgumentsException;
import de.static_interface.sinklibrary.api.exception.UnauthorizedAccessException;
import de.static_interface.sinklibrary.api.exception.UserNotFoundException;
import de.static_interface.sinklibrary.api.sender.IrcCommandSender;
import de.static_interface.sinklibrary.util.Debug;
import de.static_interface.sinklibrary.util.SinkIrcReflection;
import de.static_interface.sinklibrary.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public abstract class SinkCommand implements CommandExecutor {

    protected CommandSender sender;
    protected Plugin plugin;
    private SinkTabCompleterOptions defaultTabOptions = new SinkTabCompleterOptions(true, false, false);
    private SinkCommandOptions defaultCommandOptions = new SinkCommandOptions(false, false, false, false, false);
    private String usage = null;
    private String permission;
    private boolean onPreExecuteCalled = false;

    public SinkCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public final boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        this.sender = sender;
        boolean result = onPreExecute(sender, label, args);
        if (!onPreExecuteCalled) {
            throw new IllegalStateException(getClass().getName() + " did not call super.onPreExecute()");
        }
        return result;
    }

    public SinkTabCompleterOptions getTabCompleterOptions() {
        return defaultTabOptions;
    }

    public SinkCommandOptions getCommandOptions() {
        return defaultCommandOptions;
    }

    protected boolean onPreExecute(final CommandSender sender, final String label, final String[] args) {
        onPreExecuteCalled = true;
        if (getCommandOptions().isIrcOpOnly() && sender instanceof IrcCommandSender && !sender.isOp()) {
            throw new UnauthorizedAccessException();
        }

        if (!(sender instanceof IrcCommandSender) && getCommandOptions().isIrcOnly()) {
            Debug.log("isIrcOnly check failed: sender: " + sender.getClass().getSimpleName() + ", isIrcOnly: " + getCommandOptions().isIrcOnly());
            return false;
        }

        if (!(sender instanceof Player) && getCommandOptions().isPlayerOnly()) {
            Debug.log("isPlayerOnly check failed: sender: " + sender.getClass().getSimpleName() + ", isPlayerOnly: " + getCommandOptions()
                    .isPlayerOnly());
            return false;
        }

        boolean defaultNotices = false;
        if (getCommandOptions().useNotices() && sender instanceof IrcCommandSender) {
            defaultNotices = ((IrcCommandSender) sender).getUseNotice();
            ((IrcCommandSender) sender).setUseNotice(true);
        }

        Bukkit.getScheduler().runTask(plugin, new Runnable() {
            @Override
            public void run() {
                Exception exception = null;
                boolean success = false;
                try {
                    Debug.log("onExecute(" + sender.getName() + ", " + label + " [" +
                              StringUtil.formatArrayToString(args, ", ") + "])");
                    success = onExecute(sender, label, args);
                } catch (Exception e) {
                    exception = e;
                }

                onPostExecute(sender, label, args, exception, success);
            }
        });

        if (getCommandOptions().useNotices() && sender instanceof IrcCommandSender) {
            ((IrcCommandSender) sender).setUseNotice(defaultNotices);
        }

        return true;
    }

    protected abstract boolean onExecute(CommandSender sender, String label, String[] args);

    protected void onPostExecute(CommandSender sender, String label, String[] args, Exception exception, boolean success) {
        if (exception instanceof UnauthorizedAccessException) {
            sender.sendMessage(m("Permissions.General"));
            return;
        }

        if (exception instanceof NotEnoughArgumentsException) {
            sender.sendMessage(m("General.TooFewArguments"));
            return;
        }

        if (exception instanceof UserNotFoundException) {
            sender.sendMessage(exception.getMessage());
            return;
        }

        if (exception != null) {
            SinkLibrary.getInstance().getLogger().severe("Unexpected exception occurred: ");
            exception.printStackTrace();
            if (SinkLibrary.getInstance().getSettings().isDebugEnabled()) {
                sender.sendMessage(exception.getMessage());
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "An internal error occured");
            }
        }

        if (!success && !StringUtil.isStringEmptyOrNull(getUsage())) {
            sender.sendMessage(getUsage()); // todo: replace command prefix and <command> macro
        }
    }

    protected String getCommandPrefix() {
        if (sender instanceof IrcCommandSender) {
            return SinkIrcReflection.getIrcCommandPrefix();
        }
        return "/";
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public void setPlugin(Plugin plugin) {
        this.plugin = plugin;
    }
}
