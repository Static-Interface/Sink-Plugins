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
import java.util.logging.Level;

public class ScriptChatListener implements Listener
{
    public HashMap<String, GroovyShell> playerData = new HashMap<>(); // PlayerName - Instance
    public HashMap<String, String> codeData = new HashMap<>(); // PlayerName - Code

    Plugin plugin;

    public ScriptChatListener(Plugin plugin) {this.plugin = plugin;}

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void handleChatScript(final AsyncPlayerChatEvent event)
    {
        final User user = SinkLibrary.getUser(event.getPlayer());
        if ( !ScriptCommand.isEnabled(user) ) return;
        event.setCancelled(true);
        Bukkit.getScheduler().runTask(plugin, new Runnable()
        {
            String currentCode = ChatColor.stripColor(event.getMessage());
            String codeWithPrev = null;
            String nl = System.getProperty("line.separator");

            public void run()
            {
                String name = event.getPlayer().getName();
                GroovyShell shellInstance;

                if ( !playerData.containsKey(name) )
                {
                    SinkLibrary.getCustomLogger().log(Level.INFO, "Initializing ShellInstance for " + user.getName());
                    shellInstance = new GroovyShell();
                    shellInstance.setVariable("me", user);
                    shellInstance.setVariable("plugin", plugin);
                    playerData.put(name, shellInstance);
                }
                else shellInstance = playerData.get(event.getPlayer().getName());

                if ( !codeData.containsKey(name) )
                {

                    String defaultImports = "import de.static_interface.sinklibrary.*;" + nl +
                            "import de.static_interface.sinklibrary.*" + nl +
                            "import org.bukkit.*" + nl;

                    currentCode = defaultImports + currentCode;
                    codeData.put(name, currentCode);
                    codeWithPrev = currentCode;
                }
                else
                {
                    String prevCode = codeData.get(name);
                    codeWithPrev = prevCode + nl + currentCode;
                    codeData.put(name, codeWithPrev);
                }

                String[] args = event.getMessage().split(" ");
                String mode = args[0].toLowerCase();
                switch ( mode )
                {
                    default:
                        user.sendMessage(ChatColor.DARK_GREEN + "Input: " + ChatColor.WHITE + currentCode);
                        try
                        {
                            String result = String.valueOf(shellInstance.evaluate(codeWithPrev));
                            if ( result != null )
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

    private void sendErrorMessage(User user, String message)
    {
        user.sendMessage(ChatColor.DARK_RED + "Exception: " + ChatColor.RED + message);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        String name = event.getPlayer().getName();
        ScriptCommand.disable(SinkLibrary.getUser(event.getPlayer()));
        if ( playerData.containsKey(name) )
        {
            playerData.get(name).getClassLoader().clearCache();
            playerData.remove(name);
        }
    }
}
