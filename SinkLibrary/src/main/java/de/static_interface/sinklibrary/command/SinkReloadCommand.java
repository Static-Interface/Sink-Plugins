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

package de.static_interface.sinklibrary.command;

import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.SinkUser;
import de.static_interface.sinklibrary.configuration.LanguageConfiguration;
import de.static_interface.sinklibrary.util.BukkitUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class SinkReloadCommand extends Command {
    //Todo: fix exceptions on reload

    private static String pluginName = SinkLibrary.getInstance().getPluginName();
    public static final String PREFIX = ChatColor.DARK_GREEN + "[" + pluginName + "] " + ChatColor.RESET;

    public SinkReloadCommand(Plugin plugin) {
        super(plugin);
    }

    @Override
    public boolean isIrcOpOnly() {
        return true;
    }

    @Override
    public boolean onExecute(CommandSender sender, String label, String[] args) {
        String name;
        LanguageConfiguration.getInstance().reload();
        name = LanguageConfiguration.getInstance().getFile().getName();

        sender.sendMessage(PREFIX + "Reloaded " + name);

        SinkLibrary.getInstance().getSettings().reload();
        name = SinkLibrary.getInstance().getSettings().getFile().getName();
        sender.sendMessage(PREFIX + "Reloaded " + name);

        sender.sendMessage(PREFIX + "Reloading PlayerConfigurations...");
        for (Player p : BukkitUtil.getOnlinePlayers()) {
            SinkUser user = SinkLibrary.getInstance().getUser(p);
            user.getPlayerConfiguration().reload();
        }

        sender.sendMessage(PREFIX + "Reloading Plugins...");
        for (Plugin p : SinkLibrary.getInstance().getRegisteredPlugins()) {
            p.onDisable();
            p.onEnable();
        }

        sender.sendMessage(PREFIX + ChatColor.GREEN + "Done");
        return true;
    }
}
