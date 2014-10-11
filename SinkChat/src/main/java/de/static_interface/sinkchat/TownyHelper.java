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

package de.static_interface.sinkchat;

import static de.static_interface.sinklibrary.configuration.LanguageConfiguration.m;

import com.palmergames.bukkit.towny.TownySettings;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.user.IngameUser;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TownyHelper {

    public static String getFormattedTownName(Town town, boolean onlyCapital) {
        if (town.isCapital()) {
            return TownySettings.getCapitalPrefix(town) + town.getName().replaceAll("_", " ") + TownySettings.getCapitalPostfix(town);
        } else if (onlyCapital) {
            return town.getName().replaceAll("_", " ");
        }
        return TownySettings.getTownPrefix(town) + town.getName().replaceAll("_", " ") + TownySettings.getTownPostfix(town);
    }

    /**
     * Get the formatted name of a resident
     *
     * @param resident          Resident whichs name will be formatted
     * @param includeTownRank   Include Town Ranks when formatting
     * @param includeNationRank Include Nation Ranks when formatting
     * @return Formatted name of the resident
     */
    public static String getFormattedResidentName(Resident resident, boolean includeTownRank, boolean includeNationRank) {
        IngameUser user = SinkLibrary.getInstance().getIngameUser(resident.getName()); // TODO: GET UUID FROM NAME

        String color = user.getChatPrefix().replace(ChatColor.stripColor(user.getChatPrefix()), ""); //very bad

        String prefixName = color + user.getName();

        String nationRank;
        String townRank;

        try {
            nationRank = resident.getNationRanks().get(0);

            if (nationRank.equals("assistant")) {
                nationRank = "Assistent";
            }
            if (nationRank.equals("helper")) {
                nationRank = "Helfer";
            }

            if (!includeNationRank) {
                nationRank = null;
            }
        } catch (Exception ignored) {
            nationRank = null;
        }

        try {
            townRank = resident.getTownRanks().get(0);
            if (townRank.equals("assistant")) {
                townRank = "Assistent";
            }
            if (townRank.equals("vip")) {
                townRank = "VIP";
            }
            if (!includeTownRank) {
                townRank = null;
            }
        } catch (Exception ignored) {
            townRank = null;
        }

        if (resident.isKing()) {
            prefixName =
                    ChatColor.GOLD + TownySettings.getKingPrefix(resident) + color + resident.getName() + ChatColor.GOLD + TownySettings
                            .getKingPostfix(resident);
        } else if (nationRank != null && !nationRank.isEmpty()) {
            prefixName = ChatColor.GOLD + nationRank + ' ' + color + resident.getName();
        } else if (resident.isMayor()) {
            prefixName =
                    ChatColor.GOLD + TownySettings.getMayorPrefix(resident) + color + resident.getName() + ChatColor.GOLD + TownySettings
                            .getMayorPostfix(resident);
        } else if (townRank != null && !townRank.isEmpty()) {
            prefixName = ChatColor.GOLD + townRank + ' ' + color + resident.getName();
        }

        prefixName = prefixName.replaceAll("_", " ");

        return prefixName;
    }

    /**
     * Get resident by name
     *
     * @param name Name of the resident
     * @return null when resident not found or offline
     */
    public static Resident getResident(String name) {
        for (Resident resident : SinkChat.getTowny().getTownyUniverse().getActiveResidents()) {
            if (resident.getName().equals(name)) {
                return resident;
            }
        }
        return null;
    }

    /**
     * Get nation tag prefix of player
     * <p><b>Use {@link SinkChat#isTownyAvailable()} before using this method! Or it may throw exceptions
     * when towny is not present</b></p>
     *
     * @param player Player
     */
    public static String getNationTag(Player player) {
        Resident res = getResident(player.getName());

        if (res == null) {
            return "";
        }

        Town town;

        try {
            town = res.getTown();
        } catch (NotRegisteredException ignored) {
            return "";
        }

        if (town == null) {
            return "";
        }
        if (!town.hasNation()) {
            return "";
        }

        Nation nation;
        try {
            nation = town.getNation();
        } catch (NotRegisteredException ignored) {
            return "";
        }

        if (nation == null) {
            return "";
        }

        return m("SinkChat.Prefix.Nation", ChatColor.stripColor(nation.getTag())) + ' ' + ChatColor.RESET;
    }

    /**
     * Get nation prefix of player
     * <p><b>Use {@link SinkChat#isTownyAvailable()} before using this method! Or it may throw exceptions
     * when towny is not present</b></p>
     *
     * @param player Player
     */
    public static String getNation(Player player) {
        Resident res = getResident(player.getName());

        if (res == null) {
            return "";
        }

        Town town;

        try {
            town = res.getTown();
        } catch (NotRegisteredException ignored) {
            return "";
        }

        if (town == null) {
            return "";
        }
        if (!town.hasNation()) {
            return "";
        }

        Nation nation;
        try {
            nation = town.getNation();
        } catch (NotRegisteredException ignored) {
            return "";
        }

        if (nation == null) {
            return "";
        }

        return m("SinkChat.Prefix.Nation", ChatColor.stripColor(nation.getName())) + ' ' + ChatColor.RESET;
    }

    /**
     * Get town tag prefix of player
     * <p><b>Use {@link SinkChat#isTownyAvailable()} before using this method! Or it may throw exceptions
     * when towny is not present</b></p>
     *
     * @param player Player
     */
    public static String getTownTag(Player player) {
        Resident res = getResident(player.getName());

        if (res == null) {
            return "";
        }

        Town town;

        try {
            town = res.getTown();
        } catch (NotRegisteredException ignored) {
            return "";
        }

        if (town == null) {
            return "";
        }

        return m("SinkChat.Prefix.Town", ChatColor.stripColor(town.getTag())) + ' ' + ChatColor.RESET;
    }

    /**
     * Get town prefix of player
     * <p><b>Use {@link SinkChat#isTownyAvailable()} before using this method! Or it may throw exceptions
     * when towny is not present</b></p>
     *
     * @param player Player
     */
    public static String getTown(Player player) {
        Resident res = getResident(player.getName());

        if (res == null) {
            return "";
        }

        Town town;

        try {
            town = res.getTown();
        } catch (NotRegisteredException ignored) {
            return "";
        }

        if (town == null) {
            return "";
        }

        return m("SinkChat.Prefix.Town", ChatColor.stripColor(town.getName())) + ' ' + ChatColor.RESET;
    }
}
