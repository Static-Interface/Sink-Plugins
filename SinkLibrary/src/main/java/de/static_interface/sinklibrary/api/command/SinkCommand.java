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

import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.api.exception.NotEnoughArgumentsException;
import de.static_interface.sinklibrary.api.exception.NotEnoughPermissionsException;
import de.static_interface.sinklibrary.api.exception.UserNotFoundException;
import de.static_interface.sinklibrary.api.exception.UserNotOnlineException;
import de.static_interface.sinklibrary.api.sender.IrcCommandSender;
import de.static_interface.sinklibrary.configuration.GeneralLanguage;
import de.static_interface.sinklibrary.configuration.GeneralSettings;
import de.static_interface.sinklibrary.util.CommandUtil;
import de.static_interface.sinklibrary.util.Debug;
import de.static_interface.sinklibrary.util.SinkIrcReflection;
import de.static_interface.sinklibrary.util.StringUtil;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.command.ProxiedNativeCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class SinkCommand implements CommandExecutor {

    private final boolean async;
    protected CommandSender sender;
    protected Plugin plugin;
    private SinkTabCompleterOptions defaultTabOptions = new SinkTabCompleterOptions(true, false, false);
    private SinkCommandOptions defaultCommandOptions = new SinkCommandOptions(this);
    private String permission;
    private CommandLineParser parser = getCommandOptions().getCliParser();
    private CommandLine cmdLine = null;
    private String cmd = null;
    private String commandUsage = null;
    public SinkCommand(@Nonnull Plugin plugin) {
        this(plugin, false);
    }

    public SinkCommand(@Nonnull Plugin plugin, boolean async) {
        Validate.notNull(plugin);
        this.plugin = plugin;
        this.async = async;
    }

    @Override
    public final boolean onCommand(final CommandSender sender, @Nullable Command command, final String label, final String[] args) {
        if (getCommandOptions().isIrcOnly() && !(sender instanceof IrcCommandSender)) {
            return true; // do nothing
        }

        this.sender = sender;
        if (getCommandOptions().isIrcOpOnly() && sender instanceof IrcCommandSender && !sender.isOp()) {
            throw new NotEnoughPermissionsException();
        }

        if (!(sender instanceof IrcCommandSender) && getCommandOptions().isIrcOnly()) {
            return false;
        }

        if (!(sender instanceof Player) && getCommandOptions().isPlayerOnly()) {
            return false;
        }

        boolean defaultNotices = false;
        if (getCommandOptions().useNotices() && sender instanceof IrcCommandSender) {
            defaultNotices = ((IrcCommandSender) sender).getUseNotice();
            ((IrcCommandSender) sender).setUseNotice(true);
        }

        CommandSender localSender = sender;
        cmd = label.trim().replaceFirst(StringUtil.stripRegex(getCommandPrefix()), "").split(" ")[0];
        command = Bukkit.getPluginCommand(plugin.getName() + ":" + cmd);
        if (command != null) {
            permission = command.getPermission();
            commandUsage = command.getUsage();

            if (localSender instanceof ProxiedNativeCommandSender) {
                localSender = ((ProxiedNativeCommandSender) localSender).getCallee();
            }

            if (!(localSender instanceof IrcCommandSender) && permission != null && !localSender.hasPermission(permission) && !localSender.isOp()) {
                sender.sendMessage(GeneralLanguage.PERMISSIONS_GENERAL.format());
                return true;
            }
        }

        final Command finalCommand = command;

        CommandTask task = new CommandTask() {
            @Override
            public boolean execute() {
                Exception exception = null;

                boolean success = true;
                boolean preExecuteSuccess;
                try {
                    preExecuteSuccess = onPreExecute(sender, finalCommand, label, args);
                } catch (Exception e) {
                    preExecuteSuccess = false;
                    exception = e;
                }

                if (preExecuteSuccess) {
                    try {
                        String[] parsedCmdArgs = parseCmdArgs(args);
                        String parsedLabel = label;
                        Options options = getCommandOptions().getCliOptions();
                        if (options != null) {
                            cmdLine = parser.parse(options, parsedCmdArgs);
                            if (getCommandOptions().isDefaultHelpEnabled() && cmdLine.hasOption('h')) {
                                sendUsage(sender, finalCommand);
                                return true;
                            }
                            parsedCmdArgs = parseCmdArgs(cmdLine.getArgs());
                            parsedLabel = StringUtil.formatArrayToString(parsedCmdArgs, " ");
                        }
                        success = onExecute(sender, parsedLabel, parsedCmdArgs);
                    } catch (Exception e) {
                        exception = e;
                    }
                }

                boolean postHandled = onPostExecute(sender, finalCommand, label, args, success, exception);
                return postHandled || success || StringUtil.isEmptyOrNull(getUsage());

            }
        };

        if (getCommandOptions().useNotices() && sender instanceof IrcCommandSender) {
            ((IrcCommandSender) sender).setUseNotice(defaultNotices);
        }

        if (async) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
            return true;
        } else {
            return task.execute();
        }
    }

    private String[] parseCmdArgs(String[] args) {
        String sourceString = StringUtil.formatArrayToString(args, " ");
        List<String> arguments = new ArrayList<>();
        sourceString = sourceString.trim();

        String singleArgument = "";
        boolean inSingleQuotation = false;
        boolean inDoubleQuotation = false;

        for (char c : sourceString.toCharArray()) {
            if (c == '\'' && !inDoubleQuotation) {
                inSingleQuotation = !inSingleQuotation;
            }

            if (c == '"' && !inSingleQuotation) {
                inDoubleQuotation = !inDoubleQuotation;
            }

            if (c == ' ') {
                if (inSingleQuotation || inDoubleQuotation) {
                    singleArgument += c;
                } else {
                    arguments.add(singleArgument);
                    singleArgument = "";
                }
            } else {
                singleArgument += c;
            }
        }

        if (singleArgument.length() > 0) {
            arguments.add(singleArgument);
        }

        return arguments.toArray(new String[arguments.size()]);
    }

    public SinkTabCompleterOptions getTabCompleterOptions() {
        return defaultTabOptions;
    }

    public SinkCommandOptions getCommandOptions() {
        return defaultCommandOptions;
    }

    public boolean onPreExecute(CommandSender sender, @Nullable Command command, String label, String[] args) {
        return true;
    }

    protected abstract boolean onExecute(CommandSender sender, String label, String[] args) throws ParseException;

    public boolean onPostExecute(CommandSender sender, Command command, String label, String[] args, boolean success, @Nullable Exception exception) {
        return handleResult(sender, command, label, args, exception, success);
    }

    protected final boolean handleResult(CommandSender sender, Command command, String label, String[] args, Exception exception, boolean success) {
        boolean reportException = true;
        boolean exceptionReported = false;

        if (handleException(sender, command, label, args, exception)) {
            return true;
        } else if (exception instanceof NotEnoughPermissionsException) {
            sender.sendMessage(GeneralLanguage.PERMISSIONS_GENERAL.format());
            reportException = false;
        } else if (exception instanceof NotEnoughArgumentsException) {
            sender.sendMessage(GeneralLanguage.GENERAL_NOT_ENOUGH_ARGUMENTS.format());
            reportException = false;
        } else if (exception instanceof UserNotFoundException || exception instanceof UserNotOnlineException) {
            sender.sendMessage(GeneralLanguage.GENERAL_ERROR.format(exception.getMessage()));
            reportException = false;
        } else if (exception instanceof ParseException) {
            sendUsage(sender, command);
            sender.sendMessage(GeneralLanguage.GENERAL_ERROR.format(exception.getMessage()));
            return true;
        } else if (exception instanceof NumberFormatException) {
            sender.sendMessage(GeneralLanguage.GENERAL_ERROR.format(exception.getMessage()));
            reportException = false;
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

            if (GeneralSettings.GENERAL_DEBUG.getValue()) {
                sender.sendMessage(exception.getMessage());
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "An internal error occured.");
            }
        } else if (!success && !StringUtil.isEmptyOrNull(getUsage()) && reportException) {
            sendUsage(sender, command);
            return true;
        }
        return false;
    }

    public boolean handleException(CommandSender sender, @Nullable Command command, String label, String[] args, Exception exception) {
        return false;
    }

    protected void sendUsage(CommandSender sender, Command command) {
        String usage = getUsage();
        if (usage == null) {
            return;
        }

        if (command != null) {
            usage = getUsage().replace("<command>", command.getName());
        }
        sender.sendMessage(usage.split(System.lineSeparator()));
    }

    protected String getCommandPrefix() {
        if (sender instanceof IrcCommandSender) {
            return SinkIrcReflection.getIrcCommandPrefix();
        }
        return "/";
    }

    public String getUsage() {
        Options options = getCommandOptions().getCliOptions();
        String usage = commandUsage;
        if (options != null) {
            if (StringUtil.isEmptyOrNull(getCommandOptions().getCmdLineSyntax())) {
                getCommandOptions().setCmdLineSyntax("{PREFIX}{ALIAS} <options>");
            }
            StringWriter writer = new StringWriter();
            getCommandOptions().getCliHelpFormatter(writer);
            usage = writer.toString();
            if (!StringUtil.isEmptyOrNull(commandUsage)) {
                usage += System.lineSeparator() + commandUsage;
            }
        }
        return usage;
    }

    public void setUsage(String usage) {
        this.commandUsage = usage;
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

    public <T> T getArg(String[] args, int index, Class<T> returnType) {
        if (args.length - 1 < index) {
            throw new NotEnoughArgumentsException("Expected length: " + (index + 1) + ", given length:" + args.length);
        }

        return CommandUtil.parseValue(new String[]{args[index]}, returnType, false);
    }

    private abstract class CommandTask implements Runnable {

        @Override
        public void run() {
            execute();
        }

        public abstract boolean execute();
    }
}
