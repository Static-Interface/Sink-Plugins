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

package de.static_interface.sinkantispam;

import de.static_interface.sinklibrary.BukkitUtil;
import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.User;
import de.static_interface.sinklibrary.exceptions.NotInitializedException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

import static de.static_interface.sinklibrary.configuration.LanguageConfiguration._;

public class SinkAntiSpam extends JavaPlugin
{
    public static String prefix;

    private static boolean initialized = false;

    public void onEnable()
    {
        if ( !checkDependencies() )
        {
            return;
        }
        if ( !initialized )
        {
            Bukkit.getPluginManager().registerEvents(new SinkAntiSpamListener(), this);
            SinkLibrary.registerPlugin(this);
            initialized = true;
        }

        prefix = _("SinkAntiSpam.Prefix") + ' ' + ChatColor.RESET;
    }

    public void onDisable()
    {
        System.gc();
    }

    public static void warnPlayer(Player player, String reason)
    {
        String message = prefix + String.format(_("SinkAntiSpam.Warn"), player.getDisplayName(), reason);
        String permission = "sinkantispam.message";
        User user = SinkLibrary.getUser(player);
        if ( !user.hasPermission(permission) )
        {
            player.sendMessage(message);
        }
        BukkitUtil.broadcast(message, permission, false);
    }


    private boolean checkDependencies()
    {
        if ( Bukkit.getPluginManager().getPlugin("SinkLibrary") == null )
        {
            getLogger().log(Level.WARNING, "This Plugin requires SinkLibrary!");
            Bukkit.getPluginManager().disablePlugin(this);
            return false;
        }

        if ( !SinkLibrary.initialized )
        {
            throw new NotInitializedException("SinkLibrary is not initialized!");
        }
        return true;
    }
}
