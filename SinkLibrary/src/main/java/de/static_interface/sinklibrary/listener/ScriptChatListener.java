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

package de.static_interface.sinklibrary.listener;

import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.User;
import de.static_interface.sinklibrary.commands.ScriptCommand;
import groovy.lang.GroovyShell;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;

public class ScriptChatListener implements Listener
{
    public HashMap<String, GroovyShell> playerData = new HashMap<>();

    Plugin plugin;

    public ScriptChatListener(Plugin plugin) {this.plugin = plugin;}

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void handleChatScript(final AsyncPlayerChatEvent event)
    {
        Bukkit.getScheduler().runTask(plugin, new Runnable()
        {
            public void run()
            {
                User user = SinkLibrary.getUser(event.getPlayer());

                if ( !ScriptCommand.isEnabled(user) ) return;
                event.setCancelled(true);
                String name = event.getPlayer().getName();
                GroovyShell shellInstance;

                if ( !playerData.containsKey(name) )
                {
                    shellInstance = new GroovyShell();
                    shellInstance.setVariable("me", user);
                    shellInstance.setVariable("server", Bukkit.getServer());
                    playerData.put(name, shellInstance);
                }
                else shellInstance = playerData.get(event.getPlayer().getName());

                String[] args = event.getMessage().split(" ");
                if ( args.length < 1 )
                {
                    sendHelp(user);
                    return;
                }

                String mode = args[0].toLowerCase();
                switch ( mode )
                {
                    default:
                        user.sendMessage(ChatColor.DARK_GREEN + "Input: " + ChatColor.WHITE + event.getMessage());
                        try
                        {
                            String result = String.valueOf(shellInstance.evaluate(event.getMessage()));
                            if ( result == null ) result = "Success";
                            user.sendMessage(ChatColor.AQUA + "Output: " + ChatColor.GREEN + result);
                        }
                        catch ( Exception e )
                        {
                            sendErrorMessage(user, e.getMessage());
                        }
                        break;
                }
            }
        });
    }

    private void sendHelp(User user)
    {
        user.sendMessage(ChatColor.DARK_RED + "Error: " + ChatColor.RED + "Too few arguments!");
    }

    private void sendErrorMessage(User user, String message)
    {
        user.sendMessage(ChatColor.RED + "Exception: " + message);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        String name = event.getPlayer().getName();
        ScriptCommand.disable(SinkLibrary.getUser(event.getPlayer()));
        if ( playerData.containsKey(name) )
        {
            playerData.remove(name);
        }
    }
}
