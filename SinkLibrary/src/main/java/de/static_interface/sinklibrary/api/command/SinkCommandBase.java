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
import de.static_interface.sinklibrary.api.configuration.Configuration;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class SinkCommandBase implements CommandExecutor {

    private final boolean async;
    private final Plugin plugin;
    private final List<SinkSubCommand<?>> subCommands = new ArrayList<>();
    private String permission;
    private Configuration config;
    private SinkCommandOptions defaultCommandOptions = new SinkCommandOptions(this);
    private CommandLineParser parser = getCommandOptions().getCliParser();
    private CommandLine cmdLine;
    private String usageSyntax;

    public SinkCommandBase(@Nonnull Plugin plugin) {
        this(plugin, null, false);
    }

    public SinkCommandBase(@Nonnull Plugin plugin, boolean async) {
        this(plugin, null, async);
    }

    public SinkCommandBase(@Nonnull Plugin plugin, @Nullable Configuration config) {
        this(plugin, config, false);
    }

    public SinkCommandBase(@Nonnull Plugin plugin, @Nullable Configuration config, boolean async) {
        this.plugin = plugin;
        this.config = config;
        this.async = async;
    }

    public List<SinkSubCommand<?>> getSubCommands() {
        return Collections.unmodifiableList(subCommands);
    }

    public abstract String getName();

    protected abstract boolean onExecute(CommandSender sender, String label, String[] args) throws ParseException;

    public CommandLine getCommandLine() {
        return cmdLine;
    }

    public void setCommandLine(CommandLine cmdLine) {
        this.cmdLine = cmdLine;
    }

    public CommandLineParser getCommandParser() {
        return parser;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    protected String getCommandPrefix(@Nullable CommandSender sender) {
        if (sender instanceof IrcCommandSender) {
            return SinkIrcReflection.getIrcCommandPrefix().trim();
        }
        return "/";
    }

    @Nullable
    public String getUsage(CommandSender sender) {
        if (StringUtil.isEmptyOrNull(getUsageSyntax())) {
            return null;
        }

        return getCommandPrefix(sender) + getName() + " " + getUsageSyntax();
    }


    public boolean checkSender(CommandSender sender) {
        if (getCommandOptions().isIrcOnly() && !(sender instanceof IrcCommandSender)) {
            return false;
        }

        if (getCommandOptions().isIrcOpOnly() && sender instanceof IrcCommandSender && !sender.isOp()) {
            throw new NotEnoughPermissionsException(getPermission());
        }

        if (!getCommandOptions().isIrcEnabled() && sender instanceof IrcCommandSender) {
            return false;
        }

        if (!(sender instanceof Player) && getCommandOptions().isPlayerOnly()) {
            return false;
        }

        return true;
    }

    public void onRegistered() {

    }

    public final boolean hasSubCommands() {
        return subCommands.size() > 0;
    }

    public final <T extends SinkCommandBase> void registerSubCommand(SinkSubCommand<T> subCommand) {
        if (this instanceof SinkSubCommand) {
            if (this == subCommand) {
                throw new IllegalStateException("Trying to register a command as its own subcommand");
            }
        }

        subCommand.validateParent((T) this);
        subCommands.add(subCommand);
        subCommand.onRegistered();
    }

    @Nullable
    public final SinkSubCommand getSubCommand(String name) {
        for (SinkSubCommand cmd : subCommands) {
            if (cmd.getName().equalsIgnoreCase(name)) {
                return cmd;
            }
            if (cmd.getCommandAliases() == null) {
                continue;
            }

            for (Object s : cmd.getCommandAliases()) {
                if (s.toString().equalsIgnoreCase(name)) {
                    return cmd;
                }
            }
        }

        return null;
    }

    @Override
    public final boolean onCommand(final CommandSender sender, @Nullable Command command, final String label, String[] args) {
        Debug.logMethodCall(sender, command, label, args);

        if (!checkSender(sender)) {
            Debug.log("checkSender failed");
            return true;
        }

        if (args.length > 0 && hasSubCommands()) {
            SinkSubCommand subCmd = getSubCommand(args[0]);
            if (subCmd != null) {
                if (!testPermission(sender, subCmd.getPermission())) {
                    sender.sendMessage(GeneralLanguage.PERMISSIONS_GENERAL.getValue());
                    return true;
                }
                args = Arrays.copyOfRange(args, 1, args.length);
                subCmd.onCommand(sender, command, label, args);
                return true;
            } else if (getCommandOptions().isDefaultHelpEnabled() && (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?"))) {
                sendSubCommandList(sender, true);
                return true;
            }
        }

        boolean defaultNotices = false;
        if (getCommandOptions().useNotices() && sender instanceof IrcCommandSender) {
            defaultNotices = ((IrcCommandSender) sender).getUseNotice();
            ((IrcCommandSender) sender).setUseNotice(true);
        }

        CommandSender localSender = sender;

        if (localSender instanceof ProxiedNativeCommandSender) {
            localSender = ((ProxiedNativeCommandSender) localSender).getCallee();
        }

        if (!(localSender instanceof IrcCommandSender) && getPermission() != null && !localSender.hasPermission(getPermission()) && !localSender
                .isOp()) {
            sender.sendMessage(GeneralLanguage.PERMISSIONS_GENERAL.format());
            return true;
        }

        final Command finalCommand = command;

        final String[] finalArgs = args;
        CommandTask task = new CommandTask() {
            @Override
            public boolean execute() {
                Exception exception = null;

                boolean success = true;
                boolean preExecuteSuccess;
                try {
                    preExecuteSuccess = onPreExecute(sender, finalCommand, label, finalArgs);
                } catch (Exception e) {
                    preExecuteSuccess = false;
                    exception = e;
                }

                if (preExecuteSuccess) {
                    try {
                        if (!testPermission(sender, getPermission())) {
                            throw new NotEnoughPermissionsException(getPermission());
                        }

                        String[] parsedCmdArgs = parseCmdArgs(finalArgs);
                        Options options = getCommandOptions().getCliOptions();
                        setCommandLine(getCommandParser().parse(options, parsedCmdArgs));
                        if (getCommandOptions().hasCliOptions() && getCommandOptions().isDefaultHelpEnabled() && getCommandLine().hasOption('h')) {
                            sendCliUsage(sender, finalCommand);
                            return true;
                        }
                        parsedCmdArgs = parseCmdArgs(getCommandLine().getArgs());
                        String parsedLabel = StringUtil.formatArrayToString(parsedCmdArgs, " ");

                        int minArgs = getCommandOptions().getMinRequiredArgs();

                        success = (minArgs <= 0 || parsedCmdArgs.length >= minArgs) && onExecute(sender, parsedLabel, parsedCmdArgs);
                    } catch (Exception e) {
                        exception = e;
                    }
                }

                boolean postHandled = onPostExecute(sender, finalCommand, label, finalArgs, success, exception);
                return postHandled || success;

            }
        };

        if (getCommandOptions().useNotices() && sender instanceof IrcCommandSender) {
            ((IrcCommandSender) sender).setUseNotice(defaultNotices);
        }

        if (async) {
            Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), task);
            return true;
        } else {
            return task.execute();
        }
    }

    public void sendSubCommandList(CommandSender sender, boolean includeSelf) {
        if (includeSelf) {
            sender.sendMessage(ChatColor.GOLD + "+++++ Help: " + ChatColor.RED + getCommandPrefix(sender) + getName() + ChatColor.GOLD + " +++++");
            if (getUsage(sender) != null) {
                String
                        description =
                        StringUtil.isEmptyOrNull(getDescription()) ? "" : ChatColor.RESET.toString() + ChatColor.GRAY + ": " + getDescription();
                sender.sendMessage(ChatColor.GOLD + getUsage(sender) + description);
            }
        }

        if (hasSubCommands()) {
            for (SinkSubCommand subCommand : getSubCommands()) {
                if (sender instanceof IrcCommandSender && !subCommand.getCommandOptions().isIrcEnabled()) {
                    continue;
                }

                if (!subCommand.testPermission(sender, subCommand.getPermission())) {
                    continue;
                }
                String
                        description =
                        StringUtil.isEmptyOrNull(subCommand.getDescription()) ? "" : ChatColor.RESET.toString() + ChatColor.GRAY + ": " + subCommand
                                .getDescription();
                String
                        usage =
                        StringUtil.isEmptyOrNull(subCommand.getUsage(sender)) ? subCommand.getCommandPrefix(sender) + subCommand.getName()
                                                                              : subCommand.getUsage(sender);
                sender.sendMessage(ChatColor.GOLD + usage + description);
            }
        }
    }

    @Nullable
    public abstract String getDescription();

    public boolean testPermission(CommandSender sender, String permission) {
        if ((sender instanceof IrcCommandSender)) {
            return getCommandOptions().isIrcEnabled() && (!getCommandOptions().isIrcOpOnly() || sender.isOp());
        }

        return StringUtil.isEmptyOrNull(permission) || sender.hasPermission(permission);
    }

    protected boolean onPostExecute(CommandSender sender, Command command, String label, String[] args, boolean success,
                                    @Nullable Exception exception) {
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
            sendUsage(sender, false);
            reportException = false;
        } else if (exception instanceof UserNotFoundException || exception instanceof UserNotOnlineException) {
            sender.sendMessage(GeneralLanguage.GENERAL_ERROR.format(exception.getMessage()));
            reportException = false;
        } else if (exception instanceof ParseException) {
            sendCliUsage(sender, command);
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
        } else if (!success && reportException) {
            sendUsage(sender, true);
            return true;
        }
        return false;
    }

    public boolean handleException(CommandSender sender, @Nullable Command command, String label, String[] args, Exception exception) {
        return false;
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

    public final SinkCommandOptions getCommandOptions() {
        return defaultCommandOptions;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public <T> T getArg(String[] args, int index, Class<T> returnType) {
        if (args.length - 1 < index) {
            throw new NotEnoughArgumentsException("Expected length: " + (index + 1) + ", given length:" + args.length);
        }

        return CommandUtil.parseValue(new String[]{args[index]}, returnType, false);
    }

    public void sendCliUsage(CommandSender sender, Command command) {
        Debug.logMethodCall(sender, command);

        String commandLineUsage;
        if (!getCommandOptions().hasCliOptions()) {
            throw new IllegalStateException("getCliUsage: Command: " + getPlugin().getName() + ":" + getName() + " doesn't have cli options!");
        }
        StringWriter writer = new StringWriter();
        getCommandOptions().getCliHelpFormatter(sender, command, writer);
        commandLineUsage = writer.toString();
        if (!StringUtil.isEmptyOrNull(commandLineUsage)) {
            commandLineUsage += System.lineSeparator() + commandLineUsage;
            sender.sendMessage(commandLineUsage);
        }
    }

    public void sendUsage(CommandSender sender, boolean sendSubCommands) {
        if (sendSubCommands && hasSubCommands()) {
            sendSubCommandList(sender, true);
        } else {
            String usage = getUsage(sender);
            if (!StringUtil.isEmptyOrNull(usage)) {
                sender.sendMessage(ChatColor.DARK_RED + "Usage: " + ChatColor.RED + usage);
            }
        }
    }


    public String getSubPermission(String perm) {
        if (getPermission() == null) {
            throw new IllegalStateException("Can not get get sub permission if getPermission() == null!");
        }
        return getPermission() + "." + perm;
    }

    @Nullable
    public String getUsageSyntax() {
        if (getCommandOptions().hasCliOptions() && StringUtil.isEmptyOrNull(usageSyntax)) {
            usageSyntax = "<options>";
        }
        return usageSyntax;
    }

    public void setUsageSyntax(@Nullable String usage) {
        if (usage != null) {
            usage = usage.replaceAll("/<command>\\s?", "").trim();
        }

        this.usageSyntax = usage;
    }

    @Nullable
    public Configuration getConfig() {
        return config;
    }

    protected abstract boolean onPreExecute(CommandSender sender, Command cmd, String label, String[] args);

    public abstract String getConfigPath();

    private abstract class CommandTask implements Runnable {

        @Override
        public void run() {
            execute();
        }

        public abstract boolean execute();
    }
}
