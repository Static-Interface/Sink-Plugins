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

import static de.static_interface.sinklibrary.configuration.LanguageConfiguration.m;

import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.api.exception.NotEnoughArgumentsException;
import de.static_interface.sinklibrary.api.exception.UnauthorizedAccessException;
import de.static_interface.sinklibrary.api.exception.UserNotFoundException;
import de.static_interface.sinklibrary.api.sender.IrcCommandSender;
import de.static_interface.sinklibrary.util.Debug;
import de.static_interface.sinklibrary.util.SinkIrcReflection;
import de.static_interface.sinklibrary.util.StringUtil;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.StringWriter;

import javax.annotation.Nullable;

public abstract class SinkCommand implements CommandExecutor {

    protected CommandSender sender;
    protected Plugin plugin;
    private SinkTabCompleterOptions defaultTabOptions = new SinkTabCompleterOptions(true, false, false);
    private SinkCommandOptions defaultCommandOptions = new SinkCommandOptions(this);
    private String usage = null;
    private String permission;
    private boolean onPreExecuteCalled = false;
    private CommandLineParser parser = getCommandOptions().getCliParser();
    private CommandLine cmdLine = null;
    private String cmd = null;
    public SinkCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public final boolean onCommand(CommandSender sender, @Nullable Command command, String label, String[] args) {
        cmd = label.trim().replaceFirst(StringUtil.stripRegex(getCommandPrefix()), "").split(" ")[0];
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

        if (cmdLine != null && cmdLine.hasOption("h")) {
            sendUsage(sender);
            return true;
        }

        Bukkit.getScheduler().runTask(plugin, new Runnable() {
            @Override
            public void run() {
                Exception exception = null;
                boolean success = false;
                try {
                    Debug.log("onExecute(" + sender.getName() + ", " + label + " [" +
                              StringUtil.formatArrayToString(args, ", ") + "])");
                    String[] parsedCmdArgs = args;
                    String parsedLabel = label;
                    Options options = getCommandOptions().getCliOptions();
                    if (options != null) {
                        cmdLine = parser.parse(getCommandOptions().getCliOptions(), args);
                        parsedCmdArgs = cmdLine.getArgs();
                        parsedLabel = StringUtil.formatArrayToString(parsedCmdArgs, " ");
                    }
                    success = onExecute(sender, parsedLabel, parsedCmdArgs);
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

    protected abstract boolean onExecute(CommandSender sender, String label, String[] args) throws ParseException;

    protected void onPostExecute(CommandSender sender, String label, String[] args, Exception exception, boolean success) {
        boolean reportException = true;
        boolean exceptionReported = false;

        if (exception instanceof UnauthorizedAccessException) {
            sender.sendMessage(m("Permissions.General"));
            reportException = false;
        } else if (exception instanceof NotEnoughArgumentsException) {
            sender.sendMessage(m("General.TooFewArguments"));
            reportException = false;
        } else if (exception instanceof UserNotFoundException) {
            sender.sendMessage(exception.getMessage());
            reportException = false;
        } else if (exception instanceof ParseException) {
            sendUsage(sender);
            sender.sendMessage(ChatColor.DARK_RED + "Error: " + ChatColor.RED + exception.getMessage());
            return;
        }

        if (Debug.isEnabled() && exception != null) {
            exception.printStackTrace();
            exceptionReported = true;
        }

        if (exception != null && reportException) {
            if (!exceptionReported) {
                SinkLibrary.getInstance().getLogger().severe("Unexpected exception occurred: ");
                exception.printStackTrace();
            }

            if (SinkLibrary.getInstance().getSettings().isDebugEnabled()) {
                sender.sendMessage(exception.getMessage());
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "An internal error occured");
            }
        } else if (!success && !StringUtil.isEmptyOrNull(getUsage()) && reportException) {
            sendUsage(sender);
        }
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(getUsage().split(System.lineSeparator()));
    }

    protected String getCommandPrefix() {
        if (sender instanceof IrcCommandSender) {
            return SinkIrcReflection.getIrcCommandPrefix();
        }
        return "/";
    }

    public String getUsage() {
        Options options = getCommandOptions().getCliOptions();
        if (StringUtil.isEmptyOrNull(usage) && options != null) {
            StringWriter writer = new StringWriter();
            getCommandOptions().getCliHelpFormatter(writer);
            usage = writer.toString();
        }
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

    public CommandLine getCommandLine() {
        return cmdLine;
    }

    public CommandLineParser getCommandParser() {
        return parser;
    }

    public String getCmdAlias() {
        return cmd;
    }

    public void setCmdAlias(String cmd) {
        this.cmd = cmd;
    }
}
