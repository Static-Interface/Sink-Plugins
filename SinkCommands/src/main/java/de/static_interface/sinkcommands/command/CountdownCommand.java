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

package de.static_interface.sinkcommands.command;

import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.api.command.SinkCommand;
import de.static_interface.sinklibrary.api.command.annotation.Aliases;
import de.static_interface.sinklibrary.api.command.annotation.DefaultPermission;
import de.static_interface.sinklibrary.api.command.annotation.Description;
import de.static_interface.sinklibrary.api.command.annotation.Usage;
import de.static_interface.sinklibrary.api.configuration.Configuration;
import de.static_interface.sinklibrary.api.user.SinkUser;
import de.static_interface.sinklibrary.stream.BukkitBroadcastMessageStream;
import de.static_interface.sinklibrary.user.IngameUser;
import de.static_interface.sinklibrary.util.StringUtil;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

@DefaultPermission
@Aliases("cd")
@Description("Start a countdown")
@Usage("[options] <reason>")
public class CountdownCommand extends SinkCommand {

    public static final String PREFIX = ChatColor.DARK_RED + "[" + ChatColor.RED + "CountDown" + ChatColor.DARK_RED + "] " + ChatColor.RESET;
    private static long secondsLeftGlobal;

    //Todo: allow multiple local countdowns at the same time
    private List<IngameUser> cachedUsers = new ArrayList<>();

    public CountdownCommand(Plugin plugin, Configuration config) {
        super(plugin, config);
        getCommandOptions().setIrcOpOnly(true);
        getCommandOptions().setCliOptions(buildOptions());
        SinkLibrary.getInstance().registerMessageStream(new BukkitBroadcastMessageStream("scmd_countdown"));
    }

    private Options buildOptions() {
        Options options = new Options();
        Option global = Option.builder("g")
                .desc("Announce countdown globally")
                .longOpt("global")
                .build();

        Option radius = Option.builder("r")
                .hasArg()
                .longOpt("radius")
                .desc("Set countdown announce radius")
                .type(Number.class)
                .argName("value")
                .build();

        Option time = Option.builder("t")
                .hasArg()
                .longOpt("time")
                .desc("Set time")
                .type(Number.class)
                .argName("seconds")
                .build();

        Option command = Option.builder("c")
                .hasArgs()
                .longOpt("command")
                .valueSeparator(';')
                .desc("Execute command on finish")
                .argName("command")
                .build();

        Option skipLastMsg = Option.builder("s")
                .longOpt("skipmsg")
                .desc("Skip message when countdown finishes")
                .build();

        options.addOption(global);
        options.addOption(radius);
        options.addOption(time);
        options.addOption(command);
        options.addOption(skipLastMsg);

        return options;
    }

    @Override
    public boolean onExecute(CommandSender sender, String label, String[] args) throws ParseException {
        if (args.length < 1) {
            return false;
        }

        int seconds = 30;
        if (getCommandLine().hasOption('t')) {
            seconds = ((Number) getCommandLine().getParsedOptionValue("t")).intValue();
        }

        if (seconds < 0) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Countdown darf nicht negative sein!");
            return true;
        }

        if (seconds > 60) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Max Zeit: 60 Sekunden");
            return true;
        }

        if (getCommandLine().hasOption('g')) {
            if (getCommandLine().hasOption('r')) {
                sender.sendMessage(ChatColor.DARK_RED + "You can't use the -g and the -r flag at the same time!");
                return true;
            }
            if (secondsLeftGlobal > 0) {
                sender.sendMessage(PREFIX + ChatColor.RED + "Es läuft bereits ein globaler Countdown!");
                return true;
            } else {
                secondsLeftGlobal = seconds;
            }
        }

        String message = StringUtil.formatArrayToString(getCommandLine().getArgs(), " ");

        String command = null;
        if (getCommandLine().hasOption('c')) {
            command = StringUtil.formatArrayToString(getCommandLine().getOptionValues('c'), " ");
        }

        boolean skipLastMsg = getCommandLine().hasOption('s');

        if (getCommandLine().hasOption('g')) {
            broadcastCounterGlobal(sender, message, command, skipLastMsg);
            return true;
        }

        int radius = 30;
        if (getCommandLine().hasOption('r')) {
            radius = ((Number) getCommandLine().getParsedOptionValue("r")).intValue();
        }

        SinkUser executor = SinkLibrary.getInstance().getUser((Object) sender);
        if (!(executor instanceof IngameUser)) {
            sender.sendMessage(ChatColor.DARK_RED + "Only ingame players can use this without the -g flag");
            sender.sendMessage(ChatColor.DARK_RED + "Please use " + getCommandPrefix(sender) + "countdown -g <options> <message>");
            return true;
        }
        broadcastCounterLocal((IngameUser) executor, message, command, radius, skipLastMsg, seconds);
        return true;
    }

    private void broadcastCounterGlobal(final CommandSender sender, String message, final String command, final boolean skipLastMsg) {
        broadcastMessage(PREFIX + ChatColor.GOLD + "Countdown für " + message + " gestartet!");
        new BukkitRunnable() {
            @Override
            public void run() {
                if (secondsLeftGlobal <= 0) {
                    secondsLeftGlobal = 0;
                    if (!skipLastMsg) {
                        broadcastMessage(PREFIX + ChatColor.GREEN + "Los!");
                    }
                    if (command != null) {
                        Bukkit.dispatchCommand(sender, command);
                    }
                    cancel();
                    return;
                }
                if (secondsLeftGlobal > 10) {
                    if (secondsLeftGlobal % 10 == 0) {
                        broadcastMessage(PREFIX + ChatColor.RED + secondsLeftGlobal + "...");
                    }
                } else {
                    broadcastMessage(PREFIX + ChatColor.DARK_RED + secondsLeftGlobal);
                }
                secondsLeftGlobal--;
            }
        }.runTaskTimer(getPlugin(), 0, 20L);
    }

    private void broadcastMessage(String s) {
        SinkLibrary.getInstance().getMessageStream("scmd_countdown").sendMessage(s);
    }

    private void broadcastCounterLocal(final IngameUser executor, String message, final String command, final int radius, final boolean skipLastMsg,
                                       final int seconds) {
        for (IngameUser user : executor.getUsersInRadius(radius)) {
            user.sendMessage(PREFIX + ChatColor.GOLD + "Countdown für " + message + " gestartet!");
        }

        final int[] secondsLeft = {seconds};
        new BukkitRunnable() {
            @Override
            public void run() {
                if (secondsLeft[0] <= 0) {
                    cachedUsers = new ArrayList<>();
                    if (!skipLastMsg) {
                        for (IngameUser user : executor.getUsersInRadius(radius)) {
                            user.sendMessage(PREFIX + ChatColor.GREEN + "Los!");
                        }
                    }
                    if (command != null) {
                        Bukkit.dispatchCommand(executor.getSender(), command);
                    }
                    cancel();
                    return;
                }
                List<IngameUser> usersInRadius = (executor.isOnline() ? executor.getUsersInRadius(radius) : cachedUsers);
                if (secondsLeft[0] > 10) {
                    if (secondsLeft[0] % 10 == 0) {
                        for (IngameUser user : usersInRadius) {
                            if (!cachedUsers.contains(user)) {
                                cachedUsers.add(user);
                            }
                            user.sendMessage(PREFIX + ChatColor.RED + secondsLeft[0] + "...");
                        }
                    }
                } else {
                    for (IngameUser user : usersInRadius) {
                        user.sendMessage(PREFIX + ChatColor.DARK_RED + secondsLeft[0]);
                    }
                }
                secondsLeft[0]--;
            }
        }.runTaskTimer(getPlugin(), 0, 20L);
    }
}
