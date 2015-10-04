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

package de.static_interface.sinkchat.command;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import de.static_interface.sinkchat.SinkChat;
import de.static_interface.sinkchat.TownyHelper;
import de.static_interface.sinkchat.config.ScLanguage;
import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.api.command.SinkCommand;
import de.static_interface.sinklibrary.api.command.annotation.Aliases;
import de.static_interface.sinklibrary.api.command.annotation.DefaultPermission;
import de.static_interface.sinklibrary.api.command.annotation.Description;
import de.static_interface.sinklibrary.api.command.annotation.Usage;
import de.static_interface.sinklibrary.api.configuration.Configuration;
import de.static_interface.sinklibrary.api.stream.MessageStream;
import de.static_interface.sinklibrary.user.IngameUser;
import de.static_interface.sinklibrary.util.BukkitUtil;
import de.static_interface.sinklibrary.util.StringUtil;
import org.apache.commons.cli.ParseException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Description("Chat Channel for Towny Nations")
@Aliases("tnc")
@Usage("<message>")
@DefaultPermission
public class NationChatCommand extends SinkCommand {
    public NationChatCommand(@Nonnull Plugin plugin, Configuration config) {
        super(plugin, config);
        getCommandOptions().setPlayerOnly(true);
        getCommandOptions().setMinRequiredArgs(1);
        SinkLibrary.getInstance().registerMessageStream(new MessageStream<IngameUser>("sc_nationchat") {
            @Override
            public String formatMessage(IngameUser user, String message) {
                Player player = user.getPlayer();
                Resident resident = TownyHelper.getResident(player.getName());

                if (!resident.hasTown()) {
                    player.sendMessage(ScLanguage.SC_TOWNY_NOT_IN_TOWN.format());
                    return null;
                }

                if (!resident.hasNation()) {
                    player.sendMessage(ScLanguage.SC_TOWNY_NOT_IN_NATION.format());
                    return null;
                }

                Nation nation;
                Town town;
                try {
                    town = resident.getTown();
                    nation = town.getNation();
                } catch (NotRegisteredException ignored) //Shouldn't happen...
                {
                    return null;
                }

                String prefixName = TownyHelper.getFormattedResidentName(resident, false, true);

                String townPrefix = ChatColor.GREEN + "[" + TownyHelper.getFormattedTownName(town, true) + "] ";

                return
                        ChatColor.GRAY + "[" + ChatColor.GOLD + nation.getName() + ChatColor.GRAY + "] " + townPrefix + prefixName + ChatColor.GRAY
                        + ": "
                        + ChatColor.WHITE + message.trim();
            }

            @Override
            protected boolean onSendMessage(@Nullable IngameUser user, String message, Object... args) {
                Resident resident = TownyHelper.getResident(user.getName());
                Nation nation;
                try {
                    nation = resident.getTown().getNation();
                } catch (NotRegisteredException ignored) //Shouldn't happen...
                {
                    return false;
                }

                List<Player> sendPlayers = new ArrayList<>();

                for (Resident nationResident : nation.getResidents()) {
                    if (nationResident.isNPC()) {
                        continue;
                    }
                    Player onlineResident = BukkitUtil.getPlayer(nationResident.getName());
                    if (onlineResident == null) {
                        continue;
                    }
                    sendPlayers.add(onlineResident);
                }

                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    if (!onlinePlayer.hasPermission("sinkchat.townyspy")) {
                        continue;
                    }
                    if (sendPlayers.contains(onlinePlayer)) {
                        continue;
                    }
                    sendPlayers.add(onlinePlayer);
                }

                for (Player p : sendPlayers) {
                    p.sendMessage(message);
                }

                SinkChat.getInstance().getLogger().info(message);
                return true;
            }
        });
    }

    @Override
    protected boolean onExecute(CommandSender sender, String label, String[] args) throws ParseException {
        IngameUser user = SinkLibrary.getInstance().getIngameUser((Player) sender);
        SinkLibrary.getInstance().getMessageStream("sc_nationchat").sendMessage(user, StringUtil.formatArrayToString(args, " "));
        return true;
    }
}
