/*
 * Copyright (c) 2014 http://adventuria.eu, http://static-interface.de and contributors
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.static_interface.sinkcommands.command;

import static de.static_interface.sinklibrary.Constants.COMMAND_PREFIX;

import de.static_interface.sinklibrary.BukkitUtil;
import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.SinkUser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class VotekickCommands {

    public static final String PREFIX = ChatColor.DARK_GREEN + ChatColor.BOLD.toString() + "[VoteKick] " + ChatColor.RESET;

    static double votesYes = 0;
    static double votesNo = 0;
    static boolean voteStarted;
    static Player targetPlayer;

    static String target;

    static List<CommandSender> votedPlayers = new ArrayList<>();

    public static void endVoteKick(Plugin plugin) {
        if (voteStarted) {
            int percentYes = (int) Math.round((votesYes / (votesYes + votesNo)) * 100);
            int percentNo = (int) Math.round((votesNo / (votesYes + votesNo)) * 100);
            if ((percentYes + percentNo) <= 1) {
                BukkitUtil.broadcast(PREFIX + ChatColor.RED + target + ChatColor.RED + " wurde nicht gekickt, da zu wenige Spieler gevotet haben.",
                                     "sinkcommands.votekick.vote", true);
            }
            if (percentYes > 50) {
                BukkitUtil.broadcast(
                        PREFIX + ChatColor.RED + target + ChatColor.RED + " wurde gekickt, weil die Mehrheit der Spieler dafür war (Ja: " + percentYes
                        + "%, Nein: " + percentNo + "%). Er wurde für 5 Minuten gebannt.", "sinkcommands.votekick.vote", true);
                final String username = targetPlayer.getName();
                targetPlayer.kickPlayer(ChatColor.RED + "Du wurdest durch einen Votekick gekickt und für 5 Minuten gebannt.");
                SinkLibrary.addTempBan(username);
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        SinkLibrary.removeTempBan(username);
                    }
                }, 20 * 300); //Unban player after 5 minutes
            } else {
                BukkitUtil.broadcast(
                        PREFIX + ChatColor.RED + target + " wurde nicht gekickt, da die Mehrheit der Spieler dagegen war (Ja: " + percentYes
                        + "%, Nein: " + percentNo + "%).", "sinkcommands.votekick.vote", true);
            }
        }
        votesYes = 0;
        votesNo = 0;
        targetPlayer = null;
        voteStarted = false;
        votedPlayers.clear();
    }

    @SuppressWarnings("BooleanMethodNameMustStartWithQuestion")
    static void sendStatus(CommandSender sender) {
        if (!VotekickCommands.voteStarted) {
            sender.sendMessage(PREFIX + "Derzeit läuft kein Votekick...");
            return;
        }
        int percentYes = (int) Math.round((votesYes / (votesYes + votesNo)) * 100);
        int percentNo = (int) Math.round((votesNo / (votesYes + votesNo)) * 100);
        sender.sendMessage(PREFIX + "Status: Ja: " + percentYes + "%, Nein: " + percentNo + "%.");
    }

    @SuppressWarnings("BooleanMethodNameMustStartWithQuestion")
    static boolean vote(CommandSender sender, boolean yes, Plugin plugin) {
        if (!VotekickCommands.voteStarted) {
            sender.sendMessage(PREFIX + "Derzeit läuft kein Votekick...");
            return true;
        }
        if (votedPlayers.contains(sender)) {
            sender.sendMessage(PREFIX + "Du kannst nur einmal voten!");
            return true;
        }
        if (yes) {
            sender.sendMessage(PREFIX + "Du hast für einen Kick gestimmt!");
            votesYes = votesYes + 1;
        } else {
            sender.sendMessage(PREFIX + "Du hast gegen einen Kick gestimmt!");
            votesNo = votesNo + 1;
        }
        votedPlayers.add(sender);
        sendStatus(sender);

        if ((votesYes + votesNo) == BukkitUtil.getOnlinePlayers().size()) {
            VotekickCommands.endVoteKick(plugin);
        }
        return true;
    }

    public static class VotekickCommand implements CommandExecutor {

        Plugin plugin;

        public VotekickCommand(Plugin plugin) {
            this.plugin = plugin;
        }

        @Override
        public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
            if (args.length < 1) {
                return false;
            }

            String reason = "";
            if (BukkitUtil.getOnlinePlayers().size() < 5) {
                sender.sendMessage(PREFIX + "Es sind zu wenige Leute für einen Votekick online!");
                return true;
            }
            if (args.length > 2) {
                int i = 0;
                for (String arg : args) {
                    i++;
                    if (i < 2) {
                        continue;
                    }
                    if (reason.isEmpty()) {
                        reason = arg;
                        continue;
                    }
                    reason = reason + ' ' + arg;
                }
            }
            if (voteStarted) {
                sender.sendMessage(PREFIX + "Du kannst nicht einen Votekick starten während ein anderer Votekick läuft!");
                return true;
            }
            boolean voteable = true;
            SinkUser user = SinkLibrary.getUser(sender);
            if (!user.hasPermission("sinkcommands.votekick.staff")) {
                int i = 0;
                for (Player p : BukkitUtil.getOnlinePlayers()) {
                    SinkUser onlinePlayer = SinkLibrary.getUser(p);
                    if (!onlinePlayer.hasPermission("sinkcommands.votekick.staff")) {
                        i++;
                        break;
                    }
                }
                if (i != BukkitUtil.getOnlinePlayers().size()) {
                    voteable = false;
                }
            }

            if (!voteable) {
                sender.sendMessage(PREFIX + "Du kannst nicht einen Votekick starten wenn ein Teammitglied online ist!");
                return true;
            }

            targetPlayer = (BukkitUtil.getPlayer(args[0]));
            SinkUser targetUser = SinkLibrary.getUser(targetPlayer);
            target = targetUser.getDisplayName();
            if (targetPlayer.equals(sender)) {
                sender.sendMessage(PREFIX + "Du kannst nicht einen Votekick gegen dich selbst starten!");
                return true;
            }

            if (!user.hasPermission("sinkcommands.votekick.bypass")) {
                sender.sendMessage(PREFIX + "Du kannst nicht einen Votekick gegen diese Person starten!");
                return true;
            }

            if (targetUser.hasPermission("sinkcommands.votekick.bypass")) {
                sender.sendMessage(PREFIX + "Du kannst nicht einen Votekick gegen diese Person starten!");
                return true;
            }

            if (target == null) {
                sender.sendMessage(PREFIX + args[0] + " ist nicht online!");
                return true;
            }

            if (reason.isEmpty()) {
                BukkitUtil.broadcast(PREFIX + ChatColor.RED + BukkitUtil.getSenderName(sender) + ChatColor.RED + " hat einen Votekick gegen " + target
                                     + ChatColor.RED + " gestartet. Nutze " + COMMAND_PREFIX + "voteyes oder " + COMMAND_PREFIX
                                     + "voteno um zu voten und " + COMMAND_PREFIX + "votestatus für den Vote Status!", "sinkcommands.votekick.vote",
                                     true);
            } else {
                BukkitUtil.broadcast(PREFIX + ChatColor.RED + BukkitUtil.getSenderName(sender) + ChatColor.RED + " hat einen Votekick gegen " + target
                                     + ChatColor.RED + " gestartet. Grund: " + reason + ChatColor.RED + ". Nutze " + COMMAND_PREFIX + "voteyes oder "
                                     + COMMAND_PREFIX + COMMAND_PREFIX + "voteno um zu voten und " + COMMAND_PREFIX
                                     + "votestatus für den Vote Status!", "sinkcommands.votekick.vote", true);
            }

            voteStarted = true;
            long time = 20 * 90; // 20 Ticks (= 1 second) * 90 = 1,5 Minutes

            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    VotekickCommands.endVoteKick(plugin);
                }
            }, time);
            return true;
        }
    }

    public static class VoteyesCommand implements CommandExecutor {

        Plugin plugin;

        public VoteyesCommand(Plugin plugin) {
            this.plugin = plugin;
        }

        @Override
        public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
            return vote(sender, true, plugin);
        }
    }

    public static class VotenoCommand implements CommandExecutor {

        Plugin plugin;

        public VotenoCommand(Plugin plugin) {
            this.plugin = plugin;
        }

        @Override
        public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
            return vote(sender, false, plugin);
        }
    }

    public static class VotestatusCommand implements CommandExecutor {

        @Override
        public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
            sendStatus(sender);
            return true;
        }
    }

    public static class EndvoteCommand implements CommandExecutor {

        Plugin plugin;

        public EndvoteCommand(Plugin plugin) {
            this.plugin = plugin;
        }

        @Override
        public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
            BukkitUtil.broadcast(PREFIX + ChatColor.RED + "Der Votekick gegen " + target + ChatColor.RED + " wurde durch " + BukkitUtil
                    .getSenderName(commandSender) + ChatColor.RED + " beendet.", "sinkcommands.votekick.vote", true);
            voteStarted = false;
            endVoteKick(plugin);
            return true;
        }
    }

    public static class VotekickunbanCommand implements CommandExecutor {

        @Override
        public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
            if (args.length != 1) {
                return false;
            }
            String username = args[0];
            if (!SinkLibrary.tmpBannedPlayers.contains(username)) {
                sender.sendMessage(PREFIX + ChatColor.BLUE + username + ChatColor.RESET + " wurde nicht durch einen Votekick gebannt!");
                return true;
            }
            SinkLibrary.removeTempBan(username);
            sender.sendMessage(PREFIX + ChatColor.BLUE + username + ChatColor.RESET + " wurde entbannt.");
            return true;
        }
    }
}
