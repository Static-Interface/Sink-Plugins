/*
 * Copyright (c) 2014 adventuria.eu / static-interface.de
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.static_interface.sinkantispam;

import de.static_interface.sinklibrary.BukkitUtil;
import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.User;
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
        SinkLibrary.registerPlugin(this);
        if ( !initialized )
        {
            Bukkit.getPluginManager().registerEvents(new SinkAntiSpamListener(), this);
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
        return true;
    }
}
