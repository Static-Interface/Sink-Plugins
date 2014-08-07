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

package de.static_interface.sinkcommands;

import de.static_interface.sinkcommands.commands.*;
import de.static_interface.sinkcommands.listener.*;
import de.static_interface.sinklibrary.BukkitUtil;
import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.SinkUser;
import de.static_interface.sinklibrary.configuration.PlayerConfiguration;
import de.static_interface.sinklibrary.exceptions.NotInitializedException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.*;

import java.util.logging.Level;

import static de.static_interface.sinklibrary.Constants.TICK;

public class SinkCommands extends JavaPlugin
{
    public static boolean globalmuteEnabled;

    private static boolean initialized = false;

    @Override
    public void onEnable()
    {
        if ( !checkDependencies() )
        {
            return;
        }

        LagTimer lagTimer = new LagTimer();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, lagTimer, 15000, 15000);

        if ( !initialized )
        {
            registerEvents();
            registerCommands();
            SinkLibrary.registerPlugin(this);
            initialized = true;
        }

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
        {
            @Override
            public void run()
            {
                refreshScoreboard();
            }
        }, 0, (long) (TICK * 30)); //Update every 30 seconds

    }

    private boolean checkDependencies()
    {
        Plugin sinkLibrary = Bukkit.getPluginManager().getPlugin("SinkLibrary");

        if ( sinkLibrary == null )
        {
            getLogger().log(Level.WARNING, "This Plugin requires SinkLibrary!");
            Bukkit.getPluginManager().disablePlugin(this);
            return false;
        }

        if ( !SinkLibrary.initialized )
        {
            throw new NotInitializedException();
        }
        return true;
    }

    @Override
    public void onDisable()
    {
        //for ( User user : DutyCommand.getPlayersInDuty() )
        //{
        //    user.sendMessage(DutyCommand.PREFIX + ChatColor.DARK_RED + LanguageConfiguration._("SinkDuty.Reload.ForceLeave"));
        //    DutyCommand.endDuty(user);
        //}

        SinkLibrary.getCustomLogger().info("Saving player configurations...");

        for ( Player p : BukkitUtil.getOnlinePlayers() )
        {
            SinkUser user = SinkLibrary.getUser(p);
            PlayerConfiguration config = user.getPlayerConfiguration();
            config.save();
        }
        SinkLibrary.getCustomLogger().info("Done, disabled.");
        System.gc();
    }

    /**
     * Refresh Scoreboard for all players
     */
    public static void refreshScoreboard()
    {
        refreshScoreboard(-1);
    }

    /**
     * Refresh Scoreboard for all players
     *
     * @param players Online Players
     */
    public static void refreshScoreboard(int players)
    {
        for ( Player p : BukkitUtil.getOnlinePlayers() )
        {
            refreshScoreboard(p, players);
        }
    }

    /**
     * Refresh scoreboard for specified player
     *
     * @param player Refresh scoreboard for this player
     */
    @SuppressWarnings("deprecation")
    public static void refreshScoreboard(Player player, int players)
    {
        SinkUser user = SinkLibrary.getUser(player);
        PlayerConfiguration config = user.getPlayerConfiguration();

        if ( !config.exists() )
        {
            return;
        }

        ScoreboardManager manager = Bukkit.getScoreboardManager();

        if ( !user.hasPermission("sinkcommands.stats") || !config.isStatsEnabled() )
        {
            player.setScoreboard(manager.getNewScoreboard());
            return;
        }

        Scoreboard board = manager.getNewScoreboard();
        Team team = board.registerNewTeam(player.getName());
        Objective objective = board.registerNewObjective(ChatColor.DARK_GREEN + "Statistiken", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        if ( SinkLibrary.isEconomyAvailable() )
        {
            Score money = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.DARK_GRAY + "Geld: "));
            money.setScore(new Double(user.getMoney()).intValue());
        }

        Score onlinePlayers = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.DARK_GRAY + "Online: "));

        if ( players >= 0 )
        {
            onlinePlayers.setScore(players);
        }
        else
        {
            onlinePlayers.setScore(BukkitUtil.getOnlinePlayers().size());
        }

        Score date = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.DARK_GRAY + "Gesundheit: "));
        date.setScore((int) player.getHealth());
        team.setAllowFriendlyFire(true);
        team.setCanSeeFriendlyInvisibles(false);
        player.setScoreboard(board);
    }

    private void registerEvents()
    {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new GlobalMuteListener(), this);
        pm.registerEvents(new DutyListener(), this);
        pm.registerEvents(new VotekickListener(), this);
        pm.registerEvents(new DrugDeadListener(), this);
        pm.registerEvents(new ScoreboardListener(), this);
    }

    private void registerCommands()
    {
        getCommand("drug").setExecutor(new DrugCommand());
        getCommand("milk").setExecutor(new MilkCommand());
        getCommand("teamchat").setExecutor(new TeamchatCommand());
        getCommand("newbiechat").setExecutor(new NewbiechatCommand());
        getCommand("votekick").setExecutor(new VotekickCommands.VotekickCommand(this));
        getCommand("voteyes").setExecutor(new VotekickCommands.VoteyesCommand(this));
        getCommand("voteno").setExecutor(new VotekickCommands.VotenoCommand(this));
        getCommand("votestatus").setExecutor(new VotekickCommands.VotestatusCommand());
        getCommand("endvote").setExecutor(new VotekickCommands.EndvoteCommand(this));
        getCommand("votekickunban").setExecutor(new VotekickCommands.VotekickunbanCommand());
        getCommand("rename").setExecutor(new RenameCommand());
        getCommand("clear").setExecutor(new ClearCommand());
        getCommand("enablestats").setExecutor(new StatsCommands.EnableStatsCommand());
        getCommand("disablestats").setExecutor(new StatsCommands.DisableStatsCommand());

        SinkLibrary.registerCommand("commandsver", new CommandsverCommand(this));
        SinkLibrary.registerCommand("countdown", new CountdownCommand(this));
        SinkLibrary.registerCommand("globalmute", new GlobalmuteCommand(this));
        SinkLibrary.registerCommand("lag", new LagCommand(this));
        SinkLibrary.registerCommand("raw", new RawCommands.RawCommand(this));
        SinkLibrary.registerCommand("rawuser", new RawCommands.RawUserCommand(this));
        SinkLibrary.registerCommand("gup", new GupCommand(this));
    }
}
